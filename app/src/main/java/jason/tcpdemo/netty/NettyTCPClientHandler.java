package jason.tcpdemo.netty;


import android.util.Log;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Description:
 * Author:Giousa
 * Date:2017/2/9
 * Email:65489469@qq.com
 */
public class NettyTCPClientHandler extends ChannelInboundHandlerAdapter {
    private static final String TAG = "NettyTCPClientHandler";
    private NettyTCPClient client;

    public NettyTCPClientHandler(NettyTCPClient client) {
        this.client = client;
    }



    public interface ChannelValueChangeListener {
        void onChannelValueChangeListener(ChannelHandlerContext ctx, Object msg);
        void  onChannelActive(ChannelHandlerContext ctx);
        void  onChannelInactive(ChannelHandlerContext ctx);
    }

    private ChannelValueChangeListener mChannelValueChangeListener;

    public void setChannelValueChangeListener(ChannelValueChangeListener channelValueChangeListener) {
        mChannelValueChangeListener = channelValueChangeListener;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
//        Log.d(TAG, "channelInactive连接断开: " + ctx.channel().remoteAddress());
        if (mChannelValueChangeListener!=null){
            mChannelValueChangeListener.onChannelInactive(ctx);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
//        Log.d(TAG, "channelActive连接成功: " + ctx.channel().remoteAddress());
        if (mChannelValueChangeListener!=null){
            mChannelValueChangeListener.onChannelActive(ctx);
        }
    }

    /**
     * 读取数据
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);

//        StringBuilder sb = new StringBuilder();
//        byte[] bytes = msg.toString().getBytes();
//        for (int i = 0; i < bytes.length; i++) {
//            sb.append(bytes[i] + "  ");
//        }
//        Log.d(TAG, "channelRead: " + ctx.channel().remoteAddress()
//                + " 接收到的内容：" + msg.toString()
//                + " 内容字节码：" + sb.toString()
//        );
        if (mChannelValueChangeListener != null) {
            mChannelValueChangeListener.onChannelValueChangeListener(ctx, msg);
        }
    }

    /**
     * 读取数据结束
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        Log.d(TAG, "channelReadComplete: " + ctx.channel().remoteAddress().toString());
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
        Log.d(TAG, "channelWritabilityChanged: " + ctx.channel().remoteAddress().toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Log.d(TAG, "exceptionCaught: " + ctx.channel().remoteAddress().toString()+"   cause:"+cause.getMessage());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.超时触发
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
        Log.d(TAG, "handleReaderIdle:READER_IDLE: " + ctx.channel().remoteAddress());
    }

    protected void handleWriterIdle(ChannelHandlerContext ctx) {
        Log.d(TAG, "handleReaderIdle:WRITER_IDLE: " + ctx.channel().remoteAddress());
    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {
        Log.d(TAG, "handleReaderIdle:ALL_IDLE: " + ctx.channel().remoteAddress());
        try {
            if (this.client != null) {
                this.client.sendData(110 + "心跳");//超时心跳数据
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}