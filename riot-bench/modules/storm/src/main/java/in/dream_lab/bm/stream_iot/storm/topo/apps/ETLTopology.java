package in.dream_lab.bm.stream_iot.storm.topo.apps;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;

import in.dream_lab.bm.stream_iot.storm.bolts.ETL.TAXI.AnnotationBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.ETL.TAXI.AzureTableInsertBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.ETL.TAXI.BloomFilterCheckBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.ETL.TAXI.CsvToSenMLBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.ETL.TAXI.InterpolationBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.ETL.TAXI.JoinBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.ETL.TAXI.MQTTPublishBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.ETL.TAXI.RangeFilterBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.ETL.TAXI.SenMLParseBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.IoTPredictionBolts.SYS.LinearRegressionPredictorBolt;
import in.dream_lab.bm.stream_iot.storm.genevents.factory.ArgumentClass;
import in.dream_lab.bm.stream_iot.storm.genevents.factory.ArgumentParser;
import in.dream_lab.bm.stream_iot.storm.sinks.Sink;
import in.dream_lab.bm.stream_iot.storm.spouts.SampleSenMLSpout;

import static org.apache.storm.kafka.spout.FirstPollOffsetStrategy.EARLIEST;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff.TimeInterval;
import org.apache.storm.kafka.spout.KafkaSpoutRetryService;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff;
import org.apache.storm.kafka.spout.KafkaSpout;
//import org.apache.storm.kafka.SpoutConfig;
//import org.apache.storm.kafka.ZkHosts;
//import org.apache.storm.spout.SchemeAsMultiScheme;

public class ETLTopology 
{
    public static KafkaSpoutConfig<String, String> getKafkaSpoutConfig(String bootstrapServers, String topicName) {
	//ByTopicRecordTranslator<String, String> trans = new ByTopicRecordTranslator<>((r) -> new Values(r.topic(), r.partition(), r.offset(), r.key(), r.value()),new Fields("topic", "partition", "offset", "key", "value"), "KafkaTest");

	//trans.forTopic(TOPIC_0,
	//		       (r) -> new Values(r.topic(), r.partition(), r.offset(), r.key(), r.value()),
	//		       new Fields("topic", "partition", "offset", "key", "value"), "SampleTopic");
	return KafkaSpoutConfig.builder(bootstrapServers, new String[]{topicName})
	    .setProp(ConsumerConfig.GROUP_ID_CONFIG, "kafkaSpoutTestGroup")
	    .setRetry(getRetryService())
	    //.setRecordTranslator(trans)
	    .setOffsetCommitPeriodMs(10_000)
	    .setFirstPollOffsetStrategy(EARLIEST)
	    .setMaxUncommittedOffsets(250)
	    .build();
    }

    public static KafkaSpoutRetryService getRetryService() {
	return new KafkaSpoutRetryExponentialBackoff(TimeInterval.microSeconds(500),
						     TimeInterval.milliSeconds(2), Integer.MAX_VALUE, TimeInterval.seconds(10));
    }
	 public static void main(String[] args) throws Exception
	 {
		 ArgumentClass argumentClass = ArgumentParser.parserCLI(args);
		 if (argumentClass == null) {
			 System.out.println("ERROR! INVALID NUMBER OF ARGUMENTS");
			 return;
		 }
		 String logFilePrefix = argumentClass.getTopoName() + "-" + argumentClass.getExperiRunId() + "-" + argumentClass.getScalingFactor() + ".log";
		 String sinkLogFileName = argumentClass.getOutputDirName() + "/sink-" + logFilePrefix;
		 String spoutLogFileName = argumentClass.getOutputDirName() + "/spout-" + logFilePrefix;
		 String taskPropFilename=argumentClass.getTasksPropertiesFilename();
		 String spout1InputFilePath=argumentClass.getInputDatasetPathName();

		 String TOPIC_0 = "sample0";
		 String TOPIC_1 = "sample1";
		 String TOPIC_2 = "sample2";
		 String TOPIC_3 = "sample3";
		 Config conf = new Config();

		 //ZkHosts kafkaBrokerHosts = new ZkHosts("localhost:2181");
		 //SpoutConfig kafkaConfig = new SpoutConfig(kafkaBrokerHosts, "sample", "","storm");
		 /*		 String zkIp = "127.0.0.1";

		 String nimbusHost = "127.0.0.1";

		 String zookeeperHost = zkIp +":2181";

		 ZkHosts zkHosts = new ZkHosts(zookeeperHost);

		 SpoutConfig kafkaConfig = new SpoutConfig(zkHosts, "sample", "", "storm");

		 KafkaSpout kafkaSpout = new KafkaSpout(kafkaConfig);
		 */
		 conf.setDebug(false);
		 conf.put("topology.backpressure.enable",false);
		 conf.put("kafka.fetch.size.bytes", 12582912);
		 conf.put("kafka.buffer.size.bytes", 12582912);
		 
		 //conf.put("topology.spout.max.batch.size",64*1024);
		 //conf.put("topology.max.spout.pending", 2048);
		 //conf.put("topology.executor.receive.buffer.size", 16384);
		 //conf.put("topology.executor.send.buffer.size", 16384);
		 
		 conf.setNumWorkers(7);
		 Properties p_=new Properties();
		 InputStream input = new FileInputStream(taskPropFilename);
		 p_.load(input);
		 TopologyBuilder builder = new TopologyBuilder();
		
		/*The below code shows how we can have multiple spouts read from different files 
		This is to provide multiple spout threads running at the same time but reading 
		data from separate file - Shilpa  */

	    
	   
String spout2InputFilePath="/home/student1/streamingGc/riot-bench/modules/tasks/src/main/resources/TAXI_sample_data_senml2.csv";


//builder.setSpout("kafka_spout0", new KafkaSpout<>(getKafkaSpoutConfig("localhost:9092",TOPIC_0)), 2);
//builder.setSpout("kafka_spout1", new KafkaSpout<>(getKafkaSpoutConfig("localhost:9092",TOPIC_1)), 2);
//builder.setSpout("kafka_spout2", new KafkaSpout<>(getKafkaSpoutConfig("localhost:9092",TOPIC_2)), 2);
//builder.setSpout("kafka_spout3", new KafkaSpout<>(getKafkaSpoutConfig("localhost:9092",TOPIC_3)), 2);

builder.setSpout("kafka_spout0", new KafkaSpout<>(KafkaSpoutConfig.builder("127.0.0.1:9092", TOPIC_0).build()), 2);
builder.setSpout("kafka_spout1", new KafkaSpout<>(KafkaSpoutConfig.builder("127.0.0.1:9092", TOPIC_1).build()), 2);
builder.setSpout("kafka_spout2", new KafkaSpout<>(KafkaSpoutConfig.builder("127.0.0.1:9092", TOPIC_2).build()), 2);
builder.setSpout("kafka_spout3", new KafkaSpout<>(KafkaSpoutConfig.builder("127.0.0.1:9092", TOPIC_3).build()), 2);


//       String spout3InputFilePath=basePathForMultipleSpout+"SYS-inputcsv-predict-10spouts200mps-480sec-file3.csv";
//
//       String spout4InputFilePath=basePathForMultipleSpout+"SYS-inputcsv-predict-10spouts200mps-480sec-file4.csv";
//       String spout5InputFilePath=basePathForMultipleSpout+"SYS-inputcsv-predict-10spouts200mps-480sec-file5.csv";
//       String spout6InputFilePath=basePathForMultipleSpout+"SYS-inputcsv-predict-10spouts200mps-480sec-file6.csv";
//       String spout7InputFilePath=basePathForMultipleSpout+"SYS-inputcsv-predict-10spouts200mps-480sec-file7.csv";
//       String spout8InputFilePath=basePathForMultipleSpout+"SYS-inputcsv-predict-10spouts200mps-480sec-file8.csv";
//       String spout9InputFilePath=basePathForMultipleSpout+"SYS-inputcsv-predict-10spouts200mps-480sec-file9.csv";
//       String spout10InputFilePath=basePathForMultipleSpout+"SYS-inputcsv-predict-10spouts200mps-480sec-file10.csv";
		


//builder.setSpout("spout1", new SampleSenMLSpout(spout1InputFilePath, spoutLogFileName, argumentClass.getScalingFactor()),
//1);
//builder.setSpout("kafka_spout", new SampleSenMLSpout(spout1InputFilePath, spoutLogFileName, argumentClass.getScalingFactor()),
//              1);

//builder.setSpout("spout1", new KafkaSpout(kafkaConfig), 1);
//builder.setSpout("spout1", kafkaSpout, 1);
    
//	 builder.setSpout("spout2", new SampleSenMLSpout(spout2InputFilePath, spoutLogFileName, argumentClass.getScalingFactor()),
//               1);
//       builder.setSpout("spout3", new SampleSenMLSpout(spout2InputFilePath, spoutLogFileName, argumentClass.getScalingFactor()),
//               1);
//       builder.setSpout("spout4", new SampleSenMLSpout(spout2InputFilePath, spoutLogFileName, 10),
//               1);
//       builder.setSpout("spout5", new SampleSenMLSpout(spout5InputFilePath, spoutLogFileName, argumentClass.getScalingFactor()),
//               1);
//       builder.setSpout("spout6", new SampleSenMLSpout(spout6InputFilePath, spoutLogFileName, argumentClass.getScalingFactor()),
//               1);
//       builder.setSpout("spout7", new SampleSenMLSpout(spout7InputFilePath, spoutLogFileName, argumentClass.getScalingFactor()),
//               1);
//       builder.setSpout("spout8", new SampleSenMLSpout(spout8InputFilePath, spoutLogFileName, argumentClass.getScalingFactor()),
//               1);
//       builder.setSpout("spout9", new SampleSenMLSpout(spout9InputFilePath, spoutLogFileName, argumentClass.getScalingFactor()),
//               1);
//       builder.setSpout("spout10", new SampleSenMLSpout(spout10InputFilePath, spoutLogFileName, argumentClass.getScalingFactor()),
//               1);
		 
	   builder.setBolt("SenMlParseBolt",new SenMLParseBolt(p_), 5)
	       	.shuffleGrouping("kafka_spout0")
	       	.shuffleGrouping("kafka_spout1")
	    	.shuffleGrouping("kafka_spout2")
			.shuffleGrouping("kafka_spout3");
//builder.setBolt("SenMlParseBolt", new SenMLParseBolt(p_),1).shuffleGrouping("kafka_spout");
//         			.shuffleGrouping("spout5")
//         			.shuffleGrouping("spout6")
//         			.shuffleGrouping("spout7")
//         			.shuffleGrouping("spout8")
//         			.shuffleGrouping("spout9")
//		            .shuffleGrouping("spout10");


        builder.setBolt("RangeFilterBolt",
	                new RangeFilterBolt(p_), 5)
	                .fieldsGrouping("SenMlParseBolt", new Fields("OBSTYPE"));

		 builder.setBolt("BloomFilterBolt",
	                new BloomFilterCheckBolt(p_), 5)
	                .fieldsGrouping("RangeFilterBolt", new Fields("OBSTYPE"));
		 
		 builder.setBolt("InterpolationBolt",
	                new InterpolationBolt(p_), 5)
	                .fieldsGrouping("BloomFilterBolt", new Fields("OBSTYPE"));
		 
		 builder.setBolt("JoinBolt",
	                new JoinBolt(p_), 5)
	                .fieldsGrouping("InterpolationBolt", new Fields("MSGID"));
		 
		 builder.setBolt("AnnotationBolt",
	                new AnnotationBolt(p_), 5)
	                .shuffleGrouping("JoinBolt"); 

//		 builder.setBolt("AzureInsert",
//	                new AzureTableInsertBolt(p_), 1)
//	                .shuffleGrouping("AnnotationBolt");
		 
		 builder.setBolt("CsvToSenMLBolt",
	                new CsvToSenMLBolt(p_), 5)
	                .shuffleGrouping("AnnotationBolt");
 
		 //builder.setBolt("PublishBolt",
	         //       new MQTTPublishBolt(p_), 1)
	         //       .shuffleGrouping("CsvToSenMLBolt");

		 builder.setBolt("sink", new Sink(sinkLogFileName), 5)
         			.shuffleGrouping("CsvToSenMLBolt");
		 //.shuffleGrouping("PublishBolt");
//		            .shuffleGrouping("AzureInsert");
		 
		 StormTopology stormTopology = builder.createTopology();
		 
		 if (argumentClass.getDeploymentMode().equals("C")) 
		 {
	            StormSubmitter.submitTopology(argumentClass.getTopoName(), conf, stormTopology);
	        } else {
	            LocalCluster cluster = new LocalCluster();
	            cluster.submitTopology(argumentClass.getTopoName(), conf, stormTopology);
	            Utils.sleep(900000);
	            cluster.killTopology(argumentClass.getTopoName());
	            cluster.shutdown();
	        }
	 }
}
