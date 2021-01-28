import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A container class for the server program, contains multiple Rooms and accepts
 * new Users
 */
public class Server {
    /**
     * The port the server is running on
     */
    public static final int PORT = 4338;
    
    private static final ArrayList<Room> rooms = new ArrayList<Room>();
    private static Lock roomListAccessLock = new ReentrantLock();
    private static ArrayList<UserRunnable> users = new ArrayList<UserRunnable>();
    private static Lock userListAccessLock = new ReentrantLock();

    public static Room[] getRooms() {
        roomListAccessLock.lock();
        Room[] roomsArr = new Room[rooms.size()];
        rooms.toArray(roomsArr);
        roomListAccessLock.unlock();
        return roomsArr;
    }

    public static Room getRoom(String id) {
        roomListAccessLock.lock();
        for (Room room : rooms) {
            if (room.id.equals(id)) {
                roomListAccessLock.unlock();
                return room;
            }
        }
        roomListAccessLock.unlock();
        return null;
    }

    public static Room createRoom(String id) {
        if (id == null || getRoom(id) != null || id.isBlank())
            return null;
        Room room = new Room(id);
        roomListAccessLock.lock();
        rooms.add(room);
        roomListAccessLock.unlock();
        return room;
    }

    public static void removeRoom(Room room) {
        roomListAccessLock.lock();
        rooms.remove(room);
        roomListAccessLock.unlock();
    }

    public static void removeUser(UserRunnable user) {
        userListAccessLock.lock();
        users.remove(user);
        userListAccessLock.unlock();
    }

    public static UserRunnable getUser(String name) {
        userListAccessLock.lock();
        for (UserRunnable user : users) {
            if (user.name.equals(name)) {
                userListAccessLock.unlock();
                return user;
            }
        }
        userListAccessLock.unlock();
        return null;
    }

    public static int totalUsers() {
        userListAccessLock.lock();
        int total = users.size();
        userListAccessLock.unlock();
        return total;
    }

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                UserRunnable user = new UserRunnable(server.accept());
                userListAccessLock.lock();
                users.add(user);
                userListAccessLock.unlock();
                Thread t = new Thread(user);
                t.start();
            }
        } catch (Exception e) {
            System.out.println("Server closed unexpectedly!");
            e.printStackTrace();
        }
    }
}
