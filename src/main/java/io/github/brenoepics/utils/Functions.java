package io.github.brenoepics.utils;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.map.hash.THashMap;
import io.github.brenoepics.MentionExtractor;
import io.github.brenoepics.MentionPattern;
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
 */
public class Functions {

    /**
     * Sanitizes a string by removing any potentially harmful HTML elements.
     *
     * @param str The string to be sanitized.
     * @return The sanitized string.
     */
    public static String sanitize(String str) {
        PolicyFactory policy = new HtmlPolicyBuilder().toFactory();
        return policy.sanitize(str);
    }

    /**
     * Builds a pattern for extracting mentions from chat messages.
     *
     * @return A mention extractor with a pattern for extracting mentions from chat messages.
     */
    public static MentionExtractor buildPattern() {
        int maxMentions = Emulator.getConfig().getInt("mentionplugin.max_mentions", 5);
        MentionPattern mentionPattern;
        if (Emulator.getConfig().getBoolean("commands.cmd_mention_custom", false)) {
            String pattern = Emulator.getConfig().getValue("commands.cmd_mention_regex", "@(\\w+)");
            mentionPattern = new MentionPattern().customPattern(pattern);
        } else {
            mentionPattern = new MentionPattern().withSpecialChars();
        }

        return new MentionExtractor.Builder().pattern(mentionPattern).maxMentions(maxMentions).build();
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
    public static THashMap<String, String> bubbleAlert(Habbo sender, String message, String image, boolean followEnabled) {
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