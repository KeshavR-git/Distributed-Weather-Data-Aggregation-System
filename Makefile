FILE ?= weatherData.txt
PORT ?= 4567

all:
	javac -cp .:org.json.jar GETClient.java
	javac -cp .:org.json.jar AggregationServer.java
	javac -cp .:org.json.jar ContentServer.java

AggregationServer:
	java -cp .:org.json.jar AggregationServer

ContentServer:
	java -cp .:org.json.jar ContentServer $(FILE) localhost $(PORT)

Client:
	@read -p "Enter the port number: " port; \
	read -p "Enter the stationID (or press Enter to skip): " stationID; \
	if [ -z "$$stationID" ]; then \
		java -cp .:org.json.jar GETClient http://localhost:$$port; \
	else \
		java -cp .:org.json.jar GETClient http://localhost:$$port $$stationID; \
	fi

TestGETClient:
	javac -cp .:junit-4.13.2.jar:hamcrest-core-1.3.jar:mockito-core-5.6.0.jar AggregationServerTest.java

RunGETClientTests:
	java -cp .:junit-4.13.2.jar:hamcrest-core-1.3.jar:org.json.jar: org.junit.runner.JUnitCore GETClientTest

TestAggregationServer:
	javac -cp .:junit-4.13.2.jar:hamcrest-core-1.3.jar:org.json.jar AggregationServerTest.java

RunAggregationServerTests:
	java -cp .:junit-4.13.2.jar:hamcrest-core-1.3.jar:org.json.jar: org.junit.runner.JUnitCore AggregationServerTest

TestContentServer:
	javac -cp .:junit-4.13.2.jar:hamcrest-core-1.3.jar:org.json.jar ContentServerTest.java

RunContentServerTests:
	java -cp .:junit-4.13.2.jar:hamcrest-core-1.3.jar:org.json.jar org.junit.runner.JUnitCore ContentServerTest