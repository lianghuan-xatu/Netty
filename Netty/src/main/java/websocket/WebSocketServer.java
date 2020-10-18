package websocket;

import http.MyChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
public class WebSocketServer {
    private final int PORT;

    public WebSocketServer(int PORT) {
        this.PORT = PORT;
    }

    public void run() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)//设置两个线程组
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //因为基于Http协议，所以要使用Http的编码解码器
                            pipeline.addLast(new HttpServerCodec())
                                    //是以块方式写，添加ChunkedWriter处理器
                                    .addLast(new ChunkedWriteHandler())
                            /**
                             * 数据在传输过程中是分裂的，在Http传输过程中当发送大量数据时会发出多次Http请求
                             * HttpObjectAggregator可以多段聚合
                             *
                             */
                            .addLast(new HttpObjectAggregator(8192));
                            /**
                             * 对于WebSocket它的数据是以帧形式传递的
                             * 可以看到WebSocketFrame下面有六个子类
                             * 浏览器请求： ws://localhost:7000/hello表示请求的uri
                             * WebSocketProtocolHandler核心功能是将http协议升级为ws协议保持长连接
                             * 从Http协议升级到Websocket协议，是通过StatusCode 101（Switching Protocols）来切换
                             *
                             */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello"))
                                    .addLast(new MyTextWebSocketHandler());

                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();////关闭通道进行事件监听

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();

        }
    }

    public static void main(String args[]){
        WebSocketServer webSocketServer = new WebSocketServer(8083);
        webSocketServer.run();
    }



}
