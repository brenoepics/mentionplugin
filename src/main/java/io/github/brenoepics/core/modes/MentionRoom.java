package io.github.brenoepics.core.modes;

import static io.github.brenoepics.utils.Functions.bubbleAlert;
import static io.github.brenoepics.utils.Functions.sanitize;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import gnu.trove.map.hash.THashMap;
import io.github.brenoepics.core.Variables;
import io.github.brenoepics.core.types.IMention;
import io.github.brenoepics.core.types.exception.MentionException;
import io.github.brenoepics.core.types.exception.MentionExceptionCause;
import io.github.brenoepics.core.types.timeout.MentionTimeout;
import io.github.brenoepics.core.types.timeout.Timeout;

public class MentionRoom implements IMention {
  private final MentionTimeout timer;
  private final String prefix;
  private final String permission;

  public MentionRoom(String permission, String prefix) {
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
  public boolean execute(Habbo sender, String receiver, String message) throws MentionException {
    if (!receiver.equalsIgnoreCase("room")) return false;

    Timeout canMention = timer.canMention(sender.getHabboInfo().getId());
    if (canMention != null) {
      throw new MentionException("CANT_MENTION", cantMentionCause(canMention));
    }

    if (Variables.SANITIZE) message = sanitize(message);

    message = getReplacedMessage(sender, message);
    THashMap<String, String> alert = bubbleAlert(sender, message, getLook(sender), canFollow());

    Room room = sender.getHabboInfo().getCurrentRoom();
    if (room == null) {
      throw new MentionException("ROOM_NOT_FOUND");
    }

    sendMention(sender, room, alert);
    return true;
  }

  private static void sendMention(Habbo sender, Room room, THashMap<String, String> alert) {
    for (Habbo habbo : room.getHabbos()) {
      if (habbo == null
          || (boolean) habbo.getHabboStats().cache.get("blockmention")
          || habbo.getHabboStats().userIgnored(sender.getHabboInfo().getId())) {
        continue;
      }

      switch (Variables.ROOM_MODE) {
        case 1:
          habbo.getClient().sendResponse(new BubbleAlertComposer("mention", alert));
          break;
        case 2:
          habbo.whisper(alert.get("message"), RoomChatMessageBubbles.ALERT);
          break;
      }
    }
  }

  private static boolean canFollow() {
    return Emulator.getConfig().getBoolean("commands.cmd_mention_room.follow.enabled", true);
  }

  private static String getLook(Habbo sender) {
    return Emulator.getConfig()
        .getValue("commands.cmd_mention_room.look")
        .replace("%look%", sender.getHabboInfo().getLook());
  }

  private static String getReplacedMessage(Habbo sender, String message) {
    return Emulator.getTexts()
        .getValue("commands.cmd_mention_room.message")
        .replace("%MESSAGE%", message)
        .replace("%SENDER%", sender.getHabboInfo().getUsername());
  }

  private static MentionExceptionCause cantMentionCause(Timeout canMention) {
    return new MentionExceptionCause(
        String.valueOf(
            canMention.getFinish().minusMillis(System.currentTimeMillis()).getEpochSecond()));
  }
}
