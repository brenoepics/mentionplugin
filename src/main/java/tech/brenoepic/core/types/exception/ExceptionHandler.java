package tech.brenoepic.core.types.exception;

import com.eu.habbo.Emulator;
/**
 * ExceptionHandler is a utility class responsible for handling `MentionException` exceptions.
 * <p>
 * The handle method takes in a `MentionException` object and returns a message based on the exception message.
 * The messages are looked up in the Emulator's texts object. If the exception has a cause, it is included in the message.
 */
public class ExceptionHandler {
    public static String handle(MentionException e) {
        String message;
        switch (e.getMessage()) {
            case "CANT_MENTION":
                message = "commands.cmd_mention.timeout_message";
                break;
            case "SELF_MENTION":
                message = "commands.error.cmd_mention.not_self";
                break;
            case "USER_NOT_FOUND":
                message = "commands.error.cmd_mention.user_not_found";
                break;
            case "USER_MENTION_BLOCKED":
                message = "commands.error.cmd_mention.user_mention_blocked";
                break;
            default:
                message = "commands.error.cmd_mention.unknown_error";
                break;
        }

        return e.getCause() == null ? Emulator.getTexts().getValue(message) : Emulator.getTexts().getValue(message).replace("%var%", e.getCause().getMessage());
    }
}
