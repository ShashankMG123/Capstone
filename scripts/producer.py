from kafka import KafkaProducer
import sys
import time
import os


f=list(open('/home/student1/streamingGc/dataset/foil13/TAXI_sample_new_3.csv'))
producer= KafkaProducer(bootstrap_servers=['localhost:9092'])

prev=None
counter=0
i=0
modCounter = 0 

while(i<int(sys.argv[1])):
    s= f[i]
    bt = s.split(',',1)[0]
    s=s.split(',',1)[1]
    if(not prev):
        prev = bt
    if(prev==bt):
        #counter+=1
        producer.send('sample'+str(modCounter), value=s.encode('utf-8'))
        #modCounter = (modCounter+1)%4
        #print(s)
    else:
        #print(bt,prev,int(bt)-int(prev),counter)

        counter += 1
        if(counter == sys.argv[3]):
            #time.sleep((int(bt)-int(prev))/sys.argv[2])
            counter = 0
            
        producer.send('sample'+str(modCounter), value=s.encode('utf-8'))
        #modCounter = (modCounter+1)%4
        #producer.flush()
    i+=1
    prev = bt

    #time.sleep(2)
    

print(i)
producer.close()


