package jason.tcpdemo.netty;


import android.util.Log;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Description:
 * Author:Giousa
 * Date:2017/2/9
 * Email:65489469@qq.com
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private static final String TAG = "ClientHandler";
    private NettyClient client;

    public ClientHandler(NettyClient client) {
        this.client = client;
    }

    public interface ChannelValueChangeListener {
        void onChannelValueChangeListener(Object resistance);
    }

    private ChannelValueChangeListener mChannelValueChangeListener;

    public void setChannelValueChangeListener(ChannelValueChangeListener channelValueChangeListener) {
        mChannelValueChangeListener = channelValueChangeListener;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Log.e(TAG, "channelInactive: " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Log.e(TAG, "channelActive: " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        Log.e(TAG, "channelRead: " + ctx.channel().remoteAddress() + "  " + msg.toString());
        if (mChannelValueChangeListener != null) {
            mChannelValueChangeListener.onChannelValueChangeListener(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        Log.e(TAG, "channelReadComplete: " + ctx.channel().remoteAddress());

    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
        Log.e(TAG, "channelWritabilityChanged: " + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Log.e(TAG, "exceptionCaught: " + ctx.channel().remoteAddress());

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        Log.e(TAG, "handleReaderIdle:READER_IDLE: " + ctx.channel().remoteAddress());
    }

    protected void handleWriterIdle(ChannelHandlerContext ctx) {
        Log.e(TAG, "handleReaderIdle:WRITER_IDLE: " + ctx.channel().remoteAddress());
    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {
        Log.e(TAG, "handleReaderIdle:ALL_IDLE: " + ctx.channel().remoteAddress());
    }
}