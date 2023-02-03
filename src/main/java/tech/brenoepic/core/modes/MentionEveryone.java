package tech.brenoepic.core.modes;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import gnu.trove.map.hash.THashMap;
import tech.brenoepic.core.Variables;
import tech.brenoepic.core.types.IMention;
import tech.brenoepic.core.types.timeout.MentionTimeout;

import static tech.brenoepic.utils.Functions.BubbleAlert;
import static tech.brenoepic.utils.Functions.Sanitize;

public class MentionEveryone implements IMention {
    private final MentionTimeout timer;
    private final String prefix;
    private final String permission;

    public MentionEveryone(String permission, String prefix) {
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
    public boolean execute(Habbo sender, String receiver, String message) {
        if (!receiver.equalsIgnoreCase("everyone")) return false;
        if (!Emulator.getConfig().getBoolean("commands.cmd_mention_everyone.message.show_username.enabled"))
            message = message.replace("@" + receiver, "");

        if (Variables.SANITIZE) message = Sanitize(message);

        message = Emulator.getTexts().getValue("commands.cmd_mention_everyone.message").replace("%MESSAGE%", message).replace("%SENDER%", sender.getHabboInfo().getUsername());
        THashMap<String, String> alert = BubbleAlert(sender, message, Emulator.getConfig().getValue("commands.cmd_mention_everyone.look").replace("%look%", sender.getHabboInfo().getLook()), Emulator.getConfig().getBoolean("commands.cmd_mention_everyone.follow.enabled", true));
        for (Habbo habbo : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().values()) {
            if (habbo == null || habbo.getHabboStats().blockStaffAlerts) continue;

            if (Variables.EVERYONE_MODE == 1) {
                habbo.getClient().sendResponse(new BubbleAlertComposer("mention", alert));
            } else if (Variables.EVERYONE_MODE == 2 && habbo.getHabboInfo().getCurrentRoom() != null) {
                habbo.whisper(alert.get("message"), RoomChatMessageBubbles.ALERT);
            }
        }
        return true;
    }


}
