import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import org.json.JSONObject;
import java.util.concurrent.atomic.AtomicLong;

public class AggregationServer {
    private static final int DEFAULT_PORT = 4567;
    private static final int EXPIRATION_TIME_MS = 30000;
    private static final ConcurrentHashMap<String, WeatherData> dataStore = new ConcurrentHashMap<>();
    private static final AtomicLong lamportClock = new AtomicLong(0);

        public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
    
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Aggregation server running on port " + port);
    
           
            startDataExpunger();
    
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Runnable clientHandler = () -> handleClientRequest(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Failed to start the server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
   
    private static void handleClientRequest(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    
            String request = in.readLine();
            logRequest(request);  // Separate method for logging
    
            if (request != null) {
                if (request.startsWith("GET")) {
                    handleGetRequest(out);
                } else if (request.startsWith("PUT")) {
                    handlePutRequest(in, out);
                } else {
                    sendBadRequestResponse(out);
                }
            }
    
        } catch (IOException e) {
            logError("Error handling client request: ", e);
        } finally {
            try {
                socket.close();  // Ensure socket is closed after handling
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }
    
    private static void logRequest(String request) {
        System.out.println("Received request: " + request);
    }
    
    private static void sendBadRequestResponse(PrintWriter out) {
        out.println("HTTP/1.1 400 Bad Request");
    }
    
    
    private static void handleGetRequest(PrintWriter out) {
        lamportClock.incrementAndGet();
        JSONObject jsonResponse = new JSONObject();
        for (WeatherData data : dataStore.values()) {
            jsonResponse.put(data.getWeatherJson().getString("id"), data.getWeatherJson());
        }

        System.out.println("Returning JSON response: " + jsonResponse.toString(4));  // Log response
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: application/json");
        out.println();
        out.println(jsonResponse.toString(4));
    }

    
    private static void handlePutRequest(BufferedReader in, PrintWriter out) throws IOException {
        lamportClock.incrementAndGet();
    
        try {
            int contentLength = getContentLengthFromHeaders(in);
    
            if (contentLength == 0) {
                handleBadRequest(out, "Content-Length is 0. No data to read.");
                return;
            }
    
            String requestBody = readRequestBody(in, contentLength);
    
            if (requestBody == null) {
                handleBadRequest(out, "Mismatch in Content-Length and bytes read.");
                return;
            }
    
            processPutRequest(requestBody, out);
    
        } catch (IOException e) {
            logError("Error processing PUT request: ", e);
            out.println("HTTP/1.1 500 Internal Server Error");
        }
    }
    
    private static int getContentLengthFromHeaders(BufferedReader in) throws IOException {
        String line;
        int contentLength = 0;
    
        // Read headers and find Content-Length
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }
        System.out.println("Content-Length received: " + contentLength);
    
        return contentLength;
    }
    
    private static String readRequestBody(BufferedReader in, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        int bytesRead = in.read(body, 0, contentLength);
    
        if (bytesRead != contentLength) {
            return null;  // Mismatch in content length and bytes read
        }
        return new String(body);
    }
    
    
    private static void processPutRequest(String jsonString, PrintWriter out) {
        try {
            System.out.println("Received PUT request with JSON: " + jsonString);
    
            // Check for empty request body
            if (jsonString.trim().isEmpty()) {
                handleBadRequest(out, "Received empty PUT request.");
                return;
            }
    
            JSONObject jsonObject = new JSONObject(jsonString);
    
            // Ensure the required "id" field is present
            if (!jsonObject.has("id") || jsonObject.getString("id").isEmpty()) {
                handleBadRequest(out, "Missing or empty 'id' field in the data.");
                return;
            }
    
            // Validate that there are enough fields in the weather data
            if (jsonObject.length() < 3) {  // Assuming at least 3 fields are mandatory
                handleBadRequest(out, "Insufficient data in the PUT request.");
                return;
            }
    
            String id = jsonObject.getString("id");
            dataStore.put(id, new WeatherData(jsonObject, System.currentTimeMillis()));  // Store the weather data
    
            System.out.println("Storing weather data with ID: " + id);
            out.println("HTTP/1.1 201 Created");
    
        } catch (Exception e) {
            System.err.println("Error processing PUT request: " + e.getMessage());
            e.printStackTrace();
            out.println("HTTP/1.1 500 Internal Server Error");
        }
    }
    
    private static void handleBadRequest(PrintWriter out, String message) {
        System.err.println("Error: " + message);
        out.println("HTTP/1.1 400 Bad Request");
        out.println("Error-Message: " + message);
    }
    
    
    private static void logError(String message, Exception e) {
        System.err.println(message + e.getMessage());
        e.printStackTrace();
    }
    

    // A scheduled task that removes old data after 30 seconds
    private static void startDataExpunger() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            dataStore.entrySet().removeIf(entry -> (currentTime - entry.getValue().getTimestamp()) > EXPIRATION_TIME_MS);
        }, 0, 5, TimeUnit.SECONDS);
    }

    // Class to hold weather data with a timestamp
    static class WeatherData {
        private final JSONObject weatherJson;
        private final long timestamp;

        public WeatherData(JSONObject weatherJson, long timestamp) {
            this.weatherJson = weatherJson;
            this.timestamp = timestamp;
        }

        public JSONObject getWeatherJson() {
            return weatherJson;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}









