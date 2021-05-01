from kafka import KafkaConsumer

counter0 = counter1 = counter2 = counter3 = 0
consumer0 = KafkaConsumer(bootstrap_servers=['localhost:9092'],auto_offset_reset='earliest')
consumer0.subscribe(topics=['sample0',"sample1","sample2","sample3"])
for _ in consumer0:
        counter0+=1
        print(counter0)
print(counter0,counter1,counter2,counter3)
        
