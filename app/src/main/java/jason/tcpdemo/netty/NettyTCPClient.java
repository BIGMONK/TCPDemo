package jason.tcpdemo.netty;


import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by djf on 2017/8/10.
 */

public class NettyTCPClient {
    private static final String TAG = "NettyTCPClient";
    private NioEventLoopGroup workGroup = new NioEventLoopGroup(4);
    private Channel channel;
    private Bootstrap bootstrap;
    private String ip;
    private int port;


    private ChannelChangeListener mChannelChangeListener;

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
         * 断开连接
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
         * @param ip
         * @param port
         */
        void onStartConnecting(String ip,int port );

    }

    public void setChannelChangeListener(ChannelChangeListener channelChangeListener) {
        mChannelChangeListener = channelChangeListener;
    }

    public void sendData(String deviceValue) throws Exception {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(deviceValue);
            if (mChannelChangeListener != null) {
                mChannelChangeListener.onChannelChangeListenerSend(deviceValue);
            }
        }
    }

    public NettyTCPClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        bootstrap = new Bootstrap();
        bootstrap
                .group(workGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline p = socketChannel.pipeline();
                        NettyTCPClientHandler clientHandler = new NettyTCPClientHandler(NettyTCPClient.this);
                        p.addLast(new IdleStateHandler(0, 0, 5));
                        p.addLast("decoder", new StringDecoder());
                        p.addLast("encoder", new StringEncoder());
                        p.addLast(clientHandler);
                        clientHandler.setChannelValueChangeListener(new NettyTCPClientHandler.ChannelValueChangeListener() {
                            @Override
                            public void onChannelValueChangeListener(ChannelHandlerContext ctx, Object msg) {
                                if (mChannelChangeListener != null) {
                                    mChannelChangeListener.onChannelChangeListenerReceive(ctx, msg);
                                }
                            }

                            @Override
                            public void onChannelActive(ChannelHandlerContext ctx) {
                                if (mChannelChangeListener != null) {
                                    mChannelChangeListener.onConnectActivity(ctx);
                                }
                            }

                            @Override
                            public void onChannelInactive(ChannelHandlerContext ctx) {
                                if (mChannelChangeListener != null) {
                                    mChannelChangeListener.onConnectInactivity(ctx);
                                }
                            }
                        });
                    }
                });
    }

    int connectTimes;

    public void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }
        connectTimes++;
        final ChannelFuture future = bootstrap.connect(ip, port);
        if (mChannelChangeListener != null) {
            mChannelChangeListener.onStartConnecting(ip,port);
        }
        Log.d(TAG, "doConnect: " + ip + ":" + port);
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture futureListener) throws Exception {
                if (futureListener.isSuccess()) {
                    channel = futureListener.channel();
                    Log.d(TAG, "operationComplete: Connect to server successfully!"
                            + channel.toString());
                } else {
                    int reTryTime = 5;
                    Log.d(TAG, "operationComplete: Failed to connect to server, try connect after " + reTryTime + "s");
                    if (mChannelChangeListener != null) {
                        mChannelChangeListener.onConnectFailed(future, connectTimes);
                    }
                    if (connectTimes < 3) {
                        futureListener.channel().eventLoop().schedule(new Runnable() {
                            @Override
                            public void run() {
                                doConnect();
                            }
                        }, reTryTime, TimeUnit.SECONDS);
                    } else {
                        connectTimes = 0;
                        if (mChannelChangeListener != null) {
                            mChannelChangeListener.onreConnectStop(future);
                        }
                    }
                }
            }
        });
    }

    public void close() {
        if (channel != null && channel.isActive()) {
            channel.close();
        }
    }
}
