package jason.tcpdemo.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by djf on 2017/9/18.
 * Netty接口
 */

public interface ChannelChangeListener {
    /**
     * j接收数据
     *
     * @param ctx
     * @param msg
     */
    void onChannelChangeListenerReceive(ChannelHandlerContext ctx, Object msg);

    /**
     * 发送数据
     *
     * @param resistance
     */
    void onChannelChangeListenerSend(Object resistance);

    /**
     * 连接成功
     *
     * @param ch
     */
    void onConnectActivity(ChannelHandlerContext ch);

    /**
     * 连接断开
     *
     * @param ch
     */
    void onConnectInactivity(ChannelHandlerContext ch);

    /**
     * 连接失败
     *
     * @param future
     * @param resistance
     */
    void onConnectFailed(ChannelFuture future, Object resistance);

    /**
     * 停止连接
     *
     * @param future
     */
    void onreConnectStop(ChannelFuture future);

    /**
     * 开始连接
     *
     * @param ip
     * @param port
     */
    void onStartConnecting(String ip, int port);

    /**
     * 状态监听
     *
     * @param flag
     */
    void onNettyClientStatusListen(int flag);
}
