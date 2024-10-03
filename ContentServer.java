import java.io.*;
import java.net.*;
import org.json.JSONObject;
import java.util.concurrent.atomic.AtomicLong;

public class ContentServer {
    private static final AtomicLong lamportClock = new AtomicLong(0);

   
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java ContentServer <server> <port> <datafile>");
            return;
        }
    
        String server = args[0];
        int port = Integer.parseInt(args[1]);
        String dataFile = args[2];
    
        try {
            // Step 1: Parse the weather data file
            JSONObject jsonData = parseWeatherFile(dataFile);
            if (jsonData == null) {
                System.out.println("Error: Failed to parse the weather data file.");
                return;
            }
    
            // Step 2: Prepare and send the PUT request to the server
            sendPutRequest(server, port, jsonData);
    
        } catch (IOException e) {
            logError("Unable to connect to the server", e);
        }
    }
    
    private static void sendPutRequest(String server, int port, JSONObject jsonData) throws IOException {
        try (Socket socket = new Socket(server, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
    
            lamportClock.incrementAndGet();  // Increment Lamport clock
    
            String jsonDataString = jsonData.toString(4);  // Pretty print JSON
            System.out.println("Sending JSON data:\n" + jsonDataString);  // Log data
    
            // Send PUT request headers and body
            sendRequestHeaders(out, jsonDataString);
            out.println(jsonDataString);  // Send the JSON body
            out.flush();  // Ensure everything is sent
    
            // Read and print the server response
            logServerResponse(in);
    
        } catch (IOException e) {
            throw new IOException("Error sending PUT request: " + e.getMessage(), e);
        }
    }
    
    private static void sendRequestHeaders(PrintWriter out, String jsonDataString) {
        out.println("PUT /weather.json HTTP/1.1");
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + jsonDataString.length());
        out.println();  // End of headers
    }
    
    private static void logServerResponse(BufferedReader in) throws IOException {
        String responseLine;
        while ((responseLine = in.readLine()) != null) {
            System.out.println(responseLine);  // Log the server response
        }
    }
    
    private static void logError(String message, Exception e) {
        System.err.println(message);
        e.printStackTrace();
    }
    

    //Parse weather data from the input file
    private static JSONObject parseWeatherFile(String filePath) {
        JSONObject json = new JSONObject();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);  // Split on the first colon only
                if (parts.length == 2) {
                    json.put(parts[0].trim(), parts[1].trim());
                }
            }
            System.out.println("Parsed JSON data: " + json.toString(4));  // Print the generated JSON
            return json;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

