/home/student1/streamingGc/kafka_2.11-1.1.0/bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic sample0
#/home/student1/streamingGc/kafka_2.11-1.1.0/bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic sample1
#/home/student1/streamingGc/kafka_2.11-1.1.0/bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic sample2
#/home/student1/streamingGc/kafka_2.11-1.1.0/bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic sample3
/home/student1/streamingGc/kafka_2.11-1.1.0/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 4 --topic sample0
#/home/student1/streamingGc/kafka_2.11-1.1.0/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic sample1
#/home/student1/streamingGc/kafka_2.11-1.1.0/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic sample2
#/home/student1/streamingGc/kafka_2.11-1.1.0/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic sample3
