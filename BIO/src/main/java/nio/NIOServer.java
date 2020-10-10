package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    public static void main(String args[]) throws IOException {

        //创建ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //绑定serversocket端口
        SocketAddress socketAddress = new InetSocketAddress(6666);
        serverSocketChannel.socket().bind(socketAddress);
        //设置非阻塞
        serverSocketChannel.configureBlocking(false);

        //创建Selector对象
        Selector selector = Selector.open();

        //将serverSocketChannel注册到Selector
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {

            if (selector.select(1000) == 0) {
                //阻塞1000ms 没有获取到关注的事件
                System.out.println("服务器无连接...");
                continue;
            }
            //如果返回的 > 0,表示已经获取到关注的事件
            // 就获取到相关的 selectionKey 集合，反向获取通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                //循环判断selectionKey
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()) {
                    //从serverSocketChannel中获取SocketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    System.out.println("channeHash:" + socketChannel.hashCode() );
                    //将socketChannel注册到Selector
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    socketChannel.register(selector, SelectionKey.OP_READ, byteBuffer);
                }
                if (selectionKey.isReadable()) {
                    //Selector中注册有发生读时间的Channel
                    //获取到对应的Channel和ByteBUffer
                    SocketChannel channel = (SocketChannel) selectionKey.channel();
                    ByteBuffer attachmentBuffer = (ByteBuffer) selectionKey.attachment();
                    channel.read(attachmentBuffer);
                    System.out.println("from 客户端：" + new String(attachmentBuffer.array()));

                }
                //手动从集合中移除当前的 selectionKey，防止重复操作
                iterator.remove();

            }
        }
    }

}
