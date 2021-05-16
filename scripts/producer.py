from kafka import KafkaProducer
import sys
import time
import os
import datetime

f=list(open('/home/azureuser/streamingGc/Capstone/dataset/TAXI_sample_new_3.csv'))
producer= KafkaProducer(bootstrap_servers=['localhost:9092'])

prev=None
counter=0
i=0
modCounter = 0 

while(i<int(sys.argv[1])):
    s= f[i]
    bt = s.split(',',1)[0]
    s=s.split(',',1)[1]
    s=s+"----"+str(int(datetime.datetime.now().timestamp()))
    if(not prev):
        prev = bt
    if(prev==bt):
        producer.send('sample'+str(modCounter), value=s.encode('utf-8'))
    else:
        counter += 1
        if(counter == sys.argv[3]):
            counter = 0
        producer.send('sample'+str(modCounter), value=s.encode('utf-8'))
    i+=1
    modCounter=(modCounter+1)%4
    prev = bt
    
print(i)
producer.close()


