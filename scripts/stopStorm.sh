nimbusPid=$(head -n 1 /home/student1/streamingGc/scripts/pids/nimbusPid)
kill $nimbusPid

superPid=$(head -n 1 /home/student1/streamingGc/scripts/pids/superPid)
kill $superPid

uiPid=$(head -n 1 /home/student1/streamingGc/scripts/pids/uiPid)
kill $uiPid
