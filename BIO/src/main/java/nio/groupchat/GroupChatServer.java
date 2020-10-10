package nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 群聊系统服务端
 */
public class GroupChatServer
{
    //定义初始化属性
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private static int PORT = 6007;

    public GroupChatServer() throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        //端口绑定
        serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
        //设置非阻塞模式
        //注册到Selector
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }
    //监听
    public void listen() throws IOException {
        while (true) {
            if(selector.select() > 0) {
                //有关注的事件
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    if(selectionKey.isAcceptable()) {
                        //有上线客户端
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        //设置非阻塞模式
                        socketChannel.configureBlocking(false);
                        //注册客户端读事件
                        socketChannel.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                        SocketAddress remoteAddress = socketChannel.getRemoteAddress();
                        System.out.println(remoteAddress + "客户机上线");
                    }
                    if(selectionKey.isReadable()) {
                        //检测到读事件
                        readData(selectionKey);

                    }
                    //删除已处理的selectionKey
                    iterator.remove();

                }

            }else {
                System.out.println("服务端无连接.....");
            }
        }
    }


    //读取数据
    public void readData(SelectionKey key) throws IOException {

        SocketChannel channel = null;
        ByteBuffer attachmentBuffer = null;
        try {
            channel = (SocketChannel) key.channel();
            //获取到缓冲区
            attachmentBuffer = (ByteBuffer) key.attachment();
            int read = channel.read(attachmentBuffer);
            //判断是否获取到数据
            if (read > 0) {
                String message = new String(attachmentBuffer.array());
                //将数据输出
                SocketAddress remoteAddress = channel.getRemoteAddress();
                System.out.println(remoteAddress + ":" + message);
                //转发讯息向其他客户机
                sendInfoToOtherClients(message,channel);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            channel.close();
        }
    }

    public void sendInfoToOtherClients(String message, Channel channel) throws IOException {
        Set<SelectionKey> keys = selector.keys();
        for (SelectionKey key : keys) {
            //获取到所有注册在Selector上的Channel
            Channel targetChannel = key.channel();
            //排除发送消息方
            if(targetChannel instanceof SocketChannel && targetChannel != channel ) {
                //将信息存储到Buffer
                ByteBuffer attachmentBuffer = ByteBuffer.wrap(message.getBytes());
                //转型
                SocketChannel dest = (SocketChannel)targetChannel;
                //将信息写入到Channel
                dest.write(attachmentBuffer);
            }
        }
    }





}
