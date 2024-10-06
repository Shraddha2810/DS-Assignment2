// import java.io.*;
// import java.net.*;
// import java.util.concurrent.atomic.AtomicLong;

// public class GETClient {
//         // AtomicLong to represent the Lamport clock for event ordering in distributed systems
//     private static final AtomicLong lamportClock = new AtomicLong(0);

//     public static void main(String[] args) {
//         // Check if the required arguments (server address and port) are provided

//         if (args.length < 2) {
//             System.out.println("Usage: java GETClient <server> <port>");
//             return;
//         }
//         // The server's hostname or IP address
//         String server = args[0];

//         // The port number the server is listening on
//         int port = Integer.parseInt(args[1]);

//         // Try to establish a connection to the server
//         try (Socket socket = new Socket(server, port);
//              PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // To send requests to the server
//              BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {  // To read the server's response

//         // Increment the Lamport clock as we're making an interaction (GET request)
//             lamportClock.incrementAndGet();

//         // Send the HTTP GET request to the server
//             out.println("GET /weather.json HTTP/1.1");
//             out.println(); // Send an empty line to terminate the GET request

            
//         // Read and print the server's response line by line
//             String responseLine;
//             while ((responseLine = in.readLine()) != null) {
//                 System.out.println(responseLine);
//             }

//         } catch (IOException e) {

//             // Handle any errors related to connecting to the server or reading the response
//             System.err.println("Error: Unable to connect to the server.");
//             e.printStackTrace();
//         }
//     }
// }




import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicLong;

public class GETClient {
    
    // Lamport clock for tracking event ordering
    private static final AtomicLong lamportClock = new AtomicLong(0);

    public static void main(String[] args) {
        // Verify command-line arguments
        if (args.length < 2) {
            System.out.println("Usage: java GETClient <server> <port>");
            return;
        }

        // Extract server address and port from arguments
        String server = args[0];
        int port;
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Port must be a valid integer.");
            return;
        }

        Socket socket = null;
        try {
            // Establish a connection to the server
            socket = new Socket(server, port);
            // Initialize input and output streams
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Increment the Lamport clock when sending a request
            lamportClock.incrementAndGet();

            // Send the GET request
            out.println("GET /weather.json HTTP/1.1");
            out.println();  // Empty line to indicate end of headers

            // Read the server's response
            String responseLine;
            while ((responseLine = in.readLine()) != null) {
                System.out.println(responseLine);  // Print each line of the response
            }

        } catch (IOException e) {
            System.err.println("Error connecting to the server: " + e.getMessage());
        } finally {
            // Ensure the socket is closed after use
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Failed to close the socket: " + e.getMessage());
                }
            }
        }
    }
}

