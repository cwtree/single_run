package single_run.single_run;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import io.netty.util.internal.ThreadLocalRandom;

public class Test2 {
	static volatile AtomicLong counter = new AtomicLong(0);
	static File file = new File("config/hostnameTotal.txt");
	static List<String> lines = null;

	private static void fun(String broker, String topic, List<String> lines, long stopCounter, long time, String random,
			String postdata) throws Exception {
		String str1 = Utils.genRandomStr(32) + "`|";
		String str2 = "`|172.28." + random + ".46`|" + time + "`|/httptest/post/" + random
				+ "`|Apache-HttpClient%2F4.5.3%20(Java%2F1.8.0_152)`|`|JSESSIONID%3DYzAyOTY5ZDEtNDA5NS00YTExLTk0YjYtMjg2MjJmNWU3ZTc2`|0`|200`|0`|547`|251`|POST`|80`|http`|"
				+ postdata + "`|172.28.72.10`|off`|0";
		MyKafkaProducer mp = new MyKafkaProducer(broker);
		String cont = "";
		int len = lines.size();
		// while (true) {
		// while (i<1000) {
		String hostname = lines.get(Utils.randomNum(0, len - 1));
		cont = str1 + hostname + str2;
		// System.out.println("-------------"+cont);
		mp.produce(topic, cont.getBytes("UTF-8"));
		// System.out.println(cont);
		counter.incrementAndGet();
		if (counter.get() % 100 == 0) {
			System.out.println("##" + Thread.currentThread().getName() + "-"
					+ DateUtil.d2s(new Date(), DateUtil.YYYYMMDDHHMMSSSSS) + " --> " + counter.get());
		}
		if (counter.get() >= stopCounter) {
			System.out.println("达到计数器值 " + stopCounter + " ，系统退出");
			System.exit(0);
		}
		// i++;
		// }
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		if (args.length != 5) {
			System.out.println("使用方法 java -jar xx.jar  参数一  参数二  参数三  参数四  参数五");
			System.out.println("参数一：kafka地址 如172.28.72.113:9092,172.28.72.110:9092,172.28.72.99:9092");
			System.out.println("参数二：kafka 的topic 如 waf_access_log");
			System.out.println("参数三：线程数量 如10，这个谨慎设置");
			System.out.println("参数四：停止标志位，如100000，当计数器达到这个值程序退出，最小不低于10000");
			System.out.println("参数五：数据分布时间差，如30，预置数据为-30至当前时间");
			System.exit(0);
		}
		try {
			LogBackConfigLoader.load("config/logback.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
		long stopCounter = Long.parseLong(args[3]);
		int days = Integer.parseInt(args[4]);
		lines = Files.readLines(file, Charsets.UTF_8);
		int threadNum = Integer.parseInt(args[2]);
		for (int i = 0; i < threadNum; i++) {
			new Thread(new Runnable() {
				long time = DateUtil.addXDay(new Date(), ThreadLocalRandom.current().nextInt(-days, 0))
						.getTime();
				String random = ThreadLocalRandom.current().nextInt(1, 100) + "";
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while (true) {
						try {
							//String postdata = Utils.genRandomStr(200);
							fun(args[0], args[1], lines, stopCounter, time, random, "20191012224823246dfgkjjkgfgfhgjkfdgfhmjfgg2019101dfgkjjkgfgfhgjkfdgfhmjfgg2019101dfgkjjkgfgfhgjkfdgfhmjfgg20191012224823246dfgkjjkgfgfhgjkfdgfhmjfgg20191012224823246dfgkjjkgfgfhgjkfdgfhmjfgg201910122");
							if(counter.get()%1000==0) {
								time = DateUtil.addXDay(new Date(), ThreadLocalRandom.current().nextInt(-days, 0))
										.getTime();
								random = ThreadLocalRandom.current().nextInt(1, 100) + "";
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}, "thread" + i).start();
			System.out.println("线程thread" + i + " 启动");
		}
		System.out.println("启动时间 " + DateUtil.d2s(new Date(), DateUtil.YYYYMMDDHHMMSSSSS));
	}

}
