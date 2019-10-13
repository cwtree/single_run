package single_run.single_run;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class KafkaTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Properties props = new Properties();
		/*
		 * ***** 【一些参数说明】
		 */
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.28.72.113:9092,172.28.72.110:9092,172.28.72.99:9092");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.GROUP_ID_CONFIG,"groupIdClientChiwei");

		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
		consumer.subscribe(Arrays.asList("waf_attack_log"));
		int counter = 0;
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(100);
			for (ConsumerRecord<String, String> record : records) {
				System.out.println("--------------" + record.value());
				counter ++;
				System.out.println("counter=="+counter);
			}
		}
	}

}
