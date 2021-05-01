/home/student1/streamingGc/ApacheStorm/apache-storm-2.2.0/bin/storm nimbus &
echo $! > /home/student1/streamingGc/scripts/pids/nimbusPid

/home/student1/streamingGc/ApacheStorm/apache-storm-2.2.0/bin/storm supervisor &
echo $! > /home/student1/streamingGc/scripts/pids/superPid

/home/student1/streamingGc/ApacheStorm/apache-storm-2.2.0/bin/storm ui &
echo $! > /home/student1/streamingGc/scripts/pids/uiPid
