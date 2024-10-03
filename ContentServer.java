// import java.io.*;
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

        try (Socket socket = new Socket(server, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            lamportClock.incrementAndGet();
            JSONObject jsonData = parseWeatherFile(dataFile);

            if (jsonData != null) {
                String jsonDataString = jsonData.toString(4); // Pretty print JSON
                System.out.println("Sending JSON data:\n" + jsonDataString);  // Log what we're sending

                // Send PUT request
                out.println("PUT /weather.json HTTP/1.1");
                out.println("Content-Type: application/json");
                out.println("Content-Length: " + jsonDataString.length());
                out.println();  // End of headers
                out.println(jsonDataString);  // Send the actual JSON body
                out.flush();  // Ensure everything is sent

                // Read and print the server response
                String responseLine;
                while ((responseLine = in.readLine()) != null) {
                    System.out.println(responseLine);  // Log the server response
                }
            } else {
                System.out.println("Error: Failed to parse the weather data file.");
            }

        } catch (IOException e) {
            System.err.println("Error: Unable to connect to the server.");
            e.printStackTrace();
        }
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

