# Proejct: flink-ecommerce <br> 

Code from tutorial on <b>Youtube</b>: https://www.youtube.com/watch?v=deepQRXnniM <br>

# Steps <br>
Step 1: Download Flink and start it <br> 
1.1 https://flink.apache.org/downloads/ <br>
1.2 ```$FLINK_HOME/bin/start-cluster.sh``` <br>

Step 2: Clone master branch <br>
Step 3: Submit flink job <br>
1.1 ```mvn clean package``` <br>
1.2 ```$FLINK_HOME/bin/flink run -c package.MainClass target/JAR_FILE.jar``` <br>

Step 4: Run python scripts to gen data and push to kafka <br>

# Address used in project: <br>
localhost:5601 kibana ui <br>
localhost:8087 flink ui (edited in $FLINK_HOME/conf/flink-conf.yaml) <br>
localhost:9021 kafka ui (control-center) <br>
