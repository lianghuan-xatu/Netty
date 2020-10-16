package groupChat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import jdk.internal.org.objectweb.asm.commons.SerialVersionUIDAdder;

import java.util.Scanner;


public class NettyGroupChatClient {
    private final String HOST;
    private final int PORT;

    public NettyGroupChatClient(String HOST, int PORT) {
        this.HOST = HOST;
        this.PORT = PORT;
    }
    public void run() {
        //创建事件循环线程组
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("decoder",new StringDecoder())
                                    .addLast("encoder",new StringEncoder())
                                    .addLast(new MyGroupChatClientHandler());
                        }
                    });
            System.out.println("客户端启动...");
            ChannelFuture channelFuture = bootstrap.connect(HOST, PORT);
            Channel channel = channelFuture.channel();
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String message = scanner.nextLine();
                channel.writeAndFlush(message);

            }
        }catch (Exception e) {
            e.printStackTrace();
            eventExecutors.shutdownGracefully();
        }

    }

    public static void main(String args[]) {
        NettyGroupChatClient nettyGroupChatClient = new NettyGroupChatClient("127.0.0.1",6666);
        nettyGroupChatClient.run();
    }
}
