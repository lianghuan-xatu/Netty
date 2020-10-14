package http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class MyHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            //Http无状态，每次传输完成即断开
            // 每次浏览器刷新，都是新的Channel、pipeline

            // 根据uri进行过滤
            HttpRequest request = (HttpRequest) msg;
            URI uri = new URI(request.uri());
            if ("/favicon.ico".equals(uri.getPath())) {
                System.out.println("浏览器请求了favicon，不做响应");
                return;
            }
        }
        System.out.println("--------------------------");
        System.out.println(msg);
        System.out.println(ctx.channel().remoteAddress());
        // 返回信息
        ByteBuf content = Unpooled.copiedBuffer("Hello,World", CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,content);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());
        ctx.writeAndFlush(response);

    }

}
