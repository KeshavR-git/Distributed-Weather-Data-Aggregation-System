all:
	javac -cp .:org.json.jar GETClient.java
	javac -cp .:org.json.jar AggregationServer.java
	javac -cp .:org.json.jar ContentServer.java

AggregationServer:
	java -cp .:org.json.jar AggregationServer 4567

ContentServer:
	java -cp .:org.json.jar ContentServer weatherData.txt localhost 4567

Client:
	java -cp .:org.json.jar GETClient http://localhost:4567