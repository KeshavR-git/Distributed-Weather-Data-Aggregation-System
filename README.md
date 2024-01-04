# Distributed-Weather-Data-Aggregation-System

## Overview
The Weather Data Aggregation System is composed of multiple components, designed to aggregate and serve weather data efficiently. 
It is structured to handle incoming connections on specified ports, with the ability to concurrently process multiple connections using separate threads.

## Components
### AggregationServer
The AggregationServer is the primary server responsible for data collection and retrieval.

Handling Requests: It is equipped to handle multiple types of requests, including:
GET Requests: The server fetches and sends back weather data corresponding to a provided station ID.
Other Requests: Assumed to be weather data in JSON format, which the server then stores for future retrieval.
The server also employs a Lamport Clock for synchronization purposes.
Data Management: Data is stored in memory, with mechanisms to periodically clean out stale data. Backup functionalities are also available to ensure data persistency.

### ContentServer
The ContentServer acts as an intermediary, processing and converting raw data before sending it to the AggregationServer in a suitable format.

It reads data from specified files, converts the data to a structured JSON format, and then forwards it to the AggregationServer.
The server also ensures synchronization using the Lamport Clock mechanism, updating its clock based on responses from the AggregationServer.
### GETClient
The GETClient component serves as a client interface to retrieve weather data based on specific station IDs.

Upon initiating a GET request, it communicates with the AggregationServer to fetch the corresponding weather data.
The client then prints the received data for user visibility.
Concurrency and Robustness
The system has been designed with concurrency in mind, with each connection managed in a separate thread to ensure efficient processing. 
Exception handling mechanisms have been integrated to manage erroneous data and ensure the robustness of the system.

## How to Run

1. Compile the Java files:
   Use the provided Makefile to compile the Java files. Open a terminal in the project directory and run the following command:
   make all
   This will compile `GETClient.java`, `AggregationServer.java`, and `ContentServer.java`.

2. Start the Aggregation Server:
   Run the following command in the terminal:
   make AggregationServer
   This will start the Aggregation Server on port 4567.

3. Start the Content Server:
   In a new terminal window, run the following command:
   make ContentServer (by default the port = 4567 and the file = weatherData.txt)
   If you want different port or file run the command: make ContentServer FILE = [filename you want] PORT = [port number you want]
   This will start the Content Server, which will connect to the Aggregation Server and send weather data from `weatherData.txt`.

4. Start the GET Client:
   In another new terminal window, run the following command:
   make Client
   This will start the GET Client, which will send a GET request to the Aggregation Server and print out the response (by default that's the one for last ID).
   Then enter the port number and stationID (stationID is optional)

5. To compile the test harness for different entities:
   for GETClient, run: make TestGETClient
   for AggregationServer, run: make TestAggregationServer
   for Content Server, run: TestContentServer

6. To run the tests:
   For GETClient, run the command: make RunGETClientTests
   For AggregationServer, run the command: make RunAggregationServerTests
   For ContentServer, run the command: make RunContentServerTests

   Note: The testcases are interdependent on the Aggregation server in the RunContentServerTests class, so to run this test file
   you need to ensure that you are running the AggregationServer first.

## Testing
Junit has been used for testing purposes. Through a series of dedicated test classes: GETClientTest.java, AggregationServerTest.java, and ContentServerTest.java, it is ensured that our core functionalities are robust and reliable.

GETClientTest.java: Validates the GETClient's behavior, especially its ability to retrieve and handle data.
AggregationServerTest.java: Assesses the AggregationServer's performance in aggregating and serving data.
ContentServerTest.java: Verifies the correct functionality of the ContentServer, ensuring it processes, converts, and sends data accurately.
Integration and Regression Testing
A test harness has been set up to facilitate both integration and regression testing. This harness will ensure seamless interaction between different parts of our program and will help identify any breaking changes during the development lifecycle

Note: in ContentServerTests it's gonna throw a small error message saying socket closed. That does not affect the testing harness, 
the tests still run and pass, it's just a java warning. So please ignore that.

Jar files used: org.json.jar, junit-4.13.2, hamcrest-core-1.3.jar.
