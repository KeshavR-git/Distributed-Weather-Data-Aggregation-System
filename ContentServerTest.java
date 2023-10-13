// Importing necessary libraries and dependencies.
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

// This class tests the functionality of the ContentServer.
public class ContentServerTest {
    // Mock aggregation server to simulate responses.
    private ServerSocket mockAggregationServer;
    private final int MOCK_PORT = 9090;

    // Setup method to run before each test.
    @Before
    public void setup() throws Exception {
        // Create a mock Aggregation Server to simulate responses.
        mockAggregationServer = new ServerSocket(MOCK_PORT);
        new Thread(() -> {
            try {
                Socket clientSocket = mockAggregationServer.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String line;
                // Keep reading input from client
                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    // If input starts with PUT, simulate a response with a specific Lamport Clock value.
                    if (line.startsWith("PUT")) {
                        out.println("HTTP/1.1 200 OK");
                        out.println("Lamport-Clock: 5");
                        out.println();
                        out.flush();
                    }
                }
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Cleanup method to run after each test.
    @After
    public void tearDown() throws Exception {
        mockAggregationServer.close();
    }

    // Test if ContentServer can send data to AggregationServer and updates its Lamport Clock correctly.
    @Test
    public void testSendDataToAggregationServer() throws Exception {
        // Create a test file with sample content.
        String testFileContent = "id:station1\ntemp:25\n---";
        Path testFilePath = Paths.get("test.txt");
        Files.write(testFilePath, testFileContent.getBytes());

        // Run the ContentServer's main method, simulating its behavior.
        ContentServer.main(new String[]{"test.txt", "localhost", String.valueOf(MOCK_PORT)});

        // Check if Lamport Clock value was updated correctly after getting response from mock server.
        assertEquals(11, ContentServer.getLamportClock());
    }

    // Test if ContentServer can convert input data to JSON correctly.
    @Test
    public void testConvertToJson() {
        String input = "id:station1\ntemp:25";
        JSONObject expected = new JSONObject();
        try {
            expected.put("id", "station1");
            expected.put("temp", "25");
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject actual = ContentServer.convertToJson(input);
        assertEquals(expected.toString(), actual.toString());
    }

    // Test if ContentServer updates its Lamport Clock value correctly after sending data.
    @Test
    public void testLamportClockUpdate() throws Exception {
        // Create a temporary test file.
        String testFileContent = "id:station1\ntemp:25\n---";
        Path tempFile = Files.createTempFile("TempWeatherData", "txt");
        Files.write(tempFile, testFileContent.getBytes());

        // Simulate ContentServer behavior.
        ContentServer.main(new String[]{"test.txt", "localhost", String.valueOf(MOCK_PORT)});

        // Check if Lamport Clock value was updated correctly.
        assertEquals(8, ContentServer.getLamportClock());

        // Delete the temporary file created.
        Files.delete(tempFile);
    }

    // Test how ContentServer behaves when it fails to send data (server is unavailable).
    @Test
    public void testFailedDataSend() throws Exception {
        // Close the mock server to simulate a failure scenario.
        mockAggregationServer.close();

        // Create a temporary test file.
        String testFileContent = "id:station1\ntemp:25\n---";
        Path tempFile = Files.createTempFile("TempWeatherData", ".txt");
        Files.write(tempFile, testFileContent.getBytes());

        try {
            // Trying to simulate ContentServer behavior.
            ContentServer.main(new String[]{"test.txt", "localhost", String.valueOf(MOCK_PORT)});
            // This line should not be reached; an exception should be thrown before this.
            fail("Expected an IOException due to server being unavailable.");
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("Connection refused"));
        }

        // Delete the temporary file created.
        Files.delete(tempFile);
    }
}
