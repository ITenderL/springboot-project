package com.itender.netty.server;

import com.itender.netty.handler.WebsocketMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * @author itender
 * @date 2022/9/7 11:48
 * @desc netty server netty服务器配置
 */
@Slf4j
@Component
public class NettyServer {

    @Value("${netty.websocket.port}")
    private int port;

    @Value("${netty.websocket.ip}")
    private String ip;

    @Value("${netty.websocket.path}")
    private String path;

    @Value("${netty.websocket.max-frame-size}")
    private long maxFrameSize;

    private ApplicationContext applicationContext;

    private Channel serverChannel;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void start(ApplicationArguments args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .localAddress(new InetSocketAddress(ip, port))
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new HttpServerCodec());
                    pipeline.addLast(new ChunkedWriteHandler());
                    pipeline.addLast(new HttpObjectAggregator(65536));
                    pipeline.addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            if(msg instanceof FullHttpRequest) {
                                FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
                                String uri = fullHttpRequest.uri();
                                if (!uri.equals(path)) {
                                    // 访问的路径不是 websocket的端点地址，响应404
                                    ctx.channel().writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND))
                                            .addListener(ChannelFutureListener.CLOSE);
                                    return ;
                                }
                            }
                            super.channelRead(ctx, msg);
                        }
                    });
                    pipeline.addLast(new WebSocketServerCompressionHandler());
                    pipeline.addLast(new WebSocketServerProtocolHandler(path, null, true, maxFrameSize));
                    // 从IOC中获取到Handler
                    pipeline.addLast(applicationContext.getBean(WebsocketMessageHandler.class));
                }
            });
            Channel channel = serverBootstrap.bind(ip, port).sync().channel();
            this.serverChannel = channel;
            log.info("【NettyServer】websocket 服务启动，ip={}, port={}", this.ip, this.port);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("【NettyServer】websocket ip={}, port={}，服务启动失败！", this.ip, this.port);
            Thread.currentThread().interrupt();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void onApplicationEvent(ContextClosedEvent event) {
        if (this.serverChannel != null) {
            this.serverChannel.close();
        }
        log.info("websocket 服务停止");
    }
}
