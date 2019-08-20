package org.guirsouza;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyServer {

    private int port;
    private ServerBootstrap bootstrap;
    private ChannelFuture channelFuture;
    private LoggingHandler loggingHandler;

    public NettyServer(int port) {
        this.port = port;
        this.loggingHandler = new LoggingHandler(NettyServer.class + "-");
    }

    public void start() throws InterruptedException {
        EventLoopGroup masterGroup = new NioEventLoopGroup();
        EventLoopGroup slaveGroup = new NioEventLoopGroup();

        try {
            bootstrap = new ServerBootstrap();
            bootstrap.group(masterGroup, slaveGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.handler(loggingHandler);
            bootstrap.localAddress(new InetSocketAddress("192.168.0.9", 10000));
            bootstrap.option(ChannelOption.SO_BACKLOG, 128)
                     .childOption(ChannelOption.SO_KEEPALIVE, true);

            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast("idleStateHandler", new IdleStateHandler(0, 0, 10, TimeUnit.SECONDS));
                    socketChannel.pipeline().addLast("channelInboundHandler", new NettyChannelInboundHandlerAdapter());
                }
            });
            
            channelFuture = bootstrap.bind(new InetSocketAddress("192.168.0.9", 10000)).sync();
            channelFuture.channel().closeFuture().addListener(__ -> {
				try {
					System.out.println("Close future activated");
                    masterGroup.shutdownGracefully();
                    slaveGroup.shutdownGracefully();
				} catch (Exception e) {
					System.out.println("Closing EventLoopGroups:\n" + e);
				}
            });

            ChannelMapper.getInstance().add(channelFuture.channel());
        } catch(Exception e) {
            e.printStackTrace();;
        }
    }

}