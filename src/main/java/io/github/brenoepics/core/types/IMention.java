package io.github.brenoepics.core.types;

import com.eu.habbo.habbohotel.users.Habbo;
import io.github.brenoepics.core.types.timeout.MentionTimeout;
import io.github.brenoepics.core.types.exception.MentionException;

public interface IMention {
    /**
     * Gets the command prefix.
     *
     * @return The prefix for the mention command.
     */
    String getPrefix();

    /**
     * Gets the permission required to use the command.
     *
     * @return The permission required to use the command.
     */
    String getPermission();

    /**
     * Indicates if room owners are allowed to use the command.
     *
     * @return True if room owners are allowed to use the command, false otherwise.
     */
    boolean allowRoomOwner();

    /**
     * Gets the timer used to track mentions.
     *
     * @return The timer used to track mentions.
     */
    MentionTimeout getTimer();

    /**
     * Executes the mention command.
     *
     * @param sender The user who sent the command.
     * @param receiver The user who should receive the mention.
     * @param message The message to send with the mention.
     * @return True if the command was executed successfully, false otherwise.
     * @throws MentionException If an error occurs while executing the command.
     */
    boolean execute(Habbo sender, String receiver, String message) throws MentionException;
}