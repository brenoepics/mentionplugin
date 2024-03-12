package io.github.brenoepics.utils;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.CommandHandler;
import io.github.brenoepics.commands.BlockMentionCommand;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Extras {
  public static void loadTexts() {
    Emulator.getTexts()
        .register("commands.error.cmd_mention.not_self", "You cannot mention yourself");
    Emulator.getTexts().register("commands.cmd_mention.message", "%SENDER% said: %MESSAGE%");
    Emulator.getTexts().register("commands.cmd_mention.message.sent", "Mention successfully sent!");
    Emulator.getTexts()
        .register(
            "commands.error.cmd_mention.user_not_found",
            "Sorry, I could not find the mentioned user.");
    Emulator.getTexts()
        .register(
            "commands.error.cmd_mention.users_not_found",
            "Sorry, I could not find one or more of the mentioned users.");
    Emulator.getTexts()
        .register("commands.cmd_mention_everyone.message", "%SENDER% said: %MESSAGE%");
    Emulator.getTexts()
        .register("commands.cmd_mention_friends.message", "%SENDER% said: %MESSAGE%");
    Emulator.getTexts().register("commands.cmd_mention_room.message", "%SENDER% said: %MESSAGE%");
    Emulator.getTexts()
        .register("commands.cmd_mention_user_success", "You have successfully mentioned %var%!");
    Emulator.getTexts().register("commands.cmd_mention.allfriends", "all friends");
    Emulator.getTexts().register("commands.cmd_mention.allfriends", "whole room");
    Emulator.getConfig()
        .register("commands.cmd_mention.look", "${image.library.url}notifications/fig/%LOOK%.png");
    Emulator.getConfig()
        .register(
            "commands.cmd_mention_everyone.look",
            "${image.library.url}notifications/fig/%LOOK%.png");
    Emulator.getConfig()
        .register(
            "commands.cmd_mention_room.look", "${image.library.url}notifications/fig/%LOOK%.png");
    Emulator.getTexts()
        .register(
            "commands.error.cmd_mention.user_blocksmention",
            "The MentionUser does not want to be mentioned.");
    Emulator.getTexts()
        .register(
            "commands.error.cmd_mention.user_mention_blocked",
            "%var% do not want to be mentioned.");
    Emulator.getTexts()
        .register("commands.error.cmd_mention.unknown_error", "An unknown error occurred.");
    Emulator.getTexts().register("cmd_blockmention_keys", "blockmention;mentionsoff");
    Emulator.getTexts()
        .register(
            "commands.cmd_mention.message.multi.sent",
            "You have successfully mentioned %var% users!");
    Emulator.getTexts().register("commands.cmd_mention.success.off", "Mentions disabled!");
    Emulator.getTexts().register("commands.cmd_mention.success.on", "Mentions enabled!");
    Emulator.getTexts().register("commands.cmd_mention.room", "whole room");
    Emulator.getTexts().register("commands.description.cmd_blockmention", ":blockmention");
    Emulator.getTexts()
        .register(
            "commands.cmd_mention.timeout_message",
            "You should wait more %var% seconds to mention again!");

    Emulator.getConfig().register("commands.cmd_mention_friends.prefix", "friends");
    Emulator.getConfig().register("commands.cmd_mention.message_error.delete", "1");
    Emulator.getConfig().register("commands.cmd_mention.message_success.delete", "0");
    Emulator.getConfig().register("commands.cmd_mention.follow.enabled", "1");
    Emulator.getConfig().register("commands.cmd_mention.room.follow.enabled", "1");
    Emulator.getConfig().register("commands.cmd_mention.friends.follow.enabled", "1");
    Emulator.getConfig().register("commands.cmd_mention.message.show_username.enabled", "1");
    Emulator.getConfig()
        .register("commands.cmd_mention_friends.message.show_username.enabled", "1");
    Emulator.getConfig().register("commands.cmd_mention_room.message.show_username.enabled", "1");
    Emulator.getConfig().register("commands.cmd_mention_everyone.follow.enabled", "1");
    Emulator.getConfig().register("commands.cmd_mention_regex", "@(\\w+)");
    Emulator.getConfig().register("commands.cmd_mention_max", "5");
    Emulator.getConfig().register("mentionplugin.sanitize", "1");
    Emulator.getConfig().register("mentionplugin.mode_user", "1");
    Emulator.getConfig().register("mentionplugin.mode_everyone", "1");
    Emulator.getConfig().register("mentionplugin.mode_friends", "1");
    Emulator.getConfig().register("mentionplugin.mode_room", "1");
    Emulator.getConfig().register("mentionplugin.timeout_user", "10");
    Emulator.getConfig().register("mentionplugin.timeout_everyone", "5");
    Emulator.getConfig().register("mentionplugin.timeout_friends", "60");
    Emulator.getConfig().register("mentionplugin.timeout_room", "20");
    Emulator.getConfig().register("mentionplugin.logging_database", "1");
    Emulator.getConfig().register("mentionplugin.database.log_timeout_minutes", "30");
    Emulator.getConfig().register("mentionplugin.logging_discord", "0");
    Emulator.getConfig()
        .register(
            "mentionplugin.logging.discord-webhook.url",
            "https://discordapp.com/api/webhooks/<YOURTOKEN>");
    CommandHandler.addCommand(
        new BlockMentionCommand(
            "cmd_blockmention", Emulator.getTexts().getValue("cmd_blockmention_keys").split(";")));
  }

  private static void registerSettingField(
      final String field, final String type, final String defaultValue) {
    try (final Connection connection = Emulator.getDatabase().getDataSource().getConnection();
        final PreparedStatement statement =
            connection.prepareStatement(
                "ALTER TABLE `users_settings` ADD `"
                    + field
                    + "` "
                    + type
                    + " NOT NULL DEFAULT '"
                    + defaultValue
                    + "'")) {
      statement.execute();
    } catch (SQLException ignored) {
    }
  }

  private static boolean registerPermission(
      String name, String defaultValue, boolean defaultReturn) {
    try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
      try (PreparedStatement statement =
          connection.prepareStatement(
              "ALTER TABLE  `permissions` ADD  `"
                  + name
                  + "` ENUM('0', '1') NOT NULL DEFAULT "
                  + defaultValue)) {
        statement.execute();
        return true;
      }
    } catch (SQLException ignored) {
    }

    return defaultReturn;
  }

  public static void checkDatabase() {
    registerSettingField("blockmentions", "ENUM ('0', '1')", "0");
    boolean reloadPermissions = registerPermission("acc_mention", "1", false);
    reloadPermissions = registerPermission("acc_mention_friends", "1", reloadPermissions);
    reloadPermissions = registerPermission("acc_mention_everyone", "0", reloadPermissions);
    reloadPermissions = registerPermission("acc_mention_room", "2", reloadPermissions);
    reloadPermissions = registerPermission("acc_mention_here", "0", reloadPermissions);
    reloadPermissions = registerPermission("cmd_blockmention", "1", reloadPermissions);
    if (reloadPermissions) Emulator.getGameEnvironment().getPermissionsManager().reload();
  }
}
