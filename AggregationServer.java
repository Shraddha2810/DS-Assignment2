// import java.io.*;
// import java.net.*;
// import java.util.concurrent.*;
// import org.json.JSONObject;
// import java.util.concurrent.atomic.AtomicLong;

// public class AggregationServer {
//     private static final int DEFAULT_PORT = 4567;
//     private static final int EXPIRATION_TIME_MS = 30000;
//     private static final ConcurrentHashMap<String, WeatherData> dataStore = new ConcurrentHashMap<>();
//     private static final AtomicLong lamportClock = new AtomicLong(0);

//     public static void main(String[] args) {
//         int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
//         try (ServerSocket serverSocket = new ServerSocket(port)) {
//             System.out.println("Aggregation server running on port " + port);
//             startDataExpunger();

//             while (true) {
//                 Socket clientSocket = serverSocket.accept();
//                 new Thread(() -> handleClientRequest(clientSocket)).start();
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     private static void handleClientRequest(Socket socket) {
//         try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//              PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

//             String request = in.readLine();
//             if (request.startsWith("GET")) {
//                 handleGetRequest(out);
//             } else if (request.startsWith("PUT")) {
//                 handlePutRequest(in, out);
//             } else {
//                 out.println("HTTP/1.1 400 Bad Request");
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     private static void handleGetRequest(PrintWriter out) {
//         lamportClock.incrementAndGet();
//         JSONObject jsonResponse = new JSONObject();
//         for (WeatherData data : dataStore.values()) {
//             jsonResponse.put(data.getWeatherJson().getString("id"), data.getWeatherJson());
//         }

//         out.println("HTTP/1.1 200 OK");
//         out.println("Content-Type: application/json");
//         out.println();
//         out.println(jsonResponse.toString(4));
//     }

//     private static void handlePutRequest(BufferedReader in, PrintWriter out) throws IOException {
//         lamportClock.incrementAndGet();
//         String line;
//         int contentLength = 0;

//         // Step 1: Read headers to find Content-Length
//         while (!(line = in.readLine()).isEmpty()) {
//             if (line.startsWith("Content-Length:")) {
//                 contentLength = Integer.parseInt(line.split(":")[1].trim());
//             }
//         }

//         // Log the content length and validate
//         if (contentLength == 0) {
//             System.out.println("Error: Content-Length is 0. No data to read.");
//             out.println("HTTP/1.1 400 Bad Request");
//             return;
//         }

//         // Step 2: Read the body based on Content-Length
//         char[] body = new char[contentLength];
//         in.read(body, 0, contentLength);
//         String jsonString = new String(body);

//         try {
//             // Parse the JSON data
//             System.out.println("Received PUT request with JSON: " + jsonString);  // Debug output
//             JSONObject jsonObject = new JSONObject(jsonString);
//             String id = jsonObject.getString("id");

//             // Store the weather data with a timestamp
//             dataStore.put(id, new WeatherData(jsonObject, System.currentTimeMillis()));
//             System.out.println("Storing weather data with ID: " + id);

//             // Send success response
//             out.println("HTTP/1.1 201 Created");
//         } catch (Exception e) {
//             // Log the error if something goes wrong
//             System.err.println("Error processing PUT request: " + e.getMessage());
//             e.printStackTrace();
//             out.println("HTTP/1.1 500 Internal Server Error");
//         }
//     }

//     // A scheduled task that removes old data after 30 seconds
//     private static void startDataExpunger() {
//         ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//         scheduler.scheduleAtFixedRate(() -> {
//             long currentTime = System.currentTimeMillis();
//             dataStore.entrySet().removeIf(entry -> (currentTime - entry.getValue().getTimestamp()) > EXPIRATION_TIME_MS);
//         }, 0, 5, TimeUnit.SECONDS);
//     }

//     // Class to hold weather data with a timestamp
//     static class WeatherData {
//         private final JSONObject weatherJson;
//         private final long timestamp;

//         public WeatherData(JSONObject weatherJson, long timestamp) {
//             this.weatherJson = weatherJson;
//             this.timestamp = timestamp;
//         }

//         public JSONObject getWeatherJson() {
//             return weatherJson;
//         }

//         public long getTimestamp() {
//             return timestamp;
//         }
//     }
// }



// import java.io.*;
// import java.net.*;
// import java.util.concurrent.*;
// import org.json.JSONObject;
// import java.util.concurrent.atomic.AtomicLong;

// public class AggregationServer {
//     private static final int DEFAULT_PORT = 4567;
//     private static final int EXPIRATION_TIME_MS = 30000;
//     private static final ConcurrentHashMap<String, WeatherData> dataStore = new ConcurrentHashMap<>();
//     private static final AtomicLong lamportClock = new AtomicLong(0);

//     public static void main(String[] args) {
//         int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
//         try (ServerSocket serverSocket = new ServerSocket(port)) {
//             System.out.println("Aggregation server running on port " + port);
//             startDataExpunger();

//             while (true) {
//                 Socket clientSocket = serverSocket.accept();
//                 new Thread(() -> handleClientRequest(clientSocket)).start();
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     private static void handleClientRequest(Socket socket) {
//         try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//              PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

//             String request = in.readLine();
//             System.out.println("Received request: " + request);  // Log incoming requests

//             if (request.startsWith("GET")) {
//                 handleGetRequest(out);
//             } else if (request.startsWith("PUT")) {
//                 handlePutRequest(in, out);
//             } else {
//                 out.println("HTTP/1.1 400 Bad Request");
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     private static void handleGetRequest(PrintWriter out) {
//         lamportClock.incrementAndGet();
//         JSONObject jsonResponse = new JSONObject();
//         for (WeatherData data : dataStore.values()) {
//             jsonResponse.put(data.getWeatherJson().getString("id"), data.getWeatherJson());
//         }

//         System.out.println("Returning JSON response: " + jsonResponse.toString(4));  // Log response
//         out.println("HTTP/1.1 200 OK");
//         out.println("Content-Type: application/json");
//         out.println();
//         out.println(jsonResponse.toString(4));
//     }

//     private static void handlePutRequest(BufferedReader in, PrintWriter out) throws IOException {
//         lamportClock.incrementAndGet();
//         String line;
//         int contentLength = 0;

//         // Step 1: Read headers to find Content-Length
//         while (!(line = in.readLine()).isEmpty()) {
//             if (line.startsWith("Content-Length:")) {
//                 contentLength = Integer.parseInt(line.split(":")[1].trim());
//             }
//         }

//         // Log the content length and validate
//         System.out.println("Content-Length received: " + contentLength);  // Log content length

//         if (contentLength == 0) {
//             System.out.println("Error: Content-Length is 0. No data to read.");
//             out.println("HTTP/1.1 400 Bad Request");
//             return;
//         }

//         // Step 2: Read the body based on Content-Length
//         char[] body = new char[contentLength];
//         in.read(body, 0, contentLength);
//         String jsonString = new String(body);

//         try {
//             // Parse the JSON data
//             System.out.println("Received PUT request with JSON: " + jsonString);  // Log received data
//             JSONObject jsonObject = new JSONObject(jsonString);
//             String id = jsonObject.getString("id");

//             // Store the weather data with a timestamp
//             dataStore.put(id, new WeatherData(jsonObject, System.currentTimeMillis()));
//             System.out.println("Storing weather data with ID: " + id);

//             // Send success response
//             out.println("HTTP/1.1 201 Created");
//         } catch (Exception e) {
//             // Log the error if something goes wrong
//             System.err.println("Error processing PUT request: " + e.getMessage());
//             e.printStackTrace();
//             out.println("HTTP/1.1 500 Internal Server Error");
//         }
//     }

//     // A scheduled task that removes old data after 30 seconds
//     private static void startDataExpunger() {
//         ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//         scheduler.scheduleAtFixedRate(() -> {
//             long currentTime = System.currentTimeMillis();
//             dataStore.entrySet().removeIf(entry -> (currentTime - entry.getValue().getTimestamp()) > EXPIRATION_TIME_MS);
//         }, 0, 5, TimeUnit.SECONDS);
//     }

//     // Class to hold weather data with a timestamp
//     static class WeatherData {
//         private final JSONObject weatherJson;
//         private final long timestamp;

//         public WeatherData(JSONObject weatherJson, long timestamp) {
//             this.weatherJson = weatherJson;
//             this.timestamp = timestamp;
//         }

//         public JSONObject getWeatherJson() {
//             return weatherJson;
//         }

//         public long getTimestamp() {
//             return timestamp;
//         }
//     }
// }

















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
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Aggregation server running on port " + port);
            startDataExpunger();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClientRequest(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleClientRequest(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String request = in.readLine();
            System.out.println("Received request: " + request);  // Log incoming requests

            if (request != null && request.startsWith("GET")) {
                handleGetRequest(out);
            } else if (request != null && request.startsWith("PUT")) {
                handlePutRequest(in, out);
            } else {
                out.println("HTTP/1.1 400 Bad Request");
            }
        } catch (IOException e) {
            System.err.println("Error handling client request: " + e.getMessage());
            e.printStackTrace();
        }
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
        String line;
        int contentLength = 0;

        // Step 1: Read headers to find Content-Length
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        // Log the content length and validate
        System.out.println("Content-Length received: " + contentLength);

        if (contentLength == 0) {
            System.err.println("Error: Content-Length is 0. No data to read.");
            out.println("HTTP/1.1 400 Bad Request");
            return;
        }

        // Step 2: Read the body based on Content-Length
        char[] body = new char[contentLength];
        int bytesRead = in.read(body, 0, contentLength);
        if (bytesRead != contentLength) {
            System.err.println("Error: Mismatch in Content-Length and bytes read.");
            out.println("HTTP/1.1 400 Bad Request");
            return;
        }

        String jsonString = new String(body);

        try {
            // Parse the JSON data
            System.out.println("Received PUT request with JSON: " + jsonString);
            JSONObject jsonObject = new JSONObject(jsonString);
            String id = jsonObject.getString("id");

            // Store the weather data with a timestamp
            dataStore.put(id, new WeatherData(jsonObject, System.currentTimeMillis()));
            System.out.println("Storing weather data with ID: " + id);

            // Send success response
            out.println("HTTP/1.1 201 Created");
        } catch (Exception e) {
            // Log the error if something goes wrong
            System.err.println("Error processing PUT request: " + e.getMessage());
            e.printStackTrace();
            out.println("HTTP/1.1 500 Internal Server Error");
        }
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
