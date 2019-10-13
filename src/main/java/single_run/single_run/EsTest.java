package single_run.single_run;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.alibaba.fastjson.JSON;


public class EsTest {

	public static RestHighLevelClient restHighLevelClient() {
		RestHighLevelClient client = new RestHighLevelClient(
				RestClient.builder(new HttpHost("172.28.72.99", 9200, "http"),
						new HttpHost("172.28.72.104", 9200, "http"), new HttpHost("172.28.72.124", 9200, "http")));
		return client;
	}

	static String INDEX_NAME = "phoenix";
	static String INDEX_TYPE = "alert_item";

	private static void query() {
		RestHighLevelClient client = restHighLevelClient();
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.from(0);
		sourceBuilder.size(10);
		// sourceBuilder.sort(new FieldSortBuilder("id").order(SortOrder.ASC));
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("createdBy", "zhangsan");
		boolQueryBuilder.must(matchQueryBuilder);
		// boolQueryBuilder.must(QueryBuilders.termQuery("createdBy", "zhangsan"));
		sourceBuilder.query(boolQueryBuilder);

		SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
		searchRequest.types(INDEX_TYPE);
		// searchRequest.indices(INDEX_NAME);
		searchRequest.source(sourceBuilder);
		SearchResponse searchResponse = null;
		try {
			searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		RestStatus restStatus = searchResponse.status();
		if (restStatus != RestStatus.OK) {
			System.out.println("查询错误");
		}

		List<AlertItemVO> list = new ArrayList<AlertItemVO>();
		SearchHits searchHits = searchResponse.getHits();
		for (SearchHit hit : searchHits.getHits()) {
			String source = hit.getSourceAsString();
			AlertItemVO book = JSON.parseObject(source, AlertItemVO.class);
			list.add(book);
		}
		System.out.println("---------------------------");
		System.out.println(list);
	}

	public static void delete() {
		RestHighLevelClient client = restHighLevelClient();
		DeleteRequest deleteRequest = new DeleteRequest(INDEX_NAME, INDEX_TYPE, "1qohSGoBHElUf4H7-cM0");
		DeleteResponse response = null;
		try {
			response = client.delete(deleteRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(response);
	}

	public static void deleteIndex() {
		RestHighLevelClient client = restHighLevelClient();
		DeleteIndexRequest deleteRequest = new DeleteIndexRequest(INDEX_NAME);
		AcknowledgedResponse response = null;
		try {
			response = client.indices().delete(deleteRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(response);
	}

	private static void insert(AlertItemVO aiv) {
		RestHighLevelClient client = restHighLevelClient();
		IndexRequest indexRequest = new IndexRequest(INDEX_NAME, INDEX_TYPE);
		String source = JSON.toJSONString(aiv);
		indexRequest.source(source, XContentType.JSON);
		try {
			IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
			System.out.println(indexResponse);
			System.out.println("id = " + indexResponse.getId());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createIndex(String index, String type) {
		RestHighLevelClient client = restHighLevelClient();
		IndexRequest indexRequest = new IndexRequest(index, type);
		try {
			IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
			System.out.println(indexResponse);
			System.out.println("id = " + indexResponse.getId());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static IndexRequest generateNewsRequest(AlertItemVO aiv) {
		IndexRequest indexRequest = new IndexRequest(INDEX_NAME, INDEX_TYPE);
		String source = JSON.toJSONString(aiv);
		indexRequest.source(source, XContentType.JSON);
		return indexRequest;
	}

	private static void batchInsert() {
		long begin = System.currentTimeMillis();
		RestHighLevelClient client = restHighLevelClient();
		BulkRequest bulkRequest = new BulkRequest();
		for (int i = 0; i < 10000; i++) {
			AlertItemVO aiv = new AlertItemVO();
			aiv.setCreatedBy("zhangsan");
			aiv.setAlertStatus(i);
			aiv.setCreateTime(DateUtil.d2s(DateUtil.getCurrentTime(), DateUtil.YYYYMMDDHHMMSSSSS));
			aiv.setGroupName("group" + i);
			aiv.setImagePath("imagepath" + i);
			aiv.setImageUrl("imageurl" + i);
			aiv.setImageUrlOrign("imageurlorigin" + i);
			aiv.setLevel(i);
			aiv.setMonitorPageId((long) i);
			aiv.setResponsiblePerson("person" + i);
			aiv.setResponsiblePhone("phone" + i);
			aiv.setSource(i);
			aiv.setTitle("title" + i);
			aiv.setType("type" + i);
			aiv.setUrl("url" + i);
			IndexRequest ir = generateNewsRequest(aiv);
			bulkRequest.add(ir);
		}
		try {
			client.bulk(bulkRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println("begin - end : " + (end - begin));
	}

	private static void delIndex(String index) {
		RestHighLevelClient client = restHighLevelClient();
		DeleteIndexRequest request = new DeleteIndexRequest(index);
		try {
			AcknowledgedResponse ar = client.indices().delete(request, RequestOptions.DEFAULT);
			// DeleteResponse dr = client.delete(deleteRequest,RequestOptions.DEFAULT);
			System.out.println(ar);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void queryAll() {
		RestHighLevelClient client = restHighLevelClient();
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices(INDEX_NAME);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("createdBy", "lisi");
		searchSourceBuilder.query(matchQueryBuilder);
		searchRequest.source(searchSourceBuilder);
		SearchResponse searchResponse = null;
		try {
			searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("queryAll " + searchResponse);
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		AlertItemVO aiv = new AlertItemVO();
		aiv.setId(1L);
		aiv.setAlertStatus(1);
		aiv.setCreatedBy("chiwei");
		aiv.setCreateTime("20190422165422");
		aiv.setGroupName("分组");
		aiv.setImagePath("https://discuss.elastic.co/");
		aiv.setImageUrl("https://www.baidu.com");
		aiv.setImageUrlOrign("https://www.qq.com");
		aiv.setLevel(2);
		aiv.setMonitorPageId(3L);
		aiv.setResponsiblePerson("chiwei");
		aiv.setResponsiblePhone("18867101080");
		aiv.setSource(4);
		aiv.setTitle("标题");
		aiv.setType("篡改告警");
		aiv.setUrl("http://www.sina.com.cn");
		createIndex("demo_index", "demo_type");
		// insert(aiv);
		// batchInsert();
		// query();
		// queryAll();
		// delete();
		// deleteIndex();
		// delIndex(INDEX_NAME);
	}

}
