
// AggregationServer.java
// import java.util.concurrent.locks.ReadWriteLock;
// import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.io.*; // library for handling input and output
import java.net.*; // library for handling networking
import java.util.*; // consists of utility classes and interfaces

import org.json.*; // library for json parsing

class Request {
    public Socket clientSocket;
    public int lamportClock;
    
    public Request(Socket clientSocket, int lamportClock) {
        this.clientSocket = clientSocket;
        this.lamportClock = lamportClock;
    }
}

public class AggregationServer {
    // used to store weather data for different station IDs
    public static Map<String, JSONObject> weatherData = new HashMap<>();
    public static Set<String> recvdStationIDs = new HashSet<>();
    public static Map<String, Long> lastUpdateTime = new HashMap<>();
    // lamport clock
    public static int lamportClock = 0;

    public static PriorityQueue<Request> requestQueue = new PriorityQueue<>(
        (a, b) -> Integer.compare(a.lamportClock, b.lamportClock)
    );


    // // Read-write lock for handling simultaneous GET and PUT requests
    // private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    // Method to get weather data for a specific station ID. It's synchronized to
    // prevent concurrent modification issues.
    public static synchronized JSONObject getWeatherData(String stationID) {
        return weatherData.get(stationID);
    }

    public static void removeOldData() {
        Iterator<Map.Entry<String, Long>> iterator = lastUpdateTime.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            if (System.currentTimeMillis() - entry.getValue() > 30000) { // 30 seconds
                iterator.remove();
                weatherData.remove(entry.getKey());
                System.out.println("Removed data with ID: " + entry.getKey());
            }
        }
    }

    public static void rewriteBackupFile() {
        List<String> freshData = new ArrayList<>();
        String filePath = "backup.txt";
    
        // Read the file and filter out the outdated data
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject json = new JSONObject(line);
                String stationID = json.getString("id");
                if (lastUpdateTime.containsKey(stationID) && 
                    System.currentTimeMillis() - lastUpdateTime.get(stationID) <= 30000) { 
                    freshData.add(line);
                }
            }
        } catch (IOException | JSONException e) {
            System.out.println("An error occurred while reading the backup file.");
            e.printStackTrace();
        }
    
        // Write the fresh data back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) { // 'false' to overwrite the file
            for (String data : freshData) {
                writer.write(data);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the backup file.");
            e.printStackTrace();
        }
    }

    private void loadDataFromFile() {
        File file = new File("backup.txt");
        if (file.exists() && !file.isDirectory()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    JSONObject json = new JSONObject(line);
                    String stationID = json.getString("stationID");
                    weatherData.put(stationID, json);
                }
            } catch (IOException | JSONException e) {
                System.out.println("Error loading data from backup.txt: " + e.getMessage());
            }
        }
    }
    

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java AggregationServer <port number>");
            return;
        }

        int port = Integer.parseInt(args[0]); // Parsing the port number from command line arguments
        ServerSocket serverSocket = new ServerSocket(port); // Creating a new server socket that listens on the
                                                            // specified port
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                removeOldData();
                rewriteBackupFile();
            }
        }, 0, 30000);

        // Infinite loop to continuously accept new client
        while (true) {
            Socket clientSocket = serverSocket.accept(); // Accepting a new client connection
            // increment Lamport clock after successful connection
            lamportClock++;
            // Starting a new thread to handle the client connection so that multiple
            // clients can be served concurrently
            // new Thread(new ClientHandler(clientSocket)).start();

            // New line:
            synchronized (requestQueue) {
                requestQueue.add(new Request(clientSocket, lamportClock));
                requestQueue.notify();  // Notify any waiting threads
            }

            // This thread processes requests in the order of their Lamport timestamps.
            new Thread(() -> {
                while (true) {
                    Request nextRequest = null;
                    synchronized (requestQueue) {
                        while (requestQueue.isEmpty()) {
                            try {
                                requestQueue.wait();  // Wait until there's a request in the queue
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        nextRequest = requestQueue.poll();  // Fetch the next request (with the smallest Lamport clock)
                    }
                    // Handle this request
                    new Thread(new ClientHandler(nextRequest.clientSocket)).start();
                }
            }).start();
        }
    }

    // Method to add weather data for a specific station ID. It's synchronized to
    // prevent concurrent modification issues.
    public static synchronized void addWeatherData(String stationID, JSONObject data, PrintWriter out) {
        // weatherData.put(stationID, data);
        // add the station ID to the set of received IDs
        // recvdStationIDs.add(stationID);
        if (!recvdStationIDs.contains(stationID)) {
            recvdStationIDs.add(stationID);
            out.print("HTTP/1.1 201 Created");
        } else {
            out.println("HTTP/1.1 200 OK");
        }
        weatherData.put(stationID, data);
        lastUpdateTime.put(stationID, System.currentTimeMillis());
    }

    // public static void weatherDataRecvTime(String id) {
    //     lastUpdateTime.put(id, System.currentTimeMillis());
    // }
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
                    out.println("HTTP/1.1 200 OK");
                    out.println("Lamport-Clock: " + AggregationServer.lamportClock); // Include Lamport clock value in response
                    out.println("Content-Type: application/json"); // header
                    out.println(); // blank line between headers and content
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
                        if (data == null) {
                            // This means that the stationID wasn't found
                            out.println("HTTP/1.1 404 Not Found");
                            out.println(); // blank line to indicate the end of headers
                            out.println("Data for stationID " + stationID + " not found.");
                        }
                        else {
                            // Send weather data as response
                            out.println(data.toString());
                        }
                    }

                    // close connection after sending the response
                    clientSocket.close();
                    break;
                }
                // if the request is a PUT request
                else if (message.startsWith("PUT")) {
                    while (!(message = in.readLine()).isEmpty()) {
                        if (message.startsWith("Lamport-Clock")) {
                            int receivedClock = Integer.parseInt(message.split(": ")[1]);
                            AggregationServer.lamportClock = Math.max(AggregationServer.lamportClock, receivedClock) + 1; // Update Lamport clock
                        }
                    }
                    // Now read the JSON body
                    message = in.readLine();
                    try {
                        // Parsing the message as a JSON object
                        JSONObject json = new JSONObject(message);
                        if (!json.has("id")) {
                            throw new JSONException("Invalid JSON: missing 'id' key");
                        }
                        String filePath = "backup.txt";
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))){
                            writer.write(message);
                            writer.newLine();
                        } catch (IOException e) {
                            System.out.println("An error occurred while writing to the backup file.");
                            e.printStackTrace();
                        }
                        // Extracting the station ID from the JSON objects
                        String stationID = json.getString("id");
                        // Adding the weather data to the map
                        AggregationServer.addWeatherData(stationID, json, out);
                        // Printing a message to indicate that data has been received and stored
                        AggregationServer.lamportClock++;
                        System.out.println("Received and stored data for station: " + stationID);
                        // Acknowledge that the data has been stored
                        out.println("HTTP/1.1 200 OK");
                        out.println("Lamport-Clock: " + AggregationServer.lamportClock);
                        out.println();  // Important: sends a blank line to indicate the end of headers
                    } catch (Exception e) {
                        System.out.println("Error parsing JSON: " + e.getMessage());
                        // Send HTTP status code 500
                        out.println("HTTP/1.1 500 Internal Server Error");
                    }
                    // p
                } else if (message.isEmpty()) {
                    out.println("HTTP/1.1 204 No Content");
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

// this is for the place "p"
// Send HTTP status code 200
                    // out.println("HTTP/1.1 200 OK");
                    // // Include Lamport clock value in response
                    // out.println("Lamport-Clock: " + AggregationServer.lamportClock);
                    // // Include Content-Length header (no message body, so length is 0)
                    // out.println("Content-Length: 0");
                    // // Blank line to indicate end of headers
                    // out.println();
                    // // Flush the PrintWriter to ensure data is sent
                    // out.flush();