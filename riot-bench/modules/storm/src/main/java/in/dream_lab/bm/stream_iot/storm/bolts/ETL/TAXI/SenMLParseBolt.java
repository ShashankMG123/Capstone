package in.dream_lab.bm.stream_iot.storm.bolts.ETL.TAXI;

import in.dream_lab.bm.stream_iot.tasks.AbstractTask;
import in.dream_lab.bm.stream_iot.tasks.parse.SenMLParse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SenMLParseBolt extends BaseRichBolt {

		private static Logger l;
		private Properties p;
		private ArrayList<String> observableFields ;
		private String [] metaFields ;
		private String idField;
    
	    public SenMLParseBolt(Properties p_){
	         p=p_;
	    }
	    OutputCollector collector; 

	    SenMLParse senMLParseTask ;
    Random r;
    long id;
    FileWriter writer, writer2;
	    public static void initLogger(Logger l_) {     l = l_; }

		@Override
		public void prepare(Map arg0, TopologyContext arg1, OutputCollector outputCollector) 
		{
			try 
			{
				writer2= new FileWriter("/home/azureuser/streamingGc/outputs/timestamps.txt",true);
			    
				initLogger(LoggerFactory.getLogger("APP"));
				senMLParseTask = new SenMLParse();
				senMLParseTask.setup(l,p);
				this.collector=outputCollector;
				observableFields = new ArrayList();
				r = new Random();
				id= (long) (1*Math.pow(10,12)+(r.nextInt(1000)*Math.pow(10,9))+r.nextInt(10));
				String line;
				ArrayList<String> metaList = new ArrayList<String>();
				
				/* read meta field list from property */
				String meta = p.getProperty("PARSE.META_FIELD_SCHEMA");
				idField = p.getProperty("PARSE.ID_FIELD_SCHEMA");
				metaFields = meta.split(",");
				for(int i = 0;  i< metaFields.length; i++)
				{
					metaList.add(metaFields[i]);
				}
				/* read csv schema to read fields observable into a list
				 excluding meta fields read above */
				FileReader 	reader = new FileReader(p.getProperty("PARSE.CSV_SCHEMA_FILEPATH"));
				BufferedReader br = new BufferedReader(reader);
				line = br.readLine();
				String [] obsType = line.split(",");
				for(int i = 0; i < obsType.length ; i++)
				{
					if(metaList.contains(obsType[i]) == false)
					{
						observableFields.add(obsType[i]);
					}
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
		}
	    
	    @Override
		public void execute(Tuple tuple) 
		{
			try 
			{
			    String msg = tuple.getStringByField("value");
				String msgCopy = msg;
				writer2.write(msgCopy.split("----")[1] + "," + System.currentTimeMillis() +"\n");
				msg = msg.split("----")[0];
			    l.info(msg);
				String msgId = Long.toString(id++);
				HashMap<String, String> map = new HashMap();
		        map.put(AbstractTask.DEFAULT_KEY, msg);
				senMLParseTask.doTask(map);
				HashMap<String, String> resultMap =(HashMap) senMLParseTask.getLastResult();
				
				/* loop over to concatenate different meta fields together 
				 * preserving ordering among them */
				StringBuilder meta = new StringBuilder();
				for(int i = 0; i< metaFields.length ; i++)
				{
					meta.append(resultMap.get((metaFields[i]))).append(",");
				}
				meta = meta.deleteCharAt(meta.lastIndexOf(","));
				for(int j = 0; j < observableFields.size(); j++)
				{
					collector.emit(new Values(msgId, resultMap.get(idField) ,meta.toString() , (String)observableFields.get(j) ,(String) resultMap.get((String)observableFields.get(j))));
					collector.ack(tuple);
					
 				}				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) 
		{
			declarer.declare(new Fields("MSGID", "SENSORID" , "META", "OBSTYPE", "OBSVAL"));
		}

		@Override
		public void cleanup()
		{	    
			super.cleanup();
		}
}
