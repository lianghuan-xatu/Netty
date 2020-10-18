package heartBeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class MyHeartBeatHandler extends SimpleChannelInboundHandler {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleState eventType = null;
        if(evt instanceof IdleStateEvent) {
            eventType = ((IdleStateEvent) evt).state();
            switch (eventType) {
                case ALL_IDLE: {
                    System.out.println("读写空闲");
                    break;
                }
                case READER_IDLE: {
                    System.out.println("读空闲");
                    break;
                }
                case WRITER_IDLE: {
                    System.out.println("写空闲");
                    break;
                }
            }
            System.out.println("客户端未响应" + ctx.channel().remoteAddress().toString());
            System.out.println("服务端做出处置...");
        }
    }
}
