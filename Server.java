import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {

    public static final int PORT = 4338;
    private static final ArrayList<Room> rooms = new ArrayList<Room>();
    private static Lock roomListAccessLock = new ReentrantLock();

    /**
     * 
     * @return
     */
    public static Room[] getRooms() {
        roomListAccessLock.lock();
        Room[] roomsArr = (Room[]) rooms.toArray();
        roomListAccessLock.unlock();
        return roomsArr;
    }

    /**
     * 
     * @param id
     * @return
     */
    public static Room getRoom(String id) {
        roomListAccessLock.lock();
        Room room = null;
        for(Room r : rooms) {
            if(r.id.equals(id)) {
                room = r;
            }
        }
        roomListAccessLock.unlock();
        return room;
    }

    /**
     * 
     * @param user
     * @param id
     * @return
     */
    public static Room createRoom(UserRunnable user, String id) {
        if(getRoom(id) != null) return null;
        Room room = new Room(user, id);
        roomListAccessLock.lock();
        rooms.add(room);
        roomListAccessLock.unlock();
        return room;
    }

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            while(true) {
                Thread t = new Thread(new UserRunnable(server.accept()));
                t.run();
            }
        } catch (Exception e) {
            System.out.println("Server closed unexpectedly!");
        }
    }
}
