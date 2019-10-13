package single_run.single_run;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import io.netty.util.internal.ThreadLocalRandom;

public class Test {
	static volatile LongAdder la = new LongAdder();
	static File file = new File("config/hostnameTotal.txt");
	static List<String> lines = null;
	static File randomFile = new File("config/random.txt");
	static List<String> randomLines = null;

	private static void fun(String broker, String topic, List<String> lines, long stopCounter, List<String> randomLines,
			int days, boolean timeBalance) throws Exception {
		String str1 = "";
		String str2 = "";
		MyKafkaProducer mp = new MyKafkaProducer(broker);
		String cont = "";
		int len = lines.size();
		int randomLen = randomLines.size();
		String hostname = "";
		String random = "";
		String postdata = "20191012224823246dfgkjjkgfgfhgjkfdgfhmjfgg2019101dfgkjjkgfgfhgjkfdgfhmjfgg2019101dfgkjjkgfgfhgjkfdgfhmjfgg20191012224823246dfgkjjkgfgfhgjkfdgfhmjfgg20191012224823246dfgkjjkgfgfhgjkfdgfhmjfgg201910122";
		long time = 0;
		long tempCounter = 0;
		while (true) {
			// while (i<1000) {
			hostname = lines.get(Utils.randomNum(0, len - 1));
			random = randomLines.get(Utils.randomNum(0, randomLen));
			if (timeBalance) {
				time = DateUtil.addXDay(new Date(), ThreadLocalRandom.current().nextInt(-days, 0)).getTime();
			} else {
				time = new Date().getTime();
			}
			str1 = Utils.genRandomStr(32) + "`|";
			str2 = "`|172.28." + random + ".46`|" + time + "`|/httptest/post/" + random
					+ "`|Apache-HttpClient%2F4.5.3%20(Java%2F1.8.0_152)`|`|JSESSIONID%3DYzAyOTY5ZDEtNDA5NS00YTExLTk0YjYtMjg2MjJmNWU3ZTc2`|0`|200`|0`|547`|251`|POST`|80`|http`|"
					+ postdata + "`|172.28.72.10`|off`|0";
			cont = str1 + hostname + str2;
			// System.out.println("-------------"+cont);
			mp.produce(topic, cont.getBytes("UTF-8"));
			// System.out.println(cont);
			la.increment();
			// counter.incrementAndGet();
			tempCounter = la.longValue();
			if (tempCounter % 10000 == 0) {
				System.out.println("##" + Thread.currentThread().getName() + "-"
						+ DateUtil.d2s(new Date(), DateUtil.YYYYMMDDHHMMSSSSS) + " --> " + tempCounter);
			}
			if (tempCounter >= stopCounter) {
				System.out.println("达到计数器值 " + stopCounter + " ，系统退出");
				System.exit(0);
			}
		}
		// i++;
		// }
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("使用方法 java -jar xx.jar  参数一  参数二  参数三  参数四  参数五");
		System.out.println("参数一：kafka地址 如172.28.72.113:9092,172.28.72.110:9092,172.28.72.99:9092");
		System.out.println("参数二：kafka 的topic 如 waf_access_log");
		System.out.println("参数三：线程数量 如10，这个谨慎设置");
		System.out.println("参数四：停止标志位，如100000，当计数器达到这个值程序退出，最小不低于10000");
		System.out.println("参数五：数据分布时间差，如30，预置数据为-30至当前时间");
		System.out.println("参数六：true均匀分布时间或false当前时间戳，false时参数五失效");
		if (args.length != 6) {
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
		randomLines = Files.readLines(randomFile, Charsets.UTF_8);
		boolean timeBalance = Boolean.parseBoolean(args[5]);
		int threadNum = Integer.parseInt(args[2]);
		ThreadPoolExecutor tpe = new ThreadPoolExecutor(threadNum, threadNum, 10, TimeUnit.SECONDS,
				new LinkedBlockingQueue(), new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						// TODO Auto-generated method stub
						Thread t = new Thread(r, "MyThread-" + r.hashCode());
						return t;
					}
				});
		System.out.println("启动时间 " + DateUtil.d2s(new Date(), DateUtil.YYYYMMDDHHMMSSSSS));
		for (int i = 0; i < threadNum; i++) {
			tpe.submit(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						fun(args[0], args[1], lines, stopCounter, randomLines, days, timeBalance);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			});
		}
	}

}
