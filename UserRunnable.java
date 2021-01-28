import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class UserRunnable implements Runnable {
    private Socket socket;
    /**
     * The username of the User
     */
    public String name;
    /**
     * The Room the User is currently connected to
     */
    public Room room;
    /**
     * The output for the User
     */
    public PrintWriter out;

    /**
     * Creates a new User with the provided Socket connection and assigns it an
     * empty name
     * 
     * @param s the provided Socket connection
     */
    public UserRunnable(Socket s) {
        socket = s;
        name = "";
    }

    /**
     * Sends a message to all users in the Room
     * 
     * @param message the message to broadcast
     */
    public void broadcastMessage(String message) {
        if (room == null) {
            out.println("You are not in a room!\r\nJoin or create one with \"!join [new] <roomName>\".");
        } else if (message != null && !message.isBlank()) {
            room.broadcastMessage(message);
        }
    }

    /**
     * Joins a room of the provided ID, if it exists
     * 
     * @param id
     */
    private void joinRoom(String id) {
        joinRoom(Server.getRoom(id));
    }

    /**
     * Joins the provided Room
     * 
     * @param r the Room to join
     */
    private void joinRoom(Room r) {
        if (name.isBlank()) {
            out.println("You have not chosen a username!\r\nSelect a username with \"!username <username>\"");
        } else if (r == null) {
            out.println(
                    "That room does not exist!\r\nIf you'd like to create a room use \"!join new <roomName>\" instead.\r\nYou may also list all available rooms with \"LIST ROOMS\".");
        } else if (r.add(this)) {
            if (room != null)
                leaveRoom();
            room = r;
            out.printf("You joined room: %s\r\n", room.id);
            broadcastMessage(String.format("%s joined the room.", name));
        } else {
            out.println("Unable to join room! It's most likely closed.");
        }
    }

    /**
     * Leaves the current Room, if the User is in one
     */
    private void leaveRoom() {
        if (room != null) {
            room.remove(this);
            broadcastMessage(String.format("%s left the room.", name));
            out.printf("You left room %s.\r\n", room.id);
            room = null;
        }
    }

    /**
     * Lists all available Rooms in the Server
     */
    private void listRooms() {
        Room[] rooms = Server.getRooms();
        if (rooms.length == 0) {
            out.println("No rooms are available!\r\nCreate one with \"!join new <roomName>\".");
        } else {
            StringBuilder builder = new StringBuilder();
            for (Room r : Server.getRooms()) {
                builder.append(String.format("\r\n%s", r.id));
            }
            out.printf("Rooms in server: %s\r\n", builder);
        }
    }

    /**
     * Lists all Users in the Room, if in one
     */
    private void listUsers() {
        if (room == null) {
            out.println("You are not in a room!\r\nEnter or create one with \"!join [new] <roomName>\".");
        } else {
            StringBuilder builder = new StringBuilder();
            for (UserRunnable user : room.listUsers()) {
                builder.append(String.format("\r\n%s", user.name));
            }
            out.printf("Users in %s:%s\r\n", room.id, builder);
        }
    }

    /**
     * Sets the username
     * 
     * @param name the new username to use
     */
    private void setUsername(String name) {
        if (name == null || name.isBlank() || Server.getUser(name) != null) {
            out.println("Please select a valid username! It is either taken or invalid.");
        }
        this.name = name;
        out.printf("Set your username to: \"%s\"\r\n", name);
    }

    /**
     * Creates a new Room if it does not already exist and joins it
     * 
     * @param name the name of the Room to create
     */
    private void createRoom(String name) {
        if (this.name.isBlank()) {
            out.println("You have not chosen a username!\r\nSelect a username with \"!username <username>\"");
        } else {
            Room newRoom = Server.createRoom(name);
            if (newRoom == null) {
                out.println("That is an invalid name!\r\nEither the name is empty or already in use.");
            } else {
                joinRoom(newRoom);
            }
        }
    }

    /**
     * Procedure to be followed by each Thread running the User. Gets the IO of the
     * socket, greets the user, and awaits/handles user commands
     */
    public void run() {
        try (Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);) {
            this.out = out;
            this.out.printf(
                    "Welcome! You are connected to the Chat Room server.\r\nThere are %d users online.\r\nEnter \"!help\" to view available commands.\r\n",
                    Server.totalUsers());
            while (!Thread.interrupted()) {
                if (in.hasNextLine()) {
                    String input = in.nextLine().trim();
                    String[] args = input.split(" ");
                    if (args.length < 3) {
                        args = Arrays.copyOf(args, 3);
                        for (int i = 0; i < args.length; i++) {
                            if (args[i] == null) {
                                args[i] = "";
                            }
                        }
                    }
                    switch (args[0].toLowerCase()) {
                        case "!username":
                            setUsername(String.join(" ", Arrays.copyOfRange(args, 1, args.length)).trim());
                            break;
                        case "!join":
                            switch (args[1].toLowerCase()) {
                                case "new":
                                    createRoom(String.join(" ", Arrays.copyOfRange(args, 2, args.length)).trim());
                                    break;
                                default:
                                    joinRoom(String.join(" ", Arrays.copyOfRange(args, 1, args.length)).trim());
                            }
                            break;
                        case "!leave":
                            leaveRoom();
                            break;
                        case "!logout":
                            Thread.currentThread().interrupt();
                            break;
                        case "!list":
                            switch (args[1].toLowerCase()) {
                                case "rooms":
                                    listRooms();
                                    break;
                                case "users":
                                    listUsers();
                                    break;
                                default:
                                    if (room == null)
                                        listRooms();
                                    else
                                        listUsers();
                            }
                            break;
                        case "!help":
                            switch (args[1].toLowerCase()) {
                                case "username":
                                    out.println("Set your username.\r\nUsage: \"!username <name>\"");
                                    break;
                                case "join":
                                    out.println("Join a room.\r\nUsage: \"!join <room ID>\"");
                                    break;
                                case "leave":
                                    out.println(
                                            "Leaves the room you are currently in. Does not log you out.\r\nUsage: \"!leave\"");
                                    break;
                                case "logout":
                                    out.println("Leaves the current room and disconnects you.\r\nUsage: \"!logout\"");
                                    break;
                                case "list":
                                    out.println(
                                            "Lists the current rooms or users.\r\nUsage: \"!list rooms\", \"!list users\"");
                                    break;
                                default:
                                    out.println(
                                            "Current available commands (all commands start with \"!\"):\r\nusername\r\njoin\r\nleave\r\nlogout\r\nlist\r\nEnter\"!help [command name]\" for more information.");
                            }
                            break;
                        default:
                            broadcastMessage(String.format("%s: %s", name, input.trim()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                leaveRoom();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Server.removeUser(this);
            }
        }
    }
}