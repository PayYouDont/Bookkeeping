package com.example.bookkeeping.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.Setter;

public class ClientHandler extends SimpleChannelInboundHandler<Object> {
    @Setter
    private OnMsgListener listener;

    public ClientHandler(OnMsgListener listener) {
        this.listener = listener;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) {
        System.out.println ("客户端收到的msg="+msg);
        listener.msg (msg);
        ctx.channel().attr(AttributeKey.valueOf("Attribute_key")).set(msg);
        ctx.close();
    }
    public interface OnMsgListener{
        void msg(Object msg);
    }
}
