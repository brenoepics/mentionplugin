package tech.brenoepic.core.types.timeout;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

/**
 * A class that implements timeout behavior for mentions.
 *
 * @author BrenoEpic#9671
 */
public class MentionTimeout {

    /**
     * A hashmap to store the mentions timeout information, where the key is the user id and the value is a Timeout object.
     */
    private final HashMap<Integer, Timeout> users;

    /**
     * Creates a new instance of MentionTimeout
     */
    public MentionTimeout() {
        this.users = new HashMap<>();
    }

    /**
     * Clears all timeout data.
     */
    public void dispose() {
        this.users.clear();
    }

    /**
     * Checks if a user is able to mention.
     *
     * @param user The id of the user to be checked.
     * @return Returns the Timeout object if the user is not able to mention, otherwise returns null.
     */
    public Timeout canMention(int user) {
        if (this.users.containsKey(user)) {
            Timeout timeout = this.users.get(user);
            if (timeout.getFinish().isAfter(Instant.now())) {
                return timeout;
            }
            this.users.remove(user);
        }
        return null;
    }

    /**
     * Adds a new timeout for a user.
     *
     * @param user The id of the user.
     * @param time The duration of the timeout in seconds.
     */
    public void NewTimer(int user, int time) {
        this.users.put(user, new Timeout(user, Instant.now().plusSeconds(time)));
    }
}