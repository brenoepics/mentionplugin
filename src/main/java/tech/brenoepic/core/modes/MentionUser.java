package tech.brenoepic.core.modes;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import gnu.trove.map.hash.THashMap;
import tech.brenoepic.core.Variables;
import tech.brenoepic.core.types.IMention;
import tech.brenoepic.core.types.exception.MentionException;
import tech.brenoepic.core.types.exception.MentionExceptionCause;
import tech.brenoepic.core.types.timeout.MentionTimeout;
import tech.brenoepic.core.types.timeout.Timeout;

import static tech.brenoepic.utils.Functions.BubbleAlert;
import static tech.brenoepic.utils.Functions.Sanitize;


/**
 * MentionUser is a class that implements the {@link IMention} interface.
 * It is used to handle the functionality of mentioning a specific user in the hotel.
 *
 * @author Brenoepic
 * @version 1.0
 */
public class MentionUser implements IMention {
    private final MentionTimeout timer;
    private final String prefix;
    private final String permission;

    /**
     * Creates a new instance of MentionUser.
     *
     * @param permission The permission required to execute this mention.
     * @param prefix     The prefix used to trigger this mention.
     */
    public MentionUser(String permission, String prefix) {
        this.permission = permission;
        this.prefix = prefix;
        timer = new MentionTimeout();
    }

    /**
     * Gets the prefix used to trigger this mention.
     *
     * @return The prefix used to trigger this mention.
     */
    @Override
    public String getPrefix() {
        return prefix;
    }

    /**
     * Gets the permission required to execute this mention.
     *
     * @return The permission required to execute this mention.
     */
    @Override
    public String getPermission() {
        return permission;
    }

    /**
     * Indicates whether room owners are allowed to use this mention.
     *
     * @return False. Room owners are not allowed to use this mention.
     */
    @Override
    public boolean allowRoomOwner() {
        return false;
    }

    /**
     * Gets the maximum number of mentions allowed per message.
     *
     * @return The maximum number of mentions allowed per message, as defined in the configuration.
     */
    @Override
    public int mentionsPerMessage() {
        return Emulator.getConfig().getInt("commands.cmd_mention_user_max", 5);
    }

    /**
     * Gets the mention timer.
     *
     * @return The mention timer.
     */
    @Override
    public MentionTimeout getTimer() {
        return timer;
    }

    /**
     * Executes this mention for the specified sender and receiver.
     *
     * @param sender   The sender of the mention.
     * @param receiver The receiver of the mention.
     * @param message  The message of the mention.
     * @return True if the mention was successful, false otherwise.
     * @throws MentionException If an error occurs while executing the mention.
     */
    @Override
    public boolean execute(Habbo sender, String receiver, String message) throws MentionException {
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(receiver);
        Timeout canMention = timer.canMention(sender.getHabboInfo().getId());

        if (canMention != null) {
            throw new MentionException("CANT_MENTION", new MentionExceptionCause(String.valueOf(canMention.getFinish().minusMillis(System.currentTimeMillis()).getEpochSecond())));
        }

        if (habbo == null) {
            throw new MentionException("USER_NOT_FOUND");
        }

        if (habbo == sender) {
            throw new MentionException("SELF_MENTION");
        }

        if ((boolean) habbo.getHabboStats().cache.get("blockmention") || habbo.getHabboStats().userIgnored(sender.getHabboInfo().getId())) {
            throw new MentionException("USER_MENTION_BLOCKED", new MentionExceptionCause(habbo.getHabboInfo().getUsername()));
        }

        if (!Emulator.getConfig().getBoolean("commands.cmd_mention.message.show_username.enabled")) {
            message = message.replace("@" + receiver, "");
        }

        if (Variables.SANITIZE) {
            message = Sanitize(message);
        }

        message = Emulator.getTexts().getValue("commands.cmd_mention.message")
                .replace("%MESSAGE%", message)
                .replace("%SENDER%", sender.getHabboInfo().getUsername());

        THashMap<String, String> alert = BubbleAlert(sender, message, Emulator.getConfig().getValue("commands.cmd_mention_everyone.look")
                .replace("%look%", sender.getHabboInfo().getLook()), Emulator.getConfig().getBoolean("commands.cmd_mention_everyone.follow.enabled", true));

        if (Variables.MENTION_MODE == 1 || Variables.MENTION_MODE == 3) {
            habbo.getClient().sendResponse(new BubbleAlertComposer("mention", alert));
        }
        
        if ((Variables.MENTION_MODE == 2 || Variables.MENTION_MODE == 3) && habbo.getHabboInfo().getCurrentRoom() != null) {
            habbo.whisper(alert.get("message"), RoomChatMessageBubbles.ALERT);
        }

        return true;
    }
}