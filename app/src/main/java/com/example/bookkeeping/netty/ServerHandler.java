package com.example.bookkeeping.netty;

import com.example.bookkeeping.entity.Bill;
import com.example.bookkeeping.entity.ProgressData;
import com.example.bookkeeping.entity.SyncData;
import com.example.bookkeeping.util.ReflectUtil;

import org.json.JSONObject;
import org.json.JSONStringer;
import org.litepal.LitePal;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<Object> {
    private int count;
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) {
        System.out.println (msg);
        SyncData data = ReflectUtil.parseToSyncData (msg.toString ());
        if(data.getCount ()==0){
            data.setCount (getCount ());
        }
        ctx.channel().writeAndFlush(ReflectUtil.toJson (data));
        ctx.close();
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("新连接加入"+ctx);
    }
    private int getCount(){
        count += LitePal.count (ProgressData.class);
        count += LitePal.count (Bill.class);
        return count;
    }
}
