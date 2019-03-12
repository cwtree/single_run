package single_run.single_run;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * Hello world!
 *
 */
public class App {

	public static void start(int port) {
		Bootstrap bs = new Bootstrap();
		EventLoopGroup group = new NioEventLoopGroup();
		bs.group(group).option(ChannelOption.SO_BROADCAST, true).option(ChannelOption.SO_RCVBUF, 1024 * 1024)
				.option(ChannelOption.SO_SNDBUF, 1024 * 1024).channel(NioDatagramChannel.class)
				.handler(new ChannelInitializer<NioDatagramChannel>() {

					@Override
					protected void initChannel(NioDatagramChannel ch) throws Exception {
						// TODO Auto-generated method stub
						ch.pipeline().addLast("bizHandler", new UdpHandler());
					}

				});
		try {
			bs.bind(port).sync();
		} catch (Exception e) {
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		// BufferedReader reader;
		start(Integer.parseInt(args[0]));
		/*
		 * DatagramSocket ds = new DatagramSocket(Integer.parseInt(args[0]));
		 * 
		 * // 创建数据接收的数据缓冲区 byte[] buf = new byte[1024]; DatagramPacket dp = new
		 * DatagramPacket(buf, 1024); long counter = 0L; //
		 * 接受来自端口1024的数据包，并存储在集装箱datagramPacket中：注意一旦服务器开启，就会自动监听3000端口，如果 没有数据，则进行阻塞
		 * while (true) { ds.receive(dp); ++counter; if (counter % 100 == 0) {
		 * System.out.println("--" + counter); } }
		 */

	}
}
