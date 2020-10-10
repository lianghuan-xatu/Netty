package bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOServer {
    public static void main(String args[]) throws IOException {
        //创建一个线程池
        //如果客户端有请求连接，则创建一个线程与之通讯
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("服务器已启动");

        //等待客户端连接
        while (true) {

            System.out.println("线程信息：id= "+ Thread.currentThread().getId() + "; 线程名字：" + Thread.currentThread().getName());
            //监听，等待客户端连接
            System.out.println("等待连接");
            final Socket socket = serverSocket.accept();
            System.out.println("连接到一个客户端");

            //开辟新线程通讯
            newCachedThreadPool.execute(new Runnable() {
                public void run() {
                    try {
                        handler(socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }



    }

    private static void handler(Socket socket) throws IOException {


        try {
            System.out.println("线程信息：id= "+ Thread.currentThread().getId() + "; 线程名字：" + Thread.currentThread().getName());
            byte[] bytes = new byte[1024];
            //通过Socket获取输入流
            InputStream inputStream = socket.getInputStream();
            while (true) {
                System.out.println("线程信息：id= "+ Thread.currentThread().getId() + "; 线程名字：" + Thread.currentThread().getName());
                System.out.println("read....");
                //循环读取数据
                int read = inputStream.read(bytes);
                if(read != -1) {
                    String s = new String(bytes, 0, read);
                    System.out.println(s);
                }else {
                    break;
                }
        }

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            System.out.println("关闭和client的连接");
            try {
                socket.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
