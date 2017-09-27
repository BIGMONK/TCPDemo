package jason.tcpdemo.netty;


import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import jason.tcpdemo.Utils;

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
    ExecutorService exec = Executors.newCachedThreadPool();//无界线程池，可以进行自动线程回收


    private ChannelChangeListener mChannelChangeListener;
    private int reconnectTimes;
    private int reconnectDelay;


    public static final int StatusChannelNull = 1;
    public static final int StatusChannelIsNotActivity = 2;
    public static final int StatusChannelUnknown = 3;
    public static final int StatusChannelIsActivity = 4;
    public static final int StatusChannelConnecting = 5;
    public static final int StatusChannelForceReconnecting = 6;


    public void setChannelChangeListener(ChannelChangeListener channelChangeListener) {
        if (!channelChangeListener.equals(this.mChannelChangeListener)) {
            mChannelChangeListener = channelChangeListener;
            Log.d(TAG, "setChannelChangeListener: " + channelChangeListener.getClass().getSimpleName() + "注册监听");
        } else {
            Log.d(TAG, "setChannelChangeListener: " + channelChangeListener.getClass().getSimpleName() + "已经注册监听");
        }
    }


    public void sendData(final String deviceValue) {

        if (channel != null && channel.isActive()) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    channel.writeAndFlush(deviceValue);
                }
            });
            if (mChannelChangeListener != null) {
                mChannelChangeListener.onChannelChangeListenerSend(deviceValue);
            }
        } else {
            if (mChannelChangeListener != null) {
                if (channel == null) {
                    mChannelChangeListener.onNettyClientStatusListen(StatusChannelNull);
                } else if (!channel.isActive()) {
                    mChannelChangeListener.onNettyClientStatusListen(StatusChannelIsNotActivity);
                } else {
                    mChannelChangeListener.onNettyClientStatusListen(StatusChannelUnknown);
                }
            }
        }
    }

    private static NettyTCPClient mInstance;

    public static void init() {
        if (mInstance == null) {
            synchronized (NettyTCPClient.class) {
                if (mInstance == null) {
                    mInstance = new NettyTCPClient();
                }
            }
        }
    }

    public static NettyTCPClient getInstance() {
        if (mInstance == null) {
            synchronized (NettyTCPClient.class) {
                if (mInstance == null) {
                    mInstance = new NettyTCPClient();
                }
            }
        }
        return mInstance;
    }


    public NettyTCPClient() {
        bootstrap = new Bootstrap();
        bootstrap
                .group(workGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline p = socketChannel.pipeline();
                        NettyTCPClientHandler clientHandler = new NettyTCPClientHandler(NettyTCPClient.this);
                        p.addLast(new IdleStateHandler(10, 0, 0));//十秒超时
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
                                if (isAutoReconnect && !isConnecting) {//连接过程中造成的连接断开时主动行为，不必处理
                                    doConnect(isForced);
                                }
                            }
                        });
                    }
                });
    }

    int connectTimes;
   private boolean isConnecting, isForced, isAutoReconnect;


    /**
     *
     * @param ip
     * @param port
     * @param isAutoReconnect  是否自动重连
     * @param reConnectTimes
     * @param reConnectDelay
     * @param isForced  是否断开原有的连接并重连
     */
    public void connect(final String ip, final int port
            , final boolean isAutoReconnect
            , final int reConnectTimes
            , final int reConnectDelay
            , final boolean isForced) {
        if (isConnecting) {
            if (mChannelChangeListener != null) {
                mChannelChangeListener.onNettyClientStatusListen(StatusChannelConnecting);
            }
            return;
        }
        isConnecting = true;

        this.reconnectTimes = reConnectTimes;
        this.reconnectDelay = reConnectDelay;
        this.ip = ip;
        this.port = port;
        this.isAutoReconnect = isAutoReconnect;
        this.reconnectDelay = reConnectDelay;
        this.isForced = isForced;
        exec.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    doConnect(isForced);
                }
            }
        });
    }

    private void doConnect(final boolean isForced) {
        this.isForced = isForced;
        if (channel == null) {
            if (mChannelChangeListener != null) {
                mChannelChangeListener.onNettyClientStatusListen(StatusChannelNull);
            }
        }
        if (channel != null && channel.isActive()) {
            if (mChannelChangeListener != null) {
                mChannelChangeListener.onNettyClientStatusListen(StatusChannelIsActivity);
            }
            if (!isForced) {
                return;
            } else {
                channel.close();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mChannelChangeListener.onNettyClientStatusListen(StatusChannelForceReconnecting);
            }
        }
        if (mChannelChangeListener != null) {
            mChannelChangeListener.onStartConnecting(ip, port);
        }
        connectTimes++;
        final ChannelFuture future = bootstrap.connect(ip, port);
        Log.d(TAG, "doConnect: " + ip + ":" + port);
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture futureListener) throws Exception {
                if (futureListener.isSuccess()) {
                    channel = futureListener.channel();
                    connectTimes = 0;
                    isConnecting = false;
                    Log.d(TAG, "operationComplete: Connect to server successfully!"
                            + channel.toString());
                } else {
                    Log.d(TAG, "operationComplete: Failed to connect to server, try connect after " + reconnectDelay + "s");
                    if (mChannelChangeListener != null) {
                        mChannelChangeListener.onConnectFailed(future, connectTimes);
                    }
                    if (connectTimes < reconnectTimes + 1) {
                        futureListener.channel().eventLoop().schedule(new Runnable() {
                            @Override
                            public void run() {
                                doConnect(isForced);
                            }
                        }, reconnectDelay, TimeUnit.SECONDS);
                    } else {
                        connectTimes = 0;
                        if (mChannelChangeListener != null) {
                            mChannelChangeListener.onreConnectStop(future);
                            isConnecting = false;
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
