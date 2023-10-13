import org.json.*;
import org.junit.*;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

public class AggregationServerTest {

    // This setup method is executed before each test case.
    @Before
    public void setup() {
        // Clear all static data from the AggregationServer to ensure a fresh start for each test.
        AggregationServer.weatherData.clear();
        AggregationServer.recvdStationIDs.clear();
        AggregationServer.lastUpdateTime.clear();
        AggregationServer.lamportClock = 0;
    }

    // Test the method that adds new weather data.
    @Test
    public void testAddWeatherData() {
        JSONObject testData = new JSONObject();
        try {
            testData.put("id", "station1");
            testData.put("temp", 25);
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        // Add weather data to the AggregationServer.
        AggregationServer.addWeatherData("station1", testData, pw);

        // Assertions to validate the weather data was added correctly.
        assertEquals(testData.toString(), AggregationServer.weatherData.get("station1").toString());
        assertTrue(AggregationServer.recvdStationIDs.contains("station1"));
        assertNotNull(AggregationServer.lastUpdateTime.get("station1"));
    }

    // Test the removal of old data.
    @Test
    public void testRemoveOldData() {
        JSONObject testData = new JSONObject();
        try {
            testData.put("id", "station1");
            testData.put("temp", 25);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert old data.
        AggregationServer.weatherData.put("station1", testData);
        AggregationServer.lastUpdateTime.put("station1", System.currentTimeMillis() - 31000); // 31 seconds ago

        // Invoke method to remove old data.
        AggregationServer.removeOldData();

        // Assert that old data was removed.
        assertNull(AggregationServer.weatherData.get("station1"));
    }
    
    // Test backup file rewriting functionality.
    @Test
    public void testRewriteBackupFile() throws IOException {
        JSONObject testData = new JSONObject();
        try {
            testData.put("id", "station1");
            testData.put("temp", 25);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add data to the AggregationServer.
        AggregationServer.weatherData.put("station1", testData);
        AggregationServer.lastUpdateTime.put("station1", System.currentTimeMillis());

        // Write the data to the backup file.
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("backup.txt"))) {
            bw.write(testData.toString());
            bw.newLine();
        }

        // Invoke the method to rewrite backup data.
        AggregationServer.rewriteBackupFile();

        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("backup.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }

        // Assert that backup file contains the expected data.
        assertEquals(1, lines.size());
        assertEquals(testData.toString(), lines.get(0));
    }

     // Cleanup method after all tests are executed.
    @AfterClass
    public static void cleanup() {
        // Delete the backup file to ensure a clean state.
        new File("backup.txt").delete();
    }

    // Test multiple weather data insertions.
    @Test
    public void testMultipleWeatherDataInsertion() {
        JSONObject testData1 = new JSONObject();
        try {
            testData1.put("id", "station1");
            testData1.put("temp", 25);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject testData2 = new JSONObject();
        try {
            testData2.put("id", "station2");
            testData2.put("temp", 30);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add multiple sets of weather data.
        AggregationServer.addWeatherData("station1", testData1, null);
        AggregationServer.addWeatherData("station2", testData2, null);

        // Assert that both sets of data are added correctly.
        assertEquals(testData1.toString(), AggregationServer.weatherData.get("station1").toString());
        assertEquals(testData2.toString(), AggregationServer.weatherData.get("station2").toString());
    }

    // Test overwriting existing data.
    @Test
    public void testDataOverwrite() {
        JSONObject oldData = new JSONObject();
        try {
            oldData.put("id", "station1");
            oldData.put("temp", 25);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject newData = new JSONObject();
        try {
            newData.put("id", "station1");
            newData.put("temp", 30);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AggregationServer.addWeatherData("station1", oldData, null);
        AggregationServer.addWeatherData("station1", newData, null);

        assertEquals(newData.toString(), AggregationServer.weatherData.get("station1").toString());
    }

    // Test that valid data is retained and not mistakenly removed
    @Test
    public void testValidDataRetention() {
        JSONObject testData = new JSONObject();
        try {
            testData.put("id", "station1");
            testData.put("temp", 25);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AggregationServer.weatherData.put("station1", testData);
        AggregationServer.lastUpdateTime.put("station1", System.currentTimeMillis() - 5000); // 5 seconds ago

        AggregationServer.removeOldData();

        assertEquals(testData.toString(), AggregationServer.weatherData.get("station1").toString());
    }

     // Test the increment functionality of Lamport's clock.
    @Test
    public void testLamportClockIncrement() {
        int initialClock = AggregationServer.lamportClock;
        AggregationServer.incrementLamportClock();
        assertEquals(initialClock + 1, AggregationServer.lamportClock);
    }

    // Test the system's ability to handle malformed JSON data.
    @Test
    public void testMalformedJSONHandling() {
        String malformedData = "{ 'id': 'station1', 'temp': 25";
        assertThrows(JSONException.class,
                () -> AggregationServer.addWeatherData("station1", new JSONObject(malformedData), null));
    }
    
    // we create the backup file again to be used for running the program purpose
    @AfterClass
    public static void createBackupFile() throws IOException {
        File backupFile = new File("backup.txt");
        if (!backupFile.exists()) {
            backupFile.createNewFile();
        }
    }
}
