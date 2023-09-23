// AggregationServer.java
import java.io.*; // library for handling input and output
import java.net.*; // library for handling networking
import java.util.*; // consists of utility classes and interfaes
import org.json.*; // library for json parsing

public class AggregationServer {
    // used to store weather data for different station IDs
    public static Map<String, JSONObject> weatherData = new HashMap<>();

    // Method to get weather data for a specific station ID. It's synchronized to prevent concurrent modification issues.
    public static synchronized JSONObject getWeatherData(String stationID) {
        return weatherData.get(stationID);
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java AggregationServer <port number>");
            return;
        }

        int port = Integer.parseInt(args[0]); // Parsing the port number from command line arguments
        ServerSocket serverSocket = new ServerSocket(port); // Creating a new server socket that listens on the specified port
        // Infinite loop to continuously accept new client 
        while (true) {
            Socket clientSocket = serverSocket.accept(); // Accepting a new client connection
            // Starting a new thread to handle the client connection so that multiple clients can be served concurrently
            new Thread(new ClientHandler(clientSocket)).start(); 
        }
    }

    // Method to add weather data for a specific station ID. It's synchronized to prevent concurrent modification issues.
    public static synchronized void addWeatherData(String stationID, JSONObject data) {
        weatherData.put(stationID, data);
    }
}

// Implementing Runnable so that it can be used in a thread
class ClientHandler implements Runnable {
    private Socket clientSocket; // The socket connected to the client

    // Constructor that takes the client socket as an argument
    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    // The method that gets called when the thread starts
    public void run() {
        try {
            // Reader to read input from the client
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // Writer to send output to the client
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("GET")) {
                    // Extract stationID from GET request
                    String stationID = message.split(" ")[1].substring(1);

                    // If no stationID is supplied
                    // Retrieve weather data for all stationIDs
                    if (stationID.isEmpty()) {
                        for (String id : AggregationServer.weatherData.keySet()) {
                            JSONObject data = AggregationServer.getWeatherData(id);
                            out.println(data.toString());
                        }
                    }

                    else {
                        // Retrieve weather data for stationID
                        JSONObject data = AggregationServer.getWeatherData(stationID);
                        // Send weather data as response
                        out.println(data.toString());
                    }

                    // close connection after sending the response
                    clientSocket.close();
                // if the request is a PUT request
                } else {
                    try {
                        // Parsing the message as a JSON object
                        JSONObject json = new JSONObject(message);
                        // Extracting the station ID from the JSON objects
                        String stationID = json.getString("id");
                        // Adding the weather data to the map
                        AggregationServer.addWeatherData(stationID, json);
                        // Printing a message to indicate that data has been received and stored
                        System.out.println("Received and stored data for station: " + stationID);
                    } catch (Exception e) {
                        System.out.println("Error parsing JSON: " + e.getMessage());
                    }
                }
            }

            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
