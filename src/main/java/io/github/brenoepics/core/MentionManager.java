package io.github.brenoepics.core;

import static io.github.brenoepics.utils.Functions.buildPattern;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import io.github.brenoepics.MentionExtractor;
import io.github.brenoepics.core.modes.*;
import io.github.brenoepics.core.types.HandleResult;
import io.github.brenoepics.core.types.IMention;
import io.github.brenoepics.core.types.exception.ExceptionHandler;
import io.github.brenoepics.core.types.exception.MentionException;
import io.github.brenoepics.logging.Message;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * MentionManager class is responsible for handling all the mentions in the MentionPlugin. It holds
 * all the mention modes in a LinkedHashMap where the key is the mention mode name and the value is
 * an object of the IMention type. The class also holds a list of all the messages that are sent
 * using the mention plugin.
 */
@Slf4j
public class MentionManager {
  private final LinkedHashMap<String, IMention> mentionModes;
  private final MentionExtractor extract;
  @Getter private final List<Message> messages;

  /**
   * Constructor for MentionManager.
   *
   * @param messages List of all messages sent using the mention plugin.
   */
  public MentionManager(List<Message> messages) {
    this.messages = messages;
    this.mentionModes = new LinkedHashMap<>();
    this.extract = buildPattern();

    this.mentionModes.put("everyone", new MentionEveryone("acc_mention_everyone", "everyone"));
    this.mentionModes.put("here", new MentionHere("acc_mention_here", "here"));
    this.mentionModes.put("room", new MentionRoom("acc_mention_room", "room"));
    this.mentionModes.put("friends", new MentionFriends("acc_mention_friends", "friends"));
    this.mentionModes.put("user", new MentionUser("acc_mention", "user"));
    log.info("[MentionPlugin] successfully loaded with {} modes!", this.mentionModes.size());
  }

  /**
   * Method to handle the mentions.
   *
   * @param sender The sender of the message.
   * @param message The message sent.
   * @return Returns a boolean indicating whether the message should be deleted or not.
   */
  public boolean handle(Habbo sender, String message) {
    Collection<IMention> iMentions = this.mentionModes.values();
    Set<String> mentioned = extract.fromString(message);
    if (mentioned.isEmpty()) return false;

    ArrayList<IMention> mentions = getCollected(sender, iMentions);
    for (IMention mode : mentions) {
      HandleResult res = handleMentionList(sender, mentioned, message, mode);
      if (!res.getError().isEmpty()) {
        sender.whisper(res.getError());
      }
      if (res.isHandle()) {
        return res.isDelete();
      }
    }
    return false;
  }

  private static ArrayList<IMention> getCollected(Habbo sender, Collection<IMention> iMentions) {
    return iMentions.stream()
        .filter(mode -> sender.hasPermission(mode.getPermission(), mode.allowRoomOwner()))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private HandleResult handleMentionList(
      Habbo sender, Set<String> mentioned, String message, IMention mode) {
    HandleResult result = new HandleResult(false, false);
    for (String mention : mentioned) {
      tryHandle(sender, message, mode, mention, result);
    }

    return result;
  }

  private void tryHandle(
      Habbo sender, String message, IMention mode, String mention, HandleResult result) {
    try {
      HandleResult res = executeMention(sender, message, mode, mention);
      if (res.isHandle()) {
        result.setHandle(true);
        if (res.isDelete()) {
          result.setDelete(true);
        }
      }
    } catch (MentionException e) {
      if (Emulator.getConfig().getBoolean("commands.cmd_mention.message_error.delete")) {
        result.setDelete(true);
      }
      result.setError(ExceptionHandler.handle(e));
    }
  }

  private HandleResult executeMention(Habbo sender, String message, IMention mode, String receiver)
      throws MentionException {
    boolean delete = false;
    boolean handle = false;
    if (mode.execute(sender, receiver, message)) {
      delete = handleSuccess(sender, mode, receiver, message);
      handle = true;
    }

    return new HandleResult(delete, handle);
  }
  

  private boolean handleSuccess(Habbo sender, IMention mode, String receiver, String message) {
    int timeout = Emulator.getConfig().getInt("mentionplugin.timeout_" + mode.getPrefix(), 20);
    mode.getTimer().ofUser(sender, timeout);
    this.addMessage(new Message(sender.getHabboInfo().getId(), receiver, message));

    sender.whisper(Emulator.getTexts().getValue("commands.cmd_mention.message.sent"));

    return Emulator.getConfig().getBoolean("commands.cmd_mention.message_success.delete");
  }

  public void disposeMessages() {
    this.messages.clear();
  }

  public void addMessage(Message message) {
    messages.add(message);
  }
}
