./hybrisTypeSystem.sh > tests/typesystem/alltypes.txt
./hybrisTypeSystem.sh -t Product > tests/typesystem/product-type.txt
./hybrisTypeSystem.sh -t CommentItemRelationcommentsColl > tests/typesystem/collection-example.txt
./hybrisTypeSystem.sh -pk 1
A=`./hybrisFlexibleSearch.sh -i Product -mr 1 -f pk -of BRD | grep "pk:" | perl -npe "s/pk: //g" | perl -npe "s/[\n\r]+//g"`
./hybrisTypeSystem.sh -pk $A > tests/typesystem/type-by-pk



                                                                  