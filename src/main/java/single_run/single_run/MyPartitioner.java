package single_run.single_run;

import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * customize partitioner
 * @author chiwei
 *
 */
public class MyPartitioner implements Partitioner {

	private static Logger logger = LoggerFactory.getLogger(MyPartitioner.class);

	@Override
	public void configure(Map<String, ?> configs) {
		// TODO Auto-generated method stub

	}

	@Override
	public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
		// TODO Auto-generated method stub
		if (key == null) {
			return 0;
		}
		int hash = key.hashCode();
		int partition = Math.abs(hash % 30);
		logger.info("自定义分区 key={},partition={}", key, partition);
		return partition;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
