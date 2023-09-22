// AggregationServer.java
import java.io.*; // library for handling input and output
import java.net.*; // library for handling networking
import java.util.*; // consists of utility classes and interfaes
import org.json.*; // library for json parsing

public class AggregationServer {
    // used to store weather data for different station IDs
    private static Map<String, JSONObject> weatherData = new HashMap<>();

    public static synchronized JSONObject getWeatherData(String stationID) {
        return weatherData.get(stationID);
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java AggregationServer <port number>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    public static synchronized void addWeatherData(String stationID, JSONObject data) {
        weatherData.put(stationID, data);
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("GET")) {
                    // Extract stationID from GET request
                    String stationID = message.split(" ")[1].substring(1);

                    // Retrieve weather data for stationID
                    JSONObject data = AggregationServer.getWeatherData(stationID);

                    // Send weather data as response
                    out.println(data.toString());

                    // close connection after sending the response
                    clientSocket.close();
                    break;
                } else {
                    try {
                        JSONObject json = new JSONObject(message);
                        String stationID = json.getString("id");
                        AggregationServer.addWeatherData(stationID, json);
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
