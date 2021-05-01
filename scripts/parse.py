import json
import os
import pandas as pd

gctypes = os.listdir("/home/student1/streamingGc/gclogs/run6_withMultiExec/")

latency={}
for gc in gctypes:
    params = os.listdir("/home/student1/streamingGc/gclogs/run6_withMultiExec"+"/"+gc)
    for param in params:
        
        with open("/home/student1/streamingGc/gclogs/run6_withMultiExec/" + gc + "/" + param +"/summary10.json","r") as jsonFile:
            f = json.load(jsonFile)
        completeLatency = f["topologyStats"][0]["completeLatency"]
        if('completeLatency' not in latency):
            latency['completeLatency']=[completeLatency]
        else:
            latency['completeLatency'].append(completeLatency)        
        for i in range(len(f["spouts"])):
            if("kafka"+str(i+1) not in latency):
                latency["kafka"+str(i+1)]=[float(f["spouts"][i]["completeLatency"])]
            else:
                latency["kafka"+str(i+1)].append(float(f["spouts"][i]["completeLatency"]))

        for i in f["bolts"]:
            if(i["encodedBoltId"] not in latency):
                latency[i["encodedBoltId"]]=[float(i["executeLatency"]) + float(i["processLatency"])]
            else:
                latency[i["encodedBoltId"]].append(float(i["executeLatency"]) + float(i["processLatency"]))
                

        if("param_name" not in latency):
            latency['param_name']=[gc+"_"+param]
        else:
            latency['param_name'].append(gc+"_"+param)

'''
df= pd.DataFrame.from_dict(latency, columns=["param_name",
                                                           "kafka1",
                                                           "kafka2",
                                                           "kafka3",
                                                           "kafka4",
                                                           "RangeFilterBolt",
                                                           "BloomFilterBolt",
                                                           "InterpolationBolt",
                                                           "SenMlParseBolt",
                                                           "sink",
                                                           "JoinBolt",
                                                           "CsvToSenMLBolt",
                                                           "AnnotationBolt"])
'''
df= pd.DataFrame.from_dict(latency, orient='index')

df.to_csv("/home/student1/streamingGc/latency_table_withMultiExec.csv")





