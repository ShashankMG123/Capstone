nimbusPid=$(head -n 1 /home/azureuser/streamingGc/Capstone/scripts/pids/nimbusPid)
kill $nimbusPid

superPid=$(head -n 1 /home/azureuser/streamingGc/Capstone/scripts/pids/superPid)
kill $superPid

uiPid=$(head -n 1 /home/azureuser/streamingGc/Capstone/scripts/pids/uiPid)
kill $uiPid

