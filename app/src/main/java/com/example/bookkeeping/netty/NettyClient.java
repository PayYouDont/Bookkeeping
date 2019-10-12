package com.example.bookkeeping.netty;

import android.os.Handler;
import android.os.Message;

import com.example.bookkeeping.entity.Bill;
import com.example.bookkeeping.entity.ProgressData;
import com.example.bookkeeping.entity.SyncData;
import com.example.bookkeeping.ui.SettingFragment;
import com.example.bookkeeping.util.JsonUtil;
import com.example.bookkeeping.util.ReflectUtil;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ezy.boost.update.OnDownloadListener;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import lombok.Setter;

public class NettyClient {
    private static Bootstrap bootstrap;
    private static ChannelFuture f;
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup ();
    public InetSocketAddress address;
    @Setter
    public Handler handler;
    private Map<String,Object> map;
    private int progressDataCount;
    private int billCount;
    private boolean isEnd = false;
    public NettyClient(InetSocketAddress address, Handler handler) {
        this.address = address;
        try {
            bootstrap = new Bootstrap ();
            bootstrap.group (workerGroup)
                    .channel (NioSocketChannel.class)
                    .option (ChannelOption.SO_KEEPALIVE, true)
                    .handler (new ChannelInitializer<SocketChannel> () {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            // 解码编码
                            socketChannel.pipeline ().addLast (new StringDecoder (CharsetUtil.UTF_8));
                            socketChannel.pipeline ().addLast (new StringEncoder (CharsetUtil.UTF_8));
                            socketChannel.pipeline ().addLast (new ClientHandler (msg -> {
                                SyncData data = JsonUtil.toBean (msg.toString (),SyncData.class);
                                save (data);
                            }));
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace ();
        }
        this.handler = handler;
    }
    private Object send(Object data) throws ConnectException,InterruptedException{
        f = bootstrap.connect(address).sync();
        // 传数据给服务端
        f.channel().writeAndFlush(data);
        f.channel().closeFuture().sync();
        return f.channel().attr(AttributeKey.valueOf("Attribute_key")).get();
    }
    public void start() throws Exception{
        SyncData data = new SyncData ();
        data.setType (0);
        send (JsonUtil.toJson (data));
    }
    private void save(SyncData data){
        switch (data.getType ()){
            case 0://0:count 1:ProgressData 2:Bill
                map = data.getCountData ();
                progressDataCount = Integer.valueOf (map.get ("progressDataCount").toString ());
                billCount = Integer.valueOf (map.get ("billCount").toString ());
                if(progressDataCount>0){
                    data.setType (1);
                }else if(billCount>0){
                    data.setType (2);
                }else{
                    isEnd = true;
                }
                data.setRequestData ("0");
                if(handler!=null){
                    Message message = new Message ();
                    message.what = SettingFragment.START;
                    handler.sendMessage (message);
                }
                break;
            case 1:
                ProgressData progressData = data.getProgressData ();
                progressData.save ();
                Integer index = Integer.valueOf (data.getRequestData ());
                index++;
                if(index<progressDataCount){
                    data.setRequestData (""+index);
                }else if(billCount>0){
                    data.setType (2);
                    data.setRequestData ("0");
                }else {
                    isEnd = true;
                }
                break;
            case 2:
                Bill bill = data.getBillData ();
                bill.save ();
                index = Integer.valueOf (data.getRequestData ());
                index++;
                if(index<billCount){
                    data.setRequestData (""+index);
                }else {
                    isEnd = true;
                }
                break;
        }
        try {
            if(!isEnd){
                if(handler!=null){
                    //downloadListener.onProgress (data.getProgress ());
                    Message message = new Message ();
                    message.what = SettingFragment.PROGRESS;
                    message.obj = data.getProgress ();
                    handler.sendMessage (message);
                }
                send (JsonUtil.toJson (data));
            }else if(handler!=null){
                //downloadListener.onFinish ();
                Message message = new Message ();
                message.what = SettingFragment.FINISH;
                handler.sendMessage (message);
            }
        }catch (Exception e){
            e.printStackTrace ();
        }
    }
}
