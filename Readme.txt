# Weather Data Aggregation Server

This is a simple weather data aggregation server that listens for incoming connections on a specified port. 
Each connection is handled in a separate thread. The server can handle GET requests and other types of requests. 
For GET requests, it retrieves and sends back weather data for a specified station ID. 
For other requests, it assumes they are JSON objects containing weather data and stores them.

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
   make ContentServer
   This will start the Content Server, which will connect to the Aggregation Server and send weather data from `weatherData.txt`.

4. Start the GET Client:
   In another new terminal window, run the following command:
   make Client
   This will start the GET Client, which will send a GET request to the Aggregation Server and print out the response.

## Testing
I am yet to do testing for the program, I will be using JUnit for unit testing.
I will also be creating a test harness for integration and regression testing.

## Things I need to add:
1. Different Status Codes for GET AND PUT like 200, 201, 500, 400, 204 in the responses
2. Lamport Clocks
3. Error handling mechanisms for client, Aggregation Server, Content Server
4. Do unit, integration, regression Testing
5. add edge cases in the weatherData.txt file