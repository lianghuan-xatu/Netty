package heartBeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.omg.CORBA.NO_IMPLEMENT;

import java.util.concurrent.TimeUnit;

public class HeartBeatServer {
    private final int PORT;

    public HeartBeatServer(int port) {
        PORT = port;
    }
    public void run() {
        //创建事件循环线程组
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            /**
                             * Netty内部提供IdleStateHandler检测空闲状态处理器
                             *     private final long readerIdleTimeNanos;//表示空闲时间无读操作就会发送心跳检测
                             *     private final long writerIdleTimeNanos;//表示空闲时间无写操作将会发送心跳检测
                             *     private final long allIdleTimeNanos;//表示空闲时间无读写操作就会发送心跳检测
                             *     当 IdleStateEvent 触发后，就会传递给管道的下一个 Handler，通过调用（触发）下一个Handler的 userEventTriggered，在该方法区处理这个事件。
                             *
                             */
                            pipeline.addLast(new IdleStateHandler(3,5,7, TimeUnit.SECONDS))
                                    .addLast(new MyHeartBeatHandler());//加入自定义handler对stateEvent作出处理
                        }
                    });

            System.out.println("启动服务器...");
            ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();//设置同步模式
            //关闭通道进行事件监听
            channelFuture.channel().closeFuture().sync();

        }catch (Exception e) {
            e.printStackTrace();
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
