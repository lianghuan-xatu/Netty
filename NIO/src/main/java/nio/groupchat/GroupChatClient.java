package nio.groupchat;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class GroupChatClient {

    private Selector selector;
    private SocketChannel socketChannel;
    private static String HOST = "127.0.0.1";
    private static int PORT = 6666;
    private String username;

    public GroupChatClient() throws IOException {
        //初始化
        selector = Selector.open();
        //连接服务器
        socketChannel = SocketChannel.open(new InetSocketAddress(HOST,PORT));
        //设置非阻塞模式
        socketChannel.configureBlocking(false);
        //将客户端Channel注册到Selector  关注事件为读事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        username = socketChannel.getLocalAddress().toString().substring(1);
    }

    //sendInfo
    public void sendInfo(String message) {
        try{
            message = username + ":" + message;
            ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes());
            socketChannel.write(byteBuffer);

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("消息发送失败");
        }
    }

    //读取服务端发送消息
    public void readInfo() throws IOException {
        try {
            int select = selector.select();
            if (select > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isReadable()) {
                        //有读事件需要读取
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        channel.read(byteBuffer);
                        String s = new String(byteBuffer.array());
                        System.out.println(s);

                    }

                }


            } else {
                System.out.println("无可用Channel");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String args[]) throws IOException {
        //初始化客户端
        final GroupChatClient groupChatClient = new GroupChatClient();

        //开辟新线程持续监听
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        groupChatClient.readInfo();
                        Thread.sleep(3000);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        //客户端发送消息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            groupChatClient.sendInfo(scanner.nextLine());
        }

    }

}
