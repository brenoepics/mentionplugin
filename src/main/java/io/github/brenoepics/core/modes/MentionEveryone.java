package io.github.brenoepics.core.modes;

import static io.github.brenoepics.utils.Functions.bubbleAlert;
import static io.github.brenoepics.utils.Functions.sanitize;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import gnu.trove.map.hash.THashMap;
import io.github.brenoepics.core.Variables;
import io.github.brenoepics.core.types.IMention;
import io.github.brenoepics.core.types.timeout.MentionTimeout;

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
  public MentionTimeout getTimer() {
    return timer;
  }

  @Override
  public boolean execute(Habbo sender, String receiver, String message) {
    if (!receiver.equalsIgnoreCase("everyone")) return false;
    if (Variables.SANITIZE) message = sanitize(message);

    message = getReplacedMessage(sender, message);
    THashMap<String, String> alert =
        bubbleAlert(sender, message, getReplacedLook(sender), isFollowEnabled());
    sendEveryone(alert);
    return true;
  }

  private static boolean isFollowEnabled() {
    return Emulator.getConfig().getBoolean("commands.cmd_mention_everyone.follow.enabled", true);
  }

  private static String getReplacedLook(Habbo sender) {
    return Emulator.getConfig()
        .getValue("commands.cmd_mention_everyone.look")
        .replace("%look%", sender.getHabboInfo().getLook());
  }

  private static String getReplacedMessage(Habbo sender, String message) {
    return Emulator.getTexts()
        .getValue("commands.cmd_mention_everyone.message")
        .replace("%MESSAGE%", message)
        .replace("%SENDER%", sender.getHabboInfo().getUsername());
  }

  private static void sendEveryone(THashMap<String, String> alert) {
    for (Habbo habbo : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().values()) {
      if (habbo == null || habbo.getHabboStats().blockStaffAlerts) continue;

      switch (Variables.EVERYONE_MODE) {
        case 1:
          habbo.getClient().sendResponse(new BubbleAlertComposer("mention", alert));
          break;
        case 2:
          habbo.whisper(alert.get("message"), RoomChatMessageBubbles.ALERT);
          break;
      }
    }
  }
}
