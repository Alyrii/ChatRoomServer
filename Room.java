import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A class representing a chatroom with multiple users
 */
public class Room {
    /**
     * The name of the Room
     */
    public final String id;
    
    private ArrayList<UserRunnable> users;
    private Lock userListAccessLock;
    private boolean dead;

    /**
     * Creates a Room with the specified ID and a new list of Users
     * 
     * @param id the name for the Room
     */
    public Room(String id) {
        this.id = id;
        dead = false;
        userListAccessLock = new ReentrantLock();
        users = new ArrayList<UserRunnable>();
    }

    /**
     * Adds a new User to the Room
     * 
     * @param user the User to add
     * @return if the User was added or not
     */
    public boolean add(UserRunnable user) {
        userListAccessLock.lock();
        if (!dead)
            users.add(user);
        userListAccessLock.unlock();
        return !dead;
    }

    /**
     * Removes a User from the Room
     * 
     * @param user the User to remove
     */
    public void remove(UserRunnable user) {
        userListAccessLock.lock();
        users.remove(user);
        if (users.size() == 0) {
            Server.removeRoom(this);
            dead = true;
        }
        userListAccessLock.unlock();
    }

    /**
     * Lists all users in the room
     * 
     * @return an array of Users in the room at the time of invocation
     */
    public UserRunnable[] listUsers() {
        userListAccessLock.lock();
        UserRunnable[] usersArr = new UserRunnable[users.size()];
        users.toArray(usersArr);
        userListAccessLock.unlock();
        return usersArr;
    }

    /**
     * Sends a message to all Users in the Room
     * 
     * @param message the message to send
     */
    public void broadcastMessage(String message) {
        userListAccessLock.lock();
        for (UserRunnable user : users) {
            user.out.println(message);
        }
        userListAccessLock.unlock();
    }
}
