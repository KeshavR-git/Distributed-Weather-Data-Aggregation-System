import java.io.*; // for handling input and output
import java.net.*; // for handling networking
import org.json.*; // library used for json parsing
import java.util.*; // library consisting of utility functions and classes

public class GETClient {
    private static int lamportClock = 0;

    private static final int MAX_RETRIES = 3; // max number of retries
    private static final long RETRY_DELAY_MS = 5000; // delay between retries in milliseconds

    public static void main(String[] args) throws IOException {
        // check if the the servername is supplied
        if (args.length < 1) {
            System.out.println("Usage: java GETClient <url> [<stationID>]");
            return;
        }
        // make a URL type object of the servername string to parse the hostname and
        // port
        URL url = new URL(args[0]);
        String server = url.getHost();
        int port;
        if (url.getPort() == -1) {
            port = 4567;
        } else {
            port = url.getPort();
        }
        String stationID;
        if (args.length > 1) {
            stationID = args[1];
        } else {
            stationID = "";
        }
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                // creates new socket object that attempts to connect to host string and port
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(server, port), 10000);
                // increment the lamport clock whenever we establish connection with Aggregation
                // Server
                lamportClock++;
                // creating a PrintWriter object that sends data through the socket
                PrintWriter request = new PrintWriter(socket.getOutputStream());
                // creating a BufferedReader type object to read data received from socket
                BufferedReader response = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // Send HTTP GET request
                // \r\n is necessary to indicate end of line (required by HTTP protocol)
                request.print("GET /" + stationID + " HTTP/1.1\r\n");
                request.print("Lamport-Clock: " + lamportClock + "\r\n"); // Include Lamport clock value in request
                request.print("Host: " + server + "\r\n");
                request.print("Connection: close\r\n");
                request.print("\r\n");
                // flush method is required to ensure data is sent through socket
                request.flush();

                // Read the server's response line by line and print the response
                String line;
                while (!(line = response.readLine()).isEmpty()) {
                    if (line.startsWith("Lamport-Clock")) {
                        int receivedClock = Integer.parseInt(line.split(": ")[1]);
                        lamportClock = Math.max(lamportClock, receivedClock) + 1; // Update Lamport clock
                    }
                }
                StringBuilder sb = new StringBuilder(); // A stringBuilder is used to create a mutable sequence of
                                                        // characters.
                while ((line = response.readLine()) != null) {
                    sb.append(line);
                }
                if (sb.toString().contains("Data for stationID") && sb.toString().contains("not found.")) {
                    System.out.println(sb.toString());
                    response.close();
                    request.close();
                    socket.close();
                    return;
                }
                lamportClock++;
                try {
                    JSONObject json = new JSONObject(sb.toString());
                    Iterator<?> keys = json.keys(); // Iterator object helps iterate over the JSON string
                    // iterating through the keys in JSON string and then formatting the output
                    // approriately
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

                break; // Break out of the loop if successful.

            } catch (SocketTimeoutException e) {
                System.out.println("Connection timed out. Retrying ...");
            } catch (IOException e) {
                System.out.println("An error occured. Retrying ...");
            }

            retryCount++;
            if (retryCount < MAX_RETRIES) {
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } else {
                System.out.println("Max retries reached. Exiting...");
            }
        }
    }
}
