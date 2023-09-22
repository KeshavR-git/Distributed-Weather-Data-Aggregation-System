import java.io.*; // for handling input and output
import java.net.*; // for handling networking

public class GETClient {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java GETClient <url> [<stationID>]");
            return;
        }

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

        // creates new socket object that attempts to connect to host string and port
        Socket socket = new Socket(server, port);
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
        while ((line = response.readLine()) != null) {
            System.out.println(line);
        }

        // Close resources which is the socket obj, PrintWriter obj, BufferedReader obj
        response.close();
        request.close();
        socket.close();
    }
}


