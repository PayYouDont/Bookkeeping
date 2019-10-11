package com.example.bookkeeping.netty;

import com.example.bookkeeping.entity.SyncData;
import com.example.bookkeeping.util.ReflectUtil;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

public class NettyClient {
    private static Bootstrap bootstrap;
    private static ChannelFuture f;
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup ();
    public InetSocketAddress address;
    public NettyClient(InetSocketAddress address) {
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
                                SyncData data = ReflectUtil.parseToSyncData (msg.toString ());
                                save (data);
                            }));
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    private Object send(Object data) {
        try {
            f = bootstrap.connect(address).sync();
            // 传数据给服务端
            f.channel().writeAndFlush(data);
            f.channel().closeFuture().sync();
            return f.channel().attr(AttributeKey.valueOf("Attribute_key")).get();
        }catch (InterruptedException e){
            e.printStackTrace ();
        }
        return null;
    }
    public void start(){
        List<SyncData> syncDataList = new ArrayList<> ();
        SyncData data = new SyncData ();
        data.setType (0);
        send (ReflectUtil.toJson (data));
    }
    private void save(SyncData data){
        System.out.println (data);
        switch (data.getType ()){
            case 0://0:count 1:ProgressData 2:Bill
                System.out.println (data);
                break;
            case 1:
                System.out.println (data.getProgressData ());
                break;
            case 2:
                System.out.println (data.getBillData ());
                break;
        }
        if(data.getType ()==0){
            data.setType (1);
            send (ReflectUtil.toJson (data));
        }
    }
}
