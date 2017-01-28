#!/bin/bash
FIRST=${1:-help}
java -cp ../target/hybristools-1.0-SNAPSHOT.jar com.epam.hybristoolsclient.HybrisLog "${FIRST}" "$2" "$3" "$4" "$5" "$6" "$7" "$8" "$9" "${10}" "${11}" "${12}" "${13}" "${14}" "${15}" 
