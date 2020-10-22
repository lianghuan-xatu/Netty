package rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import netty.NettyClientHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyClient {
    //创建一个线程池
    private static ExecutorService executorService = Executors.newFixedThreadPool(5);
    private static NettyRpcClientHandler client;

    //使用代理模式获取代理对象
    public Object getBean(final Class<?> serviceClass,final String providerName) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{serviceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("代理被调用");
                if(client == null) {
                    initClient();
                }
                //设置要发送给服务端的信息
                client.setParams(providerName + args[0]);
                return executorService.submit(client).get(); //增强方法获取返回值
            }
        });
    }
    //初始化客户端
    public static void initClient() {

        client = new NettyRpcClientHandler();
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(nioEventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                            pipeline.addLast(client);
                        }
                    });
            System.out.println("client starting");
            ChannelFuture channelFuture = bootstrap.connect("localhost", 6666).sync();

        }catch (Exception e) {
            e.printStackTrace();
            nioEventLoopGroup.shutdownGracefully();
        }


    }



}
