/home/azureuser/streamingGc/Capstone/ApacheStorm/apache-storm-2.2.0/bin/storm nimbus &
echo $! > /home/azureuser/streamingGc/Capstone/scripts/pids/nimbusPid

/home/azureuser/streamingGc/Capstone/ApacheStorm/apache-storm-2.2.0/bin/storm supervisor &
echo $! > /home/azureuser/streamingGc/Capstone/scripts/pids/superPid

/home/azureuser/streamingGc/Capstone/ApacheStorm/apache-storm-2.2.0/bin/storm ui &
echo $! > /home/azureuser/streamingGc/Capstone/scripts/pids/uiPid