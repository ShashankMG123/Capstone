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

public class ETLTopologyRF
{
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
		 
		 Config conf = new Config();
		 conf.setDebug(false);
		 conf.setNumWorkers(7);
		 Properties p_=new Properties();
		 InputStream input = new FileInputStream(taskPropFilename);
		 p_.load(input);
		 TopologyBuilder builder = new TopologyBuilder();
		
		


        builder.setSpout("spout1", new SampleSenMLSpout(spout1InputFilePath, spoutLogFileName, argumentClass.getScalingFactor()),1);
		 
	   builder.setBolt("SenMlParseBolt",
	                new SenMLParseBolt(p_), 1)
	                .shuffleGrouping("spout1");


        builder.setBolt("RangeFilterBolt",
	                new RangeFilterBolt(p_), 1)
	                .fieldsGrouping("SenMlParseBolt", new Fields("OBSTYPE"));
	
	builder.setBolt("sink", new Sink(sinkLogFileName), 1)
	    .shuffleGrouping("RangeFilterBolt");
	
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
