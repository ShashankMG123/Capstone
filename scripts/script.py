import os
import time
import requests
import json

os.chdir('/home/student1/streamingGc/ApacheZoo/apache-zookeeper-3.6.2-bin')
os.system('bin/zkServer.sh start conf/zoo_sample.cfg &')
time.sleep(10)

os.chdir('/home/student1/streamingGc/kafka_2.11-1.1.0')
os.system('bin/kafka-server-start.sh config/server.properties &')
time.sleep(10)

os.chdir('/home/student1/streamingGc/scripts')
os.system('sudo sh restartSample.sh')
time.sleep(10)

os.chdir('/home/student1/streamingGc/scripts')
os.system('python3 producer.py 999999 1 1 &')
time.sleep(10)

l = ['UseParallelGC -XX:GCTimeRatio=9','UseParallelGC -XX:GCTimeRatio=19','UseParallelGC -XX:GCTimeRatio=99','UseParallelGC -XX:NewRatio=3','UseParallelGC -XX:NewRatio=6','UseParallelGC -XX:NewRatio=9','UseParallelGC -XX:MaxTenuringThreshold=5','UseParallelGC -XX:MaxTenuringThreshold=10','UseParallelGC -XX:MaxTenuringThreshold=15','UseParallelGC -XX:ParallelGCThreads=3','UseParallelGC -XX:ParallelGCThreads=6','UseParallelGC -XX:ParallelGCThreads=9','UseG1GC -XX:GCTimeRatio=9','UseG1GC -XX:GCTimeRatio=19','UseG1GC -XX:GCTimeRatio=99','UseG1GC -XX:NewRatio=3','UseG1GC -XX:NewRatio=6','UseG1GC -XX:NewRatio=9','UseG1GC -XX:MaxTenuringThreshold=5','UseG1GC -XX:MaxTenuringThreshold=10','UseG1GC -XX:MaxTenuringThreshold=15','UseG1GC -XX:ParallelGCThreads=3','UseG1GC -XX:ParallelGCThreads=6','UseG1GC -XX:ParallelGCThreads=9','UseConcMarkSweepGC -XX:GCTimeRatio=9','UseConcMarkSweepGC -XX:GCTimeRatio=19','UseConcMarkSweepGC -XX:GCTimeRatio=99','UseConcMarkSweepGC -XX:NewRatio=3','UseConcMarkSweepGC -XX:NewRatio=6','UseConcMarkSweepGC -XX:NewRatio=9','UseConcMarkSweepGC -XX:MaxTenuringThreshold=5','UseConcMarkSweepGC -XX:MaxTenuringThreshold=10','UseConcMarkSweepGC -XX:MaxTenuringThreshold=15','UseConcMarkSweepGC -XX:ParallelGCThreads=3','UseConcMarkSweepGC -XX:ParallelGCThreads=6','UseConcMarkSweepGC -XX:ParallelGCThreads=9']

i=0
while i<1:
    a_file = open("../ApacheStorm/apache-storm-2.2.0/conf/storm.yaml", "r")
    lines = a_file.readlines()
    a_file.close()

    para = "worker.childopts: \"-Xms128m -Xmx256m -XX:-PrintClassHistogram  -XX:+"+l[i]+" -XX:+PrintGCDetails -Xloggc:artifacts/gc.log -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=1M -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=artifacts/heapdump -Dcom.sun.management.jmxremote.port=1%ID% -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false\"\n"

    lines[-1]=para
    new_file = open("../ApacheStorm/apache-storm-2.2.0/conf/storm.yaml", "w+")
    for line in lines:
        new_file.write(line)
    new_file.close()

    os.chdir('/home/student1/streamingGc/scripts')
    os.system('sudo sh startStorm.sh')
    time.sleep(20)

    os.chdir('/home/student1/streamingGc/riot-bench/modules/storm/target')
    os.system('sudo ~/streamingGc/ApacheStorm/apache-storm-2.2.0/bin/storm jar iot-bm-storm-0.1-jar-with-dependencies.jar in.dream_lab.bm.stream_iot.storm.topo.apps.ETLTopology C IdentityTopology /home/student1/streamingGc/dataset/foil13/TAXI_sample_new_2.csv TAXI-1 1 /home/student1/streamingGc/rblogs /home/student1/streamingGc/riot-bench/modules/tasks/src/main/resources/tasks_TAXI.properties task')
    time.sleep(20)

    open('pid.txt','w').close()
    os.chdir('/home/student1/streamingGc/scripts')
    os.system('ps -ef|grep "java -server -Dlogging" | grep -v "java -cp" | grep -v "grep" > pid.txt')

    pid_file = open("pid.txt", "r")
    pid_lines = pid_file.readlines()
    pid_file.close()
    d=dict()
    for line in pid_lines:
        x=line[line.find('1670')+4]
        d[x]=line.split()[1]

    os.chdir('/home/student1/streamingGc/dumps')
    os.system('mkdir '+l[i].split()[0]+l[i].split()[1][4:])
    
    topos = requests.get('http://10.10.1.202:8081/api/v1/topology/summary').json()
    topo_id = topos['topologies'][0]['id']
    for j in range(1,11):

        time.sleep(60)
        for c in d:
            os.chdir('/home/student1/streamingGc/dumps/'+l[i].split()[0]+l[i].split()[1][4:])
            os.system("sudo sh -c 'jmap -histo "+str(d[c])+" > worker"+str(c)+"_"+str(j)+".txt'")
            
        topo_details = requests.get('http://10.10.1.202:8081/api/v1/topology/'+str(topo_id))
        with open('/home/student1/streamingGc/ApacheStorm/apache-storm-2.2.0/logs/workers-artifacts/'+str(topo_id)+'/summary'+str(j)+'.json','w') as f:
            json.dump(topo_details.json(),f)

    os.chdir('/home/student1/streamingGc/ApacheStorm/apache-storm-2.2.0')
    os.system('bin/storm kill IdentityTopology')
    time.sleep(20)

    os.chdir('/home/student1/streamingGc/ApacheStorm/apache-storm-2.2.0/logs/workers-artifacts')
    os.system('mv '+str(topo_id)+' '+l[i].split()[0]+l[i].split()[1][4:])
    time.sleep(20)

    os.chdir('/home/student1/streamingGc/scripts')
    os.system('sudo sh stopStorm.sh')
    time.sleep(20)
    
    i=i+1

os.chdir('/home/student1/streamingGc/scripts')
os.system('pkill -f producer.py')
time.sleep(10)

os.chdir('/home/student1/streamingGc/scripts')
os.system('sudo sh restartSample.sh')
time.sleep(10)

os.chdir('/home/student1/streamingGc/kafka_2.11-1.1.0')
os.system('bin/kafka-server-stop.sh config/server.properties &')
time.sleep(10)

os.chdir('/home/student1/streamingGc/ApacheZoo/apache-zookeeper-3.6.2-bin')
os.system('bin/zkServer.sh stop conf/zoo_sample.cfg &')

