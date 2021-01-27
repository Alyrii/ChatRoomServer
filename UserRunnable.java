import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class UserRunnable implements Runnable {
    private Socket socket;
    public String name;
    public Room room;
    private PrintWriter out;

    public UserRunnable(Socket s) {
        socket = s;
    }

    public void broadcastMessage(String message) {

    }
    
    private void sendMessage(String message) {

    }

    private void joinRoom(String id) {
        
    }

    private void leaveRoom() {

    }

    private void listRooms() {

    }

    private void listUsers() {

    }

    private void logout() {

    }

    private void setUsername(String name) {

    }

    private void createRoom(String name) {

    }

    public void run() {
        try (
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream());
        ) {
            this.out = out;
            while(!Thread.interrupted()) {
                if(in.hasNextLine()) {
                    String input = in.nextLine();
                    String[] args = input.split(" ");
                    switch (args[0]) {
                        case "USERNAME":
                            setUsername(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                            break;
                        case "JOIN":
                            switch (args[1]) {
                                case "NEW":
                                    createRoom(String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
                                    break;
                                default:
                                    joinRoom(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                            }
                            break;
                        case "LEAVE":
                            leaveRoom();
                            break;
                        case "LOGOUT":
                            logout();
                            break;
                        case "LIST":
                            switch (args[1]) {
                                case "ROOMS":
                                    listRooms();
                                    break;
                                case "USERS":
                                default:
                                    listUsers();
                            }
                            break;
                        case "MESSAGE":
                        default:
                            broadcastMessage(input);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to create input stream.");
            e.printStackTrace();
        }
    }
    
}