package io.github.brenoepics.core.modes;

import static io.github.brenoepics.utils.Functions.bubbleAlert;
import static io.github.brenoepics.utils.Functions.sanitize;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
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
  public MentionTimeout getTimer() {
    return timer;
  }

  @Override
  public boolean execute(Habbo sender, String receiver, String message) throws MentionException {
    if (!Variables.FRIENDS_PREFIX.equalsIgnoreCase(receiver)) return false;
    Timeout canMention = timer.canMention(sender.getHabboInfo().getId());

    if (canMention != null) {
      throw new MentionException("CANT_MENTION", cantMentionCause(canMention));
    }

    if (Variables.SANITIZE) message = sanitize(message);

    message = getReplacedMessage(sender, message);
    THashMap<String, String> alert =
        bubbleAlert(sender, message, getReplacedLook(sender), isFollowEnabled());
    sendMention(sender, alert);
    return true;
  }

  private static void sendMention(Habbo sender, THashMap<String, String> alert) {
    for (MessengerBuddy player : sender.getMessenger().getFriends().values()) {
      Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(player.getId());
      if (player.getOnline() == 0 || habbo == null || habbo.getHabboStats().blockRoomInvites)
        continue;

      switch (Variables.FRIENDS_MODE) {
        case 1:
          habbo.getClient().sendResponse(new BubbleAlertComposer("mention", alert));
          break;
        case 2:
          habbo.whisper(alert.get("message"), RoomChatMessageBubbles.ALERT);
          break;
      }
    }
  }

  private static boolean isFollowEnabled() {
    return Emulator.getConfig().getBoolean("commands.cmd_mention.friends.follow.enabled", true);
  }

  private static String getReplacedLook(Habbo sender) {
    return Emulator.getConfig()
        .getValue("commands.cmd_mention.look")
        .replace("%look%", sender.getHabboInfo().getLook());
  }

  private static String getReplacedMessage(Habbo sender, String message) {
    return Emulator.getTexts()
        .getValue("commands.cmd_mention_friends.message")
        .replace("%MESSAGE%", message)
        .replace("%SENDER%", sender.getHabboInfo().getUsername());
  }

  private static MentionExceptionCause cantMentionCause(Timeout canMention) {
    return new MentionExceptionCause(
        String.valueOf(
            canMention.getFinish().minusMillis(System.currentTimeMillis()).getEpochSecond()));
  }
}
