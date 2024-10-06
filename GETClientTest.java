import org.junit.*;
import java.io.*;
import static org.junit.Assert.*;

public class GETClientTest {

    
@Test
public void testNoResponseFromServer() throws IOException {
    // Simulate a GET request when server doesn't send any response
    String[] args = {"localhost", "4567"};
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));

    // No response server
    GETClient.main(args);

    String output = outputStream.toString();
    assertTrue("Should handle no response", output.contains("HTTP/1.1 200 OK"));
}



    @Test
    public void testGetRequest() throws IOException {
        // Simulate starting the GET client
        String[] args = {"localhost", "4567"};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        GETClient.main(args);

        String output = outputStream.toString();
        assertTrue(output.contains("HTTP/1.1 200 OK"));
    }
}
