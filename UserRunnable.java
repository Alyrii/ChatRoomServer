import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class UserRunnable implements Runnable {
    private Socket socket;
    public String id;
    public Room room;

    public UserRunnable(Socket s) {
        socket = s;
    }
    
    public void run() {
        try (
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream());
        ) {
            while(!Thread.interrupted()) {
                if(in.hasNextLine()) {
                    in.nextLine().split(" ");
                }
            }
            
        } catch (Exception e) {
            System.out.println("Failed to create IO streams.");
            e.printStackTrace();
        }
    }
}