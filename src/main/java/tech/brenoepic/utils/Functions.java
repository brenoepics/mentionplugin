package tech.brenoepic.utils;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.map.hash.THashMap;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The `Functions` class is a utility class that provides functions for sanitizing strings and for processing mentions
 * in chat messages.
 *
 * @author BrenoEpic#9671
 */
public class Functions {

    /**
     * Sanitizes a string by removing any potentially harmful HTML elements.
     *
     * @param str The string to be sanitized.
     * @return The sanitized string.
     */
    public static String Sanitize(String str) {
        PolicyFactory policy = new HtmlPolicyBuilder().toFactory();
        return policy.sanitize(str);
    }

    /**
     * Extracts mentions of users in a given chat message.
     *
     * @param chat The chat message to be processed.
     * @return A set of the usernames that have been mentioned in the chat message.
     */
    public static Set<String> getUserMentionedFromChat(String chat) {
        Set<String> Mentioned = new HashSet<>();
        Pattern compiledPattern = Pattern.compile(Emulator.getConfig().getValue("commands.cmd_mention_regex", "@(\\w+)"));
        Matcher matcher = compiledPattern.matcher(chat);
        while (matcher.find()) {
            if (Mentioned.size() < Emulator.getConfig().getInt("commands.cmd_mention_max", 5))
                Mentioned.add(matcher.group(1));
        }
        return Mentioned;
    }

    /**
     * Creates a bubble alert to be displayed to a set of users.
     *
     * @param sender The sender of the alert.
     * @param message The message to be displayed in the alert.
     * @param image The image to be displayed in the alert.
     * @param followEnabled Determines whether the alert should be linked to the sender's current room.
     * @return A map representing the bubble alert, with the display type, image, message, and link URL.
     */
    public static THashMap<String, String> BubbleAlert(Habbo sender, String message, String image, boolean followEnabled) {
        THashMap<String, String> notification = new THashMap<>();
        notification.put("display", "BUBBLE");
        notification.put("image", image);
        notification.put("message", message);
        final Room room = sender.getHabboInfo().getCurrentRoom();
        if (room != null && followEnabled) {
            notification.put("linkUrl", "event:navigator/goto/" + sender.getHabboInfo().getCurrentRoom().getId());
        }
        return notification;
    }
}