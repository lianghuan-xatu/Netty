package rpc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

public class NettyRpcClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext ctx;
    private String params;//方法参数
    private String result;//方法返回结果


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("通道连接成功");
        this.ctx = ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        result = msg.toString();
        notify();//notify   wait线程
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    public void setParams(String params) {
        this.params = params;
    }

    //线程submit之后返回方法执行返回值
    //被代理对象的调用，真正发送数据给服务器，发送完后就阻塞，等待被唤醒（channelRead）
    @Override
    public synchronized Object call() throws Exception {
        ctx.writeAndFlush(params);
        //进行wait操作等服务器端回复数据将notify
        wait(); //等待 channelRead 获取到服务器的结果后，进行唤醒。
        //返回服务器结果
        return result;
    }
}
