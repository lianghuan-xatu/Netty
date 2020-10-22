
**I happened to see akka, a very powerful distributed framework with high concurrency, so I wanted to learn about it. I found that he and netty are inextricably linked. Akka has an abstraction for IO operations, which is the same as netty. Akka can be used to create computing clusters, and actors pass messages between different machines. From this perspective, akka is a higher level of abstraction than netty. But akka wrote it in scala. It's not very familiar. I'm afraid I'll be dizzy.
However, I found that my netty is in the state of half knowing and half understanding. I have not seen the source code, understood the architecture, and written deep code.
The learning process is mainly around the gitbook essential netty in action (essence). Here are just some notes for easy memory and recall. At the same time, provide some summary, or provide something that the original book does not have.**



![此处输入图片的描述][1]


  [1]: https://netty.io/images/components.png
  
   **Netty is an asynchronous event-driven network application framework for rapid development of maintainable high performance protocol servers & clients.** 
   
   
   
  Netty is a NIO client server framework which enables quick and easy development of network applications such as protocol servers and clients. It greatly simplifies and streamlines network programming such as TCP and UDP socket server.

'Quick and easy' doesn't mean that a resulting application will suffer from a maintainability or a performance issue. Netty has been designed carefully with the experiences earned from the implementation of a lot of protocols such as FTP, SMTP, HTTP, and various binary and text-based legacy protocols. As a result, Netty has succeeded to find a way to achieve ease of development, performance, stability, and flexibility without a compromise.
