package single_run.single_run;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

public class EsMain {

	public static RestHighLevelClient restHighLevelClient() {
		RestHighLevelClient client = new RestHighLevelClient(
				RestClient.builder(new HttpHost("172.28.72.99", 9200, "http"),
						new HttpHost("172.28.72.104", 9200, "http"), new HttpHost("172.28.72.124", 9200, "http")));
		return client;
	}

	private static void insert(String index, String type, Employee bean) {
		RestHighLevelClient client = restHighLevelClient();
		IndexRequest indexRequest = new IndexRequest(index, type);
		String source = JSON.toJSONString(bean);
		indexRequest.source(source, XContentType.JSON);
		try {
			IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
			System.out.println(indexResponse);
			System.out.println("id = " + indexResponse.getId());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static IndexRequest generateNewsRequest(String index, String type, Employee bean) {
		IndexRequest indexRequest = new IndexRequest(index, type);
		String source = JSON.toJSONString(bean);
		indexRequest.source(source, XContentType.JSON);
		return indexRequest;
	}

	private static void batchInsert(String index, String type, List<Employee> list) {
		long begin = System.currentTimeMillis();
		RestHighLevelClient client = restHighLevelClient();
		BulkRequest bulkRequest = new BulkRequest();
		for (int i = 0; i < list.size(); i++) {
			IndexRequest ir = generateNewsRequest(index, type, list.get(i));
			bulkRequest.add(ir);
		}
		try {
			client.bulk(bulkRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println("begin - end : " + (end - begin));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("使用方法java -jar xxx.jar single/batch counter");
		if (args.length < 2) {
			System.out.println("参数错误");
			System.exit(1);
		}
		long begin = System.currentTimeMillis();
		int counter = Integer.parseInt(args[1]);
		String indexDatabase = "cmcc";
		String typeTable = "employee";
		if ("single".equalsIgnoreCase(args[0])) {
			for (int i = 0; i < counter; i++) {
				Employee ee = new Employee();
				ee.setAbout("about");
				ee.setAge(29 + i);
				ee.setFirstName("chi" + i);
				ee.setLastName("wei" + i);
				List<String> interests = Lists.newArrayList();
				interests.add(i + "sing");
				interests.add("run" + i);
				ee.setInterests(interests);
				insert(indexDatabase, typeTable, ee);
			}
		} else {
			List<Employee> list = Lists.newArrayList();
			for (int i = 0; i < counter; i++) {
				Employee ee = new Employee();
				ee.setAbout("about");
				ee.setAge(29 + i);
				ee.setFirstName("chi" + i);
				ee.setLastName("wei" + i);
				List<String> interests = Lists.newArrayList();
				interests.add(i + "sing");
				interests.add("run" + i);
				ee.setInterests(interests);
				list.add(ee);
				if (i % 10000 == 0) {
					batchInsert(indexDatabase, typeTable, list);
					list.clear();
				}
				if (i >= counter - 1) {
					batchInsert(indexDatabase, typeTable, list);
					list.clear();
				}
			}

		}
		System.out.println("Total time:" + (System.currentTimeMillis() - begin));
	}

}
