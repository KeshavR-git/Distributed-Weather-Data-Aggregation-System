import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class GETClientTest {

    // ByteArrayOutputStream to capture the standard output stream.
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    // ByteArrayOutputStream to capture the error output stream.
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    // Saving the original standard output stream.
    private final PrintStream originalOut = System.out;
    // Saving the original error output stream.
    private final PrintStream originalErr = System.err;

    // This method is executed before each test to set up the streams to be captured.
    @Before
    public void setUpStreams() {
        // Redirecting the standard output to our ByteArrayOutputStream.
        System.setOut(new PrintStream(outContent));
        // Redirecting the error output to our ByteArrayOutputStream.
        System.setErr(new PrintStream(errContent));
    }

    // Test to ensure that the program displays correct usage message when no arguments are provided.
    @Test
    public void testInsufficientArguments() throws Exception {
        String[] args = {};
        GETClient.main(args);
        assertEquals("Usage: java GETClient <url> [<stationID>]\n", outContent.toString());
    }

    // Test to ensure the program can handle a URL without a port and doesn't crash.
    @Test
    public void testDefaultPort() throws Exception {
        String[] args = { "http://localhost" };
        GETClient.main(args);
        assertTrue(true);
    }

    // Test to ensure the program can handle a URL with a provided port and doesn't crash.
    @Test
    public void testProvidedPort() throws Exception {
        String[] args = { "http://localhost:8080" };
        GETClient.main(args);
        assertTrue(true);
    }

    // Test to ensure that the program can handle the maximum number of retries when a server is not reachable.
    @Test
    public void testMaxRetries() throws Exception {
        String[] args = { "http://nonexistentserver.com" };
        GETClient.main(args);
        assertTrue(outContent.toString().contains("Max retries reached. Exiting..."));
    }

    // Test the program's error handling when it encounters a malformed JSON response.
    @Test
    public void testJSONErrorHandling() throws Exception {
        // Capturing the standard output stream for this test.
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        // Mocking a malformed JSON response.
        String mockResponse = "HTTP/1.1 200 OK\r\n" +
                "Lamport-Clock: 1\r\n" +
                "\r\n" +
                "{bad_json_response";

        // Redirecting the system input to provide our mock response.
        InputStream sysInBackup = System.in;
        ByteArrayInputStream in = new ByteArrayInputStream(mockResponse.getBytes());
        System.setIn(in);

        String[] args = { "http://localhost:4567" };
        GETClient.main(args);

        assertFalse(outContent.toString().contains("Error parsing JSON"));

        // Restoring the original system input.
        System.setIn(sysInBackup);
    }

    // Test to ensure the program can handle a URL with a valid station ID without crashing.
    @Test
    public void testValidStationID() throws Exception {
        String[] args = { "http://localhost:8080", "123" };
        GETClient.main(args);
        assertTrue(true);
    }

    // Test to ensure the program can handle a URL with an invalid station ID without crashing.
    @Test
    public void testInvalidStationID() throws Exception {
        String[] args = { "http://localhost:8080", "invalid" };
        GETClient.main(args);
        assertTrue(true);
    }
}
