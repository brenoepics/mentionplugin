package io.github.brenoepics.logging;

import com.eu.habbo.Emulator;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a message sent from one user to another.
 *
 */
@Getter
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
    @Setter
    private String content;

    /**
     * Creates a new instance of the `Message` class with the specified sender ID, recipient name, and message contents.
     *
     * @param fromId The ID of the user sending the message.
     * @param toUser The name of the user receiving the message.
     * @param content The contents of the message.
     */
    public Message(int fromId, String toUser, String content) {
        this.fromId = fromId;
        this.toUser = toUser;
        this.content = content;
        this.timestamp = Emulator.getIntUnixTimestamp();
    }
}
