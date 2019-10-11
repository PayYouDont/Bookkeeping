package com.example.bookkeeping.netty;

import org.litepal.util.LogUtil;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class NettyServer {
    // 修改为自己的主机和端口
    private static String HOST;
    private static final int PORT = 8800;
    private final EventLoopGroup bossGroup = new NioEventLoopGroup ();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup ();
    private Channel channel;

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
        LogUtil.d ("NettyServer", "Shutdown Netty Server...");
        if (channel != null) {
            channel.close ();
        }
        workerGroup.shutdownGracefully ();
        bossGroup.shutdownGracefully ();
        LogUtil.d ("NettyServer", "Shutdown Netty Server Success!");
    }
    private class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        public void initChannel(SocketChannel socketChannel) throws Exception {
            // 解码编码
            socketChannel.pipeline().addLast(new StringDecoder (CharsetUtil.UTF_8));
            socketChannel.pipeline().addLast(new StringEncoder (CharsetUtil.UTF_8));

            socketChannel.pipeline().addLast(new ServerHandler());
        }
    }
    

}
