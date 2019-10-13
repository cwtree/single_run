/**  
 * Project Name:cmcc.cvt  
 * File Name:KafkaProducer.java  
 * Package Name:com.cmcc.dpi.cvt.kafka  
 * Date:2018年1月31日上午10:19:15  
 * Copyright (c) 2018, chiwei@chinamobile.com All Rights Reserved.  
 *  
*/

package single_run.single_run;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * ClassName:KafkaProducer <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年1月31日 上午10:19:15 <br/>
 * 
 * @author chiwei
 * @version
 * @since JDK 1.6
 * @see
 */
public class MyKafkaProducer {

	private final KafkaProducer<String, byte[]> producer;

	/**
	 * produce
	 * 
	 * @param topic
	 * @param conts
	 * @param dstMac
	 */
	public void produce(String topic, byte[] conts) {

		// 创建kafka的生产者
		producer.send(new ProducerRecord<String, byte[]>(topic, conts));

		// 生产者的主要方法
		// close(long timeout, TimeUnit timeUnit);
		// This method waits up to timeout for the producer to complete the sending of
		// all incomplete requests.
		//producer.flush();// 所有缓存记录被立刻发送
		// producer.close();//Close this producer.
	}

	public MyKafkaProducer(String kafkaServer) {
		Properties props = new Properties();
		/*
		 * ***** 【一些参数说明】
		 */
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
		//props.put(ProducerConfig.CLIENT_ID_CONFIG, "dpi_client_cvt");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.ByteArraySerializer");
		props.put(ProducerConfig.RETRIES_CONFIG, 0);// 失败也不重发，接收丢包
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);// 32 Mbytes生产者能使用的缓冲区大小，缓冲数据
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);// bytes，按照这个大小批量发送消息
		props.put(ProducerConfig.LINGER_MS_CONFIG, 5);// 毫秒级延迟发送
		// 设置缓冲区大小，默认10KB,TCP send buffer
		props.put(ProducerConfig.SEND_BUFFER_CONFIG, 2097152);//TCP发送缓冲区大小bytes
		// kafka生产性能优化
		props.put(ProducerConfig.ACKS_CONFIG, "1");// 生产不等响应
		/**/
		// 自定义分区器
		//props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, MyPartitioner.class.getCanonicalName());// 自定义分区函数
		producer = new KafkaProducer<String, byte[]>(props);
	}

}
