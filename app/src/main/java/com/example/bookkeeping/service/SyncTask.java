package com.example.bookkeeping.service;

import com.example.bookkeeping.netty.NettyServer;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelFuture;
import lombok.Getter;

public class SyncTask extends Thread {
    @Getter
    private NettyServer nettyServer;
    private InetSocketAddress address;

    public SyncTask(InetSocketAddress address) {
        this.address = address;
        nettyServer = new NettyServer ();
        new Thread (() -> {});
    }

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                nettyServer.destroy();
            }
        });
        ChannelFuture future = nettyServer.run(address);
        future.channel().closeFuture().syncUninterruptibly();
    }
}
