mvn -f pom-worker.xml package
scp -r target/* yjian004@datamining-node02.cs.fiu.edu:source/java/system-noiser
ssh yjian004@datamining-node02.cs.fiu.edu
