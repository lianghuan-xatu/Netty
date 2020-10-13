package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.omg.CORBA.BooleanSeqHelper;

public class NettyClient {
    public NettyClient() {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(SocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(null);
                        }
                    });
            System.out.println("client is ready");
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6666).sync();
            //监听关闭事件
            channelFuture.channel().closeFuture().sync();

        }catch (Exception e){
        e.printStackTrace();
    }finally {
            eventLoopGroup.shutdownGracefully();
        }

    }
}

