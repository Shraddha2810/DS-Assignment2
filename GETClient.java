import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicLong;

public class GETClient {
    private static final AtomicLong lamportClock = new AtomicLong(0);

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java GETClient <server> <port>");
            return;
        }

        String server = args[0];
        int port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(server, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            lamportClock.incrementAndGet();
            out.println("GET /weather.json HTTP/1.1");
            out.println(); // Send an empty line to terminate the GET request

            String responseLine;
            while ((responseLine = in.readLine()) != null) {
                System.out.println(responseLine);
            }

        } catch (IOException e) {
            System.err.println("Error: Unable to connect to the server.");
            e.printStackTrace();
        }
    }
}
