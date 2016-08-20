./hybrisBeans.sh > testresults/beans/allbeans
./hybrisBeans.sh -b defaultIndexerContextFactory > testresults/beans/defaultIndexerContextFactory
./hybrisBeans.sh -b solrUpdateSynonymsJob > testresults/beans/solrUpdateSynonymsJob
./hybrisBeans.sh -b testBean > testresults/beans/testBeanInfo
./hybrisBeans.sh -b testBean -n stringProperty -v test2 > testresults/beans/testBean-changing-string-value
./hybrisBeans.sh -b testBean -n refLink -v "<solrUpdateSynonymsJob>" > testresults/beans/testBean-changing-ref-value1
./hybrisBeans.sh -b testBean -n refLink -v "<flexibleSearchToolService>" > testresults/beans/testBean-changing-ref-value2
./hybrisBeans.sh -b defaultIndexerContextFactory > testresults/beans/beanInformation
./hybrisBeans.sh -b image30ValueProvider -n mediaFormat -v 50Wx50H > testresults/beans/changing-string-value-of-bean-property