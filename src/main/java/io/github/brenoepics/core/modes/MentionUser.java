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
import io.github.brenoepics.core.types.exception.MentionException;
import io.github.brenoepics.core.types.exception.MentionExceptionCause;
import io.github.brenoepics.core.types.timeout.MentionTimeout;
import io.github.brenoepics.core.types.timeout.Timeout;
import lombok.extern.slf4j.Slf4j;

/**
 * MentionUser is a class that implements the {@link IMention} interface.
 * It is used to handle the
 * functionality of mentioning a specific user in the hotel.
 *
 */
public class MentionUser implements IMention {
  private final MentionTimeout timer;
  private final String prefix;
  private final String permission;

  /**
   * Creates a new instance of MentionUser.
   *
   * @param permission The permission required to execute this mention.
   * @param prefix The prefix used to trigger this mention.
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
   * @param sender The sender of the mention.
   * @param receiver The receiver of the mention.
   * @param message The message of the mention.
   * @return True if the mention was successful, false otherwise.
   * @throws MentionException If an error occurs while executing the mention.
   */
  @Override
  public boolean execute(Habbo sender, String receiver, String message) throws MentionException {
    Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(receiver);
    Timeout canMention = timer.canMention(sender.getHabboInfo().getId());
    isBroken(sender, canMention, habbo);

    if (Variables.SANITIZE) {
      message = sanitize(message);
    }

    message = getReplacedMessage(sender, message);
    THashMap<String, String> alert = bubbleAlert(sender, message, getLook(sender), canFollow());
    sendMention(habbo, alert);

    return true;
  }

  private static void sendMention(Habbo habbo, THashMap<String, String> alert) {
    switch (Variables.MENTION_MODE) {
      case 1:
        sendMentionBubble(habbo, alert);
        break;
      case 2:
        sendMentionWhisper(habbo, alert);
        break;
      case 3:
        sendMentionBubble(habbo, alert);
        sendMentionWhisper(habbo, alert);
        break;
    }
  }

  private static void sendMentionWhisper(Habbo habbo, THashMap<String, String> alert) {
    habbo.whisper(alert.get("message"), RoomChatMessageBubbles.ALERT);
  }

  private static void sendMentionBubble(Habbo habbo, THashMap<String, String> alert) {
    habbo.getClient().sendResponse(new BubbleAlertComposer("mention", alert));
  }

  private static boolean canFollow() {
    return Emulator.getConfig().getBoolean("commands.cmd_mention_everyone.follow.enabled", true);
  }

  private static String getLook(Habbo sender) {
    return Emulator.getConfig()
        .getValue("commands.cmd_mention_everyone.look")
        .replace("%look%", sender.getHabboInfo().getLook());
  }

  private static String getReplacedMessage(Habbo sender, String message) {
    return Emulator.getTexts()
        .getValue("commands.cmd_mention.message")
        .replace("%MESSAGE%", message)
        .replace("%SENDER%", sender.getHabboInfo().getUsername());
  }

  private static void isBroken(Habbo sender, Timeout canMention, Habbo habbo)
      throws MentionException {
    if (canMention != null) {
      throw new MentionException("CANT_MENTION", getExceptionCause(canMention));
    }

    if (habbo == null) {
      throw new MentionException("USER_NOT_FOUND");
    }

    if (habbo == sender) {
      throw new MentionException("SELF_MENTION");
    }

    if ((boolean) habbo.getHabboStats().cache.get("blockmention")
        || habbo.getHabboStats().userIgnored(sender.getHabboInfo().getId())) {
      throw new MentionException(
          "USER_MENTION_BLOCKED", new MentionExceptionCause(habbo.getHabboInfo().getUsername()));
    }
  }

  private static MentionExceptionCause getExceptionCause(Timeout canMention) {
    return new MentionExceptionCause(
        String.valueOf(
            canMention.getFinish().minusMillis(System.currentTimeMillis()).getEpochSecond()));
  }
}
