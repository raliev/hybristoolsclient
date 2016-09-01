#!/bin/bash
java -classpath "../lib/commons-logging-1.2.jar;../lib/httpclient-4.5.2.jar;../lib/httpmime-4.5.2.jar;../lib/jcommander-1.48.jar;../lib/rxjava-1.1.6.jar;../target/hybristools-1.0-SNAPSHOT.jar;../lib/httpcore-4.4.4.jar" com.epam.hybristoolsclient.HybrisCronJob "$1" "$2" "$3" "$4" "$5" "$6" "$7" "$8" "$9" "${10}" "${11}" "${12}" "${13}" "${14}" "${15}" 
