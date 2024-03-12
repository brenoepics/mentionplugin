package io.github.brenoepics.logging;

import com.eu.habbo.Emulator;
import io.github.brenoepics.MentionPlugin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.TimerTask;

import lombok.extern.slf4j.Slf4j;

/** DatabaseLogger class is used to save the mention messages in a database. */
@Slf4j
public class DatabaseLogger {

  /**
   * This method saves the given list of mention messages to a database.
   *
   * @param messages List of mention messages to be saved.
   */
  public static void save(List<Message> messages) {
    // Check if database logging is enabled in the configuration
    if (Emulator.getConfig().getBoolean("mentionplugin.logging_database", true)) {
      try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
          PreparedStatement statement =
              connection.prepareStatement(
                  "INSERT INTO mention_logs (user_from_id, user_to, message, timestamp) VALUES (?, ?, ?, ?)")) {

        // Loop through all messages in the list and execute the insertion statement
        for (Message n : messages) {
          statement.setInt(1, n.getFromId());
          statement.setString(2, n.getToUser());
          statement.setString(3, n.getContent());
          statement.setInt(4, n.getTimestamp());
          statement.execute();
        }
      } catch (SQLException e) {
        // Log the caught SQL exception
        log.error("Caught SQL exception", e);
      } finally {
        // TODO: Check if Discord logging is enabled in the configuration
        //if (Emulator.getConfig().getBoolean("mentionplugin.logging_discord", true)) {
        //  // Create a WebhookClient with the given URL
        //  WebhookClient client =
        //      WebhookClient.withUrl(
        //          Emulator.getConfig().getValue("mentionplugin.logging.discord-webhook.url"));
        //  WebhookMessageBuilder message =
        //      new WebhookMessageBuilder().setAllowedMentions(AllowedMentions.none());
//
        //  // Create the message to be sent to Discord
        //  StringBuilder msg = new StringBuilder().append("**[IMention-Plugin]**\n");
        //  for (Message n : messages) {
        //    msg.append("`FROM: ")
        //        .append(n.getFromId())
        //        .append(" TO: ")
        //        .append(n.getToUser())
        //        .append(" MESSAGE: ")
        //        .append(n.getMessage())
        //        .append(" TIME: ")
        //        .append(n.getTimestamp())
        //        .append("`\n");
        //  }
        //  message.append(msg.toString());
//
        //  // Send the message to Discord
        //  client.send(message.build());
        //}
        // Dispose the messages and log a message indicating that mentions have been saved
        // successfully
        MentionPlugin.getManager().disposeMessages();
        log.debug("[MentionPlugin] Mentions saved successfully!");
      }
    }
  }

  public static TimerTask saveTask() {
    return new TimerTask() {
      @Override
      public void run() {
        if (!MentionPlugin.getManager().getMessages().isEmpty()) {
          DatabaseLogger.save(MentionPlugin.getManager().getMessages());
        }
      }
    };
  }
}
