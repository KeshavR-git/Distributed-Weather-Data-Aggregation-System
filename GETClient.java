import java.io.*; // for handling input and output
import java.net.*; // for handling networking
import org.json.*; // library used for json parsing
import java.util.*; // library consisting of utility functions and classes

public class GETClient {
    public static void main(String[] args) throws IOException {
        // check if the the servername is supplied
        if (args.length < 1) {
            System.out.println("Usage: java GETClient <url> [<stationID>]");
            return;
        }
        // make a URL type object of the servername string to parse the hostname and port
        URL url = new URL(args[0]);
        String server = url.getHost();
        int port;
        if (url.getPort() == -1) {
            port = 80;
        }
        else {
            port = url.getPort();
        }
        String stationID;
        if (args.length > 1) {
            stationID = args[1];
        }
        else {
            stationID = "";
        }
        try {
            // creates new socket object that attempts to connect to host string and port
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(server, port), 10000);
            // creating a PrintWriter object that sends data through the socket
            PrintWriter request = new PrintWriter(socket.getOutputStream());
            // creating a BufferedReader type object to read data received from socket
            BufferedReader response = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Send HTTP GET request
            // \r\n is necessary to indicate end of line (required by HTTP protocol)
            request.print("GET /" + stationID + " HTTP/1.1\r\n");
            request.print("Host: " + server + "\r\n");
            request.print("Connection: close\r\n");
            request.print("\r\n");
            // flush method is required to ensure data is sent through socket
            request.flush();

            // Read the server's response line by line and print the response
            String line;
            StringBuilder sb = new StringBuilder(); // A stringBuilder is used to create a mutable sequence of characters.
            while ((line = response.readLine()) != null) {
                sb.append(line);
            }
            try {
                JSONObject json = new JSONObject(sb.toString());
                Iterator<?> keys = json.keys(); // Iterator object helps iterate over the JSON string
                // iterating through the keys in JSON string and then formatting the output approriately
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    System.out.println(key + ": " + json.get(key));
                }
            } catch (JSONException e) {
                System.out.println("Error parsing JSON: " + e.getMessage());
            }

            // Close resources which is the socket obj, PrintWriter obj, BufferedReader obj
            response.close();
            request.close();
            socket.close();
            
        } catch (SocketTimeoutException e) {
            System.out.println("Connection timed out");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


