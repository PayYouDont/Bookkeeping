package com.example.bookkeeping.netty;

import com.example.bookkeeping.entity.Bill;
import com.example.bookkeeping.entity.ProgressData;
import com.example.bookkeeping.entity.SyncData;
import com.example.bookkeeping.util.JsonUtil;

import org.litepal.LitePal;
import org.litepal.util.LogUtil;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class NettyServer {
    // 修改为自己的主机和端口
    private final EventLoopGroup bossGroup = new NioEventLoopGroup ();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup ();
    private Channel channel;
    private int count;
    private List<ProgressData> progressDataList;
    private List<Bill> billList;
    /**
     * 启动服务
     */
    public ChannelFuture run(InetSocketAddress address) {

        ChannelFuture f = null;
        try {
            ServerBootstrap b = new ServerBootstrap ();
            b.group (bossGroup, workerGroup).channel (NioServerSocketChannel.class)
                    .childHandler (new ServerChannelInitializer ()).option (ChannelOption.SO_BACKLOG, 128)
                    .childOption (ChannelOption.SO_KEEPALIVE, true);

            f = b.bind (address).syncUninterruptibly ();
            channel = f.channel ();
        } catch (Exception e) {
            LogUtil.e ("NettyServer", e);
        } finally {
            if (f != null && f.isSuccess ()) {
                LogUtil.d ("NettyServer", "Netty server listening " + address.getHostName () + " on port " + address.getPort ()
                        + " and ready for connections...");
            } else {
                LogUtil.d ("NettyServer", "Netty server start up Error!");
            }
        }

        return f;
    }

    public void destroy() {
        System.out.println ("Shutdown Netty Server...");
        if (channel != null) {
            channel.close ();
        }
        workerGroup.shutdownGracefully ();
        bossGroup.shutdownGracefully ();
        System.out.println ("Shutdown Netty Server Success!");

    }
    private class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        public void initChannel(SocketChannel socketChannel) throws Exception {
            // 解码编码
            socketChannel.pipeline().addLast(new StringDecoder (CharsetUtil.UTF_8));
            socketChannel.pipeline().addLast(new StringEncoder (CharsetUtil.UTF_8));

            //socketChannel.pipeline().addLast(new ServerHandler(data -> setData (data)));
            socketChannel.pipeline().addLast (new SimpleChannelInboundHandler<Object> () {
                @Override
                protected void messageReceived(ChannelHandlerContext ctx, Object msg) {
                    SyncData data = JsonUtil.toBean (msg.toString (),SyncData.class);
                    setData (data);
                    System.out.println (data);
                    ctx.channel().writeAndFlush(JsonUtil.toJson (data));
                    ctx.close();
                }
            });
        }
    }

    private int getCount(){
        progressDataList = LitePal.findAll (ProgressData.class);
        billList = LitePal.findAll (Bill.class);
        count += progressDataList.size ();
        count +=billList.size ();
        return count;
    }
    private void setData(SyncData syncData){
        if(syncData.getCount ()==0){
            syncData.setCount (getCount ());
        }
        Integer index;
        switch (syncData.getType ()){
            case 0://0:开始 1:ProgressData 2:Bill 3:结束
                Map<String,Object> map = new HashMap<> ();
                map.put ("progressDataCount",progressDataList.size ());
                map.put ("billCount",billList.size ());
                syncData.setResponseData (JsonUtil.toJson (map));
                break;
            case 1:
                index = Integer.valueOf (syncData.getRequestData ());
                ProgressData progressData = progressDataList.get (index);
                syncData.setResponseData (JsonUtil.toJson (progressData));
                syncData.setProgress (syncData.getProgress ()+1);
                break;
            case 2:
                index = Integer.valueOf (syncData.getRequestData ());
                Bill bill = billList.get (index);
                syncData.setResponseData (JsonUtil.toJson (bill));
                syncData.setProgress (syncData.getProgress ()*100/syncData.getCount ());
                break;
            default:
                break;
        }

    }
}
