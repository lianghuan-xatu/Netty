package groupChat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import javax.swing.*;

public class NettGroupChatServer {
    private int PORT;

    public NettGroupChatServer(int PORT) {
        this.PORT = PORT;
    }

    public void run() {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(bossGroup, workGroup);
            serverBootstrap.channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("StringDecoder", new StringDecoder())
                                    .addLast("StringEncoder", new StringEncoder())
                                    .addLast(new MyGroupChatHandler());

                        }
                    });
            System.out.println("服务器启动...");
            ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();
            //关闭监听事件服务
            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();

        }


    }

    public static void main(String args[]) {
        NettGroupChatServer nettGroupChatServer = new NettGroupChatServer(6666);
        nettGroupChatServer.run();
    }

}
