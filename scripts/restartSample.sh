/home/azureuser/streamingGc/Capstone/kafka_2.13-2.8.0/bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic sample0
/home/azureuser/streamingGc/Capstone/kafka_2.13-2.8.0/bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic sample1
/home/azureuser/streamingGc/Capstone/kafka_2.13-2.8.0/bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic sample2
/home/azureuser/streamingGc/Capstone/kafka_2.13-2.8.0/bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic sample3
/home/azureuser/streamingGc/Capstone/kafka_2.13-2.8.0/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 4 --topic sample0
/home/azureuser/streamingGc/Capstone/kafka_2.13-2.8.0/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic sample1
/home/azureuser/streamingGc/Capstone/kafka_2.13-2.8.0/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic sample2
/home/azureuser/streamingGc/Capstone/kafka_2.13-2.8.0/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic sample3
