package jason.tcpdemo.netty;


import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import jason.tcpdemo.netty.code.MsgPackDecode;
import jason.tcpdemo.netty.code.MsgPackEncode;

/**
 * Created by djf on 2017/8/10.
 */

public class NettyClient {
    private static final String TAG = "NettyClient";
    private NioEventLoopGroup workGroup = new NioEventLoopGroup(4);
    private Channel channel;
    private Bootstrap bootstrap;
    private String ip;
    private int port;


    private ChannelChangeListener mChannelChangeListener;
    public interface ChannelChangeListener{
        void onChannelChangeListener(Object resistance);
    }

    public void setChannelChangeListener(ChannelChangeListener channelChangeListener) {
        mChannelChangeListener = channelChangeListener;
    }

    public void sendData(String deviceValue) throws Exception {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(deviceValue);
        }
    }
    public NettyClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        bootstrap = new Bootstrap();
        bootstrap
                .group(workGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline p = socketChannel.pipeline();
                        ClientHandler clientHandler = new ClientHandler(NettyClient.this);
                        p.addLast(new IdleStateHandler(0, 0, 5));
                        p.addLast(new MsgPackDecode());
                        p.addLast(new MsgPackEncode());
                        p.addLast(clientHandler);
                        clientHandler.setChannelValueChangeListener(new ClientHandler.ChannelValueChangeListener() {
                            @Override
                            public void onChannelValueChangeListener(Object res) {
                                if(mChannelChangeListener != null){
                                    mChannelChangeListener.onChannelChangeListener(res);
                                }
                            }
                        });
                    }
                });
    }
    public void doConnect() {
        Log.e(TAG, "doConnect: " );
        if (channel != null && channel.isActive()) {
            return;
        }
        ChannelFuture future = bootstrap.connect(ip, port);
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture futureListener) throws Exception {
                if (futureListener.isSuccess()) {
                    channel = futureListener.channel();
                    Log.e(TAG, "operationComplete: Connect to server successfully!");
                } else {
                    Log.e(TAG, "operationComplete: Failed to connect to server, try connect after 10s");
                    futureListener.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            doConnect();
                        }
                    }, 2, TimeUnit.SECONDS);
                }
            }
        });
    }

}
