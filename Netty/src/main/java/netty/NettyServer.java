package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class NettyServer {
    public NettyServer() throws InterruptedException {

        /**
         * BossGroup和WorkGroup都是无限循环的
         * 含有的子线程数量(NioEventLoop)
         * 默认是cpu核心数量的两倍
         */
        //创建bossGroup用来监听连接事件
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        //创建workGroup用来监听IO事件
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //链式编程设置参数
            serverBootstrap.group(bossGroup, workGroup)//设置两个线程组
                    .channel(NioServerSocketChannel.class)//设置服务端通道类型
                    .option(ChannelOption.SO_BACKLOG, 128)//设置服务端线程队列连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true)//设置客户端线程连接保持连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //创建通道连接测试对象
                            ch.pipeline().addLast(new NettyServerHandler());//添加处理器


                        }
                    });//为workGroup的Eventloop设置处理器
            System.out.println("server is ready");
            //绑定端口同步生成CHannelFuture对象
            //启动服务器并绑定端口
            ChannelFuture cf = serverBootstrap.bind(6666).sync();
            //对关闭通道进行事件监听
            cf.channel().closeFuture().sync();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();

        }

    }

    public static void main(String args[]) throws InterruptedException {
        NettyServer nettyServer = new NettyServer();
        }



}
