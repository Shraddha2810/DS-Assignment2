import org.junit.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import static org.junit.Assert.*;

public class AggregationSystemTest {

    private static ExecutorService executorService;
    private static final int TEST_PORT = 4567;
    private static final String SERVER_ADDRESS = "localhost";
    private static final String TEST_WEATHER_FILE = "test_weather_data.txt";

    @BeforeClass
    public static void setUpServer() throws Exception {
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                AggregationServer.main(new String[]{String.valueOf(TEST_PORT)});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread.sleep(2000);
    }

    @AfterClass
    public static void tearDownServer() throws Exception {
        executorService.shutdown();
    }

    @Before
    public void createTestDataFile() throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(TEST_WEATHER_FILE));
        writer.println("id:TEST123");
        writer.println("name: TestLocation");
        writer.println("state: TestState");
        writer.println("time_zone: TestTZ");
        writer.println("lat: -35.0");
        writer.println("lon: 138.0");
        writer.println("local_date_time: 15/04:00pm");
        writer.println("local_date_time_full: 20230715160000");
        writer.println("air_temp: 13.3");
        writer.println("apparent_t: 9.5");
        writer.println("cloud: Partly cloudy");
        writer.println("dewpt: 5.7");
        writer.println("press: 1023.9");
        writer.println("rel_hum: 60");
        writer.println("wind_dir: S");
        writer.println("wind_spd_kmh: 15");
        writer.println("wind_spd_kt: 8");
        writer.close();
    }

    @Test
    public void testPUTRequest() throws IOException, InterruptedException {
        String[] contentServerArgs = {SERVER_ADDRESS, String.valueOf(TEST_PORT), TEST_WEATHER_FILE};
        ContentServer.main(contentServerArgs);

        Thread.sleep(1000);

        String response = simulateGETRequest(SERVER_ADDRESS, TEST_PORT);

       
        assertTrue("Response should contain 'TEST123'", response.contains("TEST123"));
        assertTrue("Response should contain 'TestLocation'", response.contains("TestLocation"));
        assertTrue("Response should contain '13.3'", response.contains("13.3"));
    }

    // EDGE CASE TESTS 


    @Test
    public void testLargeDataPUTRequest() throws IOException, InterruptedException {
        // Create a file with a large amount of weather data
        PrintWriter writer = new PrintWriter(new FileWriter(TEST_WEATHER_FILE));
        for (int i = 0; i < 10000; i++) {
            writer.println("id:TEST" + i);
            writer.println("name: TestLocation" + i);
            writer.println("state: TestState");
        }
        writer.close();

        // Simulate ContentServer sending a PUT request with large data
        String[] contentServerArgs = {SERVER_ADDRESS, String.valueOf(TEST_PORT), TEST_WEATHER_FILE};
        ContentServer.main(contentServerArgs);

        // Allow time for the server to process the request
        Thread.sleep(2000);

        // Simulate GETClient retrieving the data (checking for at least one expected entry)
        String response = simulateGETRequest(SERVER_ADDRESS, TEST_PORT);
        assertTrue("Server should store large amount of data", response.contains("TEST9999"));
    }

    // INTEGRATION TESTS
    @Test
    public void testIntegrationPUTAndGET() throws IOException, InterruptedException {
        // Integration test that simulates the ContentServer and GETClient

        // Simulate ContentServer sending a PUT request
        String[] contentServerArgs = {SERVER_ADDRESS, String.valueOf(TEST_PORT), TEST_WEATHER_FILE};
        ContentServer.main(contentServerArgs);

        // Allow time for the server to process the request
        Thread.sleep(1000);

        // Simulate GETClient retrieving the data
        String response = simulateGETRequest(SERVER_ADDRESS, TEST_PORT);

        // Check if GETClient correctly fetches the data which is sent by the ContentServer
        assertTrue("Response should contain 'TEST123'", response.contains("TEST123"));
    }
    @Test
    public void testInvalidJSONPUTRequest() throws IOException, InterruptedException {
    // Create a file with invalid JSON format
    PrintWriter writer = new PrintWriter(new FileWriter(TEST_WEATHER_FILE));
    writer.println("{invalidJson: value}");
    writer.close();

    // ContentServer sending a PUT request with invalid JSON
    String[] contentServerArgs = {SERVER_ADDRESS, String.valueOf(TEST_PORT), TEST_WEATHER_FILE};
    ContentServer.main(contentServerArgs);

    // Allow time for the server to process the request
    Thread.sleep(1000);

    // Simulate GETClient retrieving the data (should reject or not store)
    String response = simulateGETRequest(SERVER_ADDRESS, TEST_PORT);

    // Assert that the server did not store the invalid data
    assertFalse("Server should reject invalid JSON data", response.contains("invalidJson"));
}


    // HELPER METHODS 
    //GETClient sending a GET request and retrieving the data
    private String simulateGETRequest(String server, int port) throws IOException {
        StringBuilder response = new StringBuilder();
        try (Socket socket = new Socket(server, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            
            out.println("GET /weather.json HTTP/1.1");
            out.println();  // End of headers

            // Read and store the server's response
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
        }
        return response.toString();
    }
}
