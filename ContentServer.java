// ContentServer.java
import java.io.*; // library for handling input and output
import java.net.*; // library for handling networking
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.*; 
import org.json.*; // for JSON parsign

public class ContentServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 3) {
            System.out.println("Usage: java ContentServer <file path> <server host> <server port>");
            return;
        }
        // storing the second command line argument which is the aggregationServer host in serverHost
        String serverHost = args[1];
        // port number of the aggregationServer
        int serverPort = Integer.parseInt(args[2]);

        Path filePath = Paths.get(args[0]);
        List<String> allLines = Files.readAllLines(filePath);
        List<String> currentData = new ArrayList<>();
        // converts file data to JSON and sends it every 30 seconds
        for (String line : allLines) {
            if (line.equals("---")) {
                // Convert currentData to JSON and send it to the Aggregation Server.
                String content = String.join("\n", currentData);
                JSONObject json = convertToJson(content);
                sendToAggregationServer(serverHost, serverPort, json.toString());
    
                // Clear currentData for the next set of data.
                currentData.clear();
    
                // Wait for 30 seconds.
                Thread.sleep(30000);
            } else {
                currentData.add(line);
            }
        }

        // Don't forget to send the last set of data if it exists.
        if (!currentData.isEmpty()) {
            String content = String.join("\n", currentData);
            JSONObject json = convertToJson(content);
            sendToAggregationServer(serverHost, serverPort, json.toString());
        }
    }

    // method for doing the JSON parsing (JSON parser)
    private static JSONObject convertToJson(String content) {
        JSONObject json = new JSONObject();
        String[] lines = content.split("\n");
        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts.length > 1) {
                try {
                    json.put(parts[0], parts[1]);
                } catch (Exception e) {
                    System.out.println("JSON cannot be PUT");
                }
            }
        }
        return json;
    }
    // method to send data through sockets
    private static void sendToAggregationServer(String host, int port, String message) throws IOException {
        Socket socket = new Socket(host, port);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println(message);
        socket.close();
    }
}





