import java.util.ArrayList;

public class Room {
    
    private UserRunnable owner;
    public final String id;
    private ArrayList<UserRunnable> users;

    public Room(UserRunnable owner, String id) {
        this.owner = owner;
        this.id = id;
        users = new ArrayList<UserRunnable>();
        add(this.owner);
    }

    public void add(UserRunnable user) {
        users.add(user);
    }
}
