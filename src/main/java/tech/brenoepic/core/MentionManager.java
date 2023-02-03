package tech.brenoepic.core;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import tech.brenoepic.MentionPlugin;
import tech.brenoepic.core.modes.*;
import tech.brenoepic.core.types.IMention;
import tech.brenoepic.core.types.exception.ExceptionHandler;
import tech.brenoepic.core.types.exception.MentionException;
import tech.brenoepic.logging.Message;
import tech.brenoepic.utils.Functions;

import java.util.*;
/**
 * MentionManager class is responsible for handling all the mentions in the MentionPlugin.
 * It holds all the mention modes in a LinkedHashMap where the key is the mention mode name and
 * the value is an object of the IMention type.
 * The class also holds a list of all the messages that are sent using the mention plugin.
 */
public class MentionManager {
    private final LinkedHashMap<String, IMention> mentionModes;
    private final List<Message> messages;
    /**
     * Constructor for MentionManager.
     * @param messages List of all messages sent using the mention plugin.
     */
    public MentionManager(List<Message> messages) {
        this.messages = messages;
        this.mentionModes = new LinkedHashMap<>();


        this.mentionModes.put("everyone", new MentionEveryone("acc_mention_everyone", "everyone"));
        this.mentionModes.put("here", new MentionHere("acc_mention_here", "here"));
        this.mentionModes.put("room", new MentionRoom("acc_mention_room", "room"));
        this.mentionModes.put("friends", new MentionFriends("acc_mention_friends", "friends"));
        this.mentionModes.put("user", new MentionUser("acc_mention", "user"));
        MentionPlugin.LOGGER.info("[MENTION-PLUGIN] successfully loaded with {} modes!", this.mentionModes.size());
    }
    /**
     * Method to handle the mentions.
     * @param sender The sender of the message.
     * @param message The message sent.
     * @return Returns a boolean indicating whether the message should be deleted or not.
     */
    public boolean handle(Habbo sender, String message) {
        boolean delete = false;
        Collection<IMention> Modes = this.mentionModes.values();
        int total = 0;
        String error = "";
        Set<String> mentioned = Functions.getUserMentionedFromChat(message);
        for (IMention mode : Modes) {
            if (mentioned.isEmpty() || mentioned.size() > mode.mentionsPerMessage() || (total > 0 && mode.getPrefix().equals("user")) || !sender.hasPermission(mode.getPermission(), mode.allowRoomOwner()))
                continue;
            int totalInMode = 0;
            for (String receiver : mentioned) {
                if (total >= mode.mentionsPerMessage()) break;
                try {
                    if (mode.execute(sender, receiver, message)) total++;
                } catch (MentionException e) {
                    if (Emulator.getConfig().getBoolean("commands.cmd_mention.message_error.delete")) delete = true;
                    error = ExceptionHandler.handle(e);
                    continue;
                }
                if (Emulator.getConfig().getBoolean("commands.cmd_mention.message_success.delete")) delete = true;
                totalInMode++;
                if (totalInMode == 1)
                    mode.getTimer().NewTimer(sender.getHabboInfo().getId(), Emulator.getConfig().getInt("mentionplugin.timeout_" + mode.getPrefix(), 20));
            }

            MentionPlugin.getManager().addMessage(new Message(sender.getHabboInfo().getId(), String.join(" ", mentioned), message));
        }
    
sender.whisper(total > 0 ? Emulator.getTexts().getValue(total == 1 ? "commands.cmd_mention.message.sent" : "commands.cmd_mention.message.multi.sent").replace("%var%", String.valueOf(total)) : error);

        return delete;
    }

    public List<Message> getMessages() {
        return this.messages;
    }

    public void disposeMessages() {
        this.messages.clear();
    }

    public void addMessage(Message message) {
        messages.add(message);
    }
}
