package single_run.single_run;


import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

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
		//start(Integer.parseInt(args[0]));
	    List<ServerAddress> serverList = new ArrayList<ServerAddress>();
        //serverList.add(new ServerAddress("172.28.72.124", 27017));
	    serverList.add(new ServerAddress(args[0], Integer.parseInt(args[1])));
	    MongoClient mongoClient = null;
        MongoClientOptions.Builder build = new MongoClientOptions.Builder();
        build.connectionsPerHost(50); //与目标数据库能够建立的最大connection数量为50  
        build.heartbeatConnectTimeout(1000);//和集群的心跳连接超时的时间
        build.heartbeatFrequency(10000);//心跳频率，默认10000ms
        build.heartbeatSocketTimeout(1000);//socket连接的心跳超时时间
        build.threadsAllowedToBlockForConnectionMultiplier(50); //如果当前所有的connection都在使用中，则每个connection上可以有50个线程排队等待  
        /* 
         * 一个线程访问数据库的时候，在成功获取到一个可用数据库连接之前的最长等待时间为2分钟 
         * 这里比较危险，如果超过maxWaitTime都没有获取到这个连接的话，该线程就会抛出Exception 
         * 故这里设置的maxWaitTime应该足够大，以免由于排队线程过多造成的数据库访问失败 
         */
        build.maxWaitTime(1000 * 60 * 2);
        build.connectTimeout(1000 * 60 * 1); //与数据库建立连接的timeout设置为1分钟  

        MongoClientOptions myOptions = build.build();
        char[] pwd = "Phoenix@hy2018".toCharArray();
        MongoCredential credential = MongoCredential.createCredential("phoenix", "phoenix", pwd);
        try {
            mongoClient = new MongoClient(serverList, credential, myOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MongoDatabase db = mongoClient.getDatabase("phoenix");//获取数据库实例
        MongoCollection<Document> mc = db.getCollection("alertItem");
        long begin = System.currentTimeMillis();
        /* List<Document> dList = new ArrayList<Document>();
        for(int i=0;i<10000;i++) {
           Document bdo = new Document();
           bdo.append("name", "eclipse"+i);
           bdo.append("age", i);
           bdo.append("sex", i%2);
           dList.add(bdo);
           //mc.insertOne(bdo);
        }
        mc.insertMany(dList);*/
        System.out.println("time = " + (System.currentTimeMillis() - begin));
        /*Document bdo = new Document();
        bdo.append("name", "eclipse");
        bdo.append("age", 20);
        bdo.append("sex", 1);
        mc.insertOne(bdo);*/
        FindIterable<Document> findIterable = mc.find();
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while (mongoCursor.hasNext()) {
            Document d = mongoCursor.next();
            int title = (Integer) d.get("age");
            if (title % 1000 == 0) {
                System.out.println(d);
            }
        }
	}
}
