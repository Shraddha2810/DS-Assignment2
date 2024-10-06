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

