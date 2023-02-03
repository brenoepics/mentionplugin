package tech.brenoepic.core.modes;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
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

public class MentionFriends implements IMention {
    private final MentionTimeout timer;
    private final String prefix;
    private final String permission;

    public MentionFriends(String permission, String prefix) {
        this.permission = permission;
        this.prefix = prefix;
        timer = new MentionTimeout();
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public boolean allowRoomOwner() {
        return false;
    }

    @Override
    public int mentionsPerMessage() {
        return 1;
    }

    @Override
    public MentionTimeout getTimer() {
        return timer;
    }

    @Override
    public boolean execute(Habbo sender, String receiver, String message) throws MentionException {
        if(!Variables.FRIENDS_PREFIX.equalsIgnoreCase(receiver)) return false;
        Timeout canMention = timer.canMention(sender.getHabboInfo().getId());

        if (canMention != null) {
            throw new MentionException("CANT_MENTION", new MentionExceptionCause(String.valueOf(canMention.getFinish().minusMillis(System.currentTimeMillis()).getEpochSecond())));
        }

        if (!Emulator.getConfig().getBoolean("commands.cmd_mention_friends.message.show_username.enabled", true))
            message = message.replace("@" + receiver, "");

        if (Variables.SANITIZE)
            message = Sanitize(message);

        message = Emulator.getTexts().getValue("commands.cmd_mention_friends.message").replace("%MESSAGE%", message).replace("%SENDER%", sender.getHabboInfo().getUsername());
        THashMap<String, String> alert = BubbleAlert(sender, message, Emulator.getConfig().getValue("commands.cmd_mention.look").replace("%look%", sender.getHabboInfo().getLook()), Emulator.getConfig().getBoolean("commands.cmd_mention.friends.follow.enabled", true));
        for (MessengerBuddy player : sender.getMessenger().getFriends().values()) {
            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(player.getId());
            if (player.getOnline() == 0 || habbo == null || habbo.getHabboStats().blockRoomInvites)
                continue;

            if (Variables.FRIENDS_MODE == 1) {
                habbo.getClient().sendResponse(new BubbleAlertComposer("mention", alert));
            } else if (Variables.FRIENDS_MODE == 2 && habbo.getHabboInfo().getCurrentRoom() != null) {
                habbo.whisper(alert.get("message"), RoomChatMessageBubbles.ALERT);
            }
        }
        return true;
    }
}