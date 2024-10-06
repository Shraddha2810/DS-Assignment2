import org.junit.*;
import java.io.*;
import java.net.*;
import static org.junit.Assert.*;

public class ContentServerTest {

    private static final String TEST_WEATHER_FILE = "test_weather_data.txt";
    private static final String SERVER_ADDRESS = "localhost";
    private static final int TEST_PORT = 4567;

    @Before
    public void setUp() throws IOException {
        // Create a valid test weather data file before each test
        PrintWriter writer = new PrintWriter(new FileWriter(TEST_WEATHER_FILE));
        writer.println("id: TEST123");
        writer.println("name: TestLocation");
        writer.println("state: TestState");
        writer.println("lat: -35.0");
        writer.println("lon: 138.0");
        writer.close();
    }

    @After
    public void tearDown() {
        // Clean up after each test by deleting the test file
        File file = new File(TEST_WEATHER_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    // ============== UNIT TESTS ==============
   

    @Test
    public void testInvalidDataFileFormat() throws IOException {
        // Create a file with invalid format (missing colons, etc.)
        PrintWriter writer = new PrintWriter(new FileWriter(TEST_WEATHER_FILE));
        writer.println("INVALID DATA FILE");
        writer.close();

        // Simulate starting the ContentServer
        String[] args = {SERVER_ADDRESS, String.valueOf(TEST_PORT), TEST_WEATHER_FILE};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        ContentServer.main(args);

        String output = outputStream.toString();
        assertTrue("Should print error about invalid format", output.contains("Error: Unable to connect to the server."));
    }

    // ============== EDGE CASE TESTS ==============

    @Test
    public void testEmptyDataFile() throws IOException {
        // Create an empty weather data file
        PrintWriter writer = new PrintWriter(new FileWriter(TEST_WEATHER_FILE));
        writer.close();

        // Simulate starting the ContentServer with an empty file
        String[] args = {SERVER_ADDRESS, String.valueOf(TEST_PORT), TEST_WEATHER_FILE};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        ContentServer.main(args);

        String output = outputStream.toString();
        assertTrue("Should print error about empty file", output.contains("Error: Unable to connect to the server."));
    }

    @Test
    public void testLargeDataFile() throws IOException {
        // Create a large file for testing
        PrintWriter writer = new PrintWriter(new FileWriter(TEST_WEATHER_FILE));
        for (int i = 0; i < 10000; i++) {
            writer.println("id: TEST" + i);
            writer.println("name: TestLocation" + i);
            writer.println("state: TestState");
            writer.println("lat: -35.0");
            writer.println("lon: 138.0");
        }
        writer.close();

        // Simulate starting the ContentServer with a large data file
        String[] args = {SERVER_ADDRESS, String.valueOf(TEST_PORT), TEST_WEATHER_FILE};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        ContentServer.main(args);

        String output = outputStream.toString();
        assertTrue("Should handle large data file without crashing", output.contains("Sending JSON data"));
    }

    // ============== HELPER METHOD ==============
    // A helper method for simulating server responses can be added if needed for more advanced tests

}
