package groupChat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GlobalEventExecutor;
import javafx.beans.property.SimpleObjectProperty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyGroupChatHandler extends SimpleChannelInboundHandler<String> {

    //定义ChannleGroup统一管理
    //GlobalEventExecutor是全局是执行器是一个单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");

    //此方法表示一旦连接建立第一个执行
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //客户端上线
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("客户机" + channel.remoteAddress() + "已上线");
        //上面的方法会遍历channelGroup中的所有channel并逐个执行
        //将此channle加入到ChannelGroup
        channelGroup.add(channel);
    }

    /**
     * 客户机断开连接，遍历通知下线消息
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //客户端离线
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("客户机" + channel.remoteAddress() + "离线");
        //上面的方法会遍历channelGroup中的所有channel并逐个执行
        //此方法执行将会ChannelGroup中的Channel自动删除
    }

    /**
     * channel处于活动状态，提示客户机已上线
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //客户端上线
        System.out.println("客户机" + ctx.channel().remoteAddress() + "上线" + dateFormat.format(new Date()));
        System.out.println("客户机数目:" + channelGroup.size());

    }

    /**
     * channel处于不活动状态，提示客户机已离线
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //客户端离离线
         System.out.println("客户机" + ctx.channel().remoteAddress() + "离线" + dateFormat.format(new Date()));

    }

    /**
     * 读取数据并进行消息转发
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        //获取本机channel
        Channel channel = ctx.channel();
        //遍历channelGroup排除自己进行消息转发
        channelGroup.forEach(item -> {
            if(item == channel) {
                //把消息发给自己
                item.writeAndFlush("已发送消息：" + msg);
            }else {
                //进行消息转发
                item.writeAndFlush("客户机：" + item.remoteAddress() + "发送消息：" +msg);

            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
