./hybrisConfiguration.sh -e hybristoolsclient -n test1 -v value1
./hybrisConfiguration.sh -e hybristoolsclient -list | grep test1
./hybrisConfiguration.sh -e hybristoolsclient -check
./hybrisConfiguration.sh -e hybristoolsclient -sync