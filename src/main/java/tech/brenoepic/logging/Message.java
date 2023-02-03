package tech.brenoepic.logging;

import com.eu.habbo.Emulator;

/**
 * Represents a message sent from one user to another.
 *
 * @author BrenoEpic#9671
 */
public class Message {
    /**
     * The ID of the user sending the message.
     */
    private final int fromId;

    /**
     * The name of the user receiving the message.
     */
    private final String toUser;

    /**
     * The timestamp when the message was sent.
     */
    private final int timestamp;

    /**
     * The contents of the message.
     */
    private String message;

    /**
     * Creates a new instance of the `Message` class with the specified sender ID, recipient name, and message contents.
     *
     * @param fromId The ID of the user sending the message.
     * @param toUser The name of the user receiving the message.
     * @param message The contents of the message.
     */
    public Message(int fromId, String toUser, String message) {
        this.fromId = fromId;
        this.toUser = toUser;
        this.message = message;

        this.timestamp = Emulator.getIntUnixTimestamp();
    }

    /**
     * Gets the name of the user receiving the message.
     *
     * @return The name of the user receiving the message.
     */
    public String getToUser() {
        return this.toUser;
    }

    /**
     * Gets the ID of the user sending the message.
     *
     * @return The ID of the user sending the message.
     */
    public int getFromId() {
        return this.fromId;
    }

    /**
     * Gets the contents of the message.
     *
     * @return The contents of the message.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Sets the contents of the message.
     *
     * @param message The contents of the message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the timestamp when the message was sent.
     *
     * @return The timestamp when the message was sent.
     */
    public int getTimestamp() {
        return this.timestamp;
    }
}
