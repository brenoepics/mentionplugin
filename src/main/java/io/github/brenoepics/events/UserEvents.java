package io.github.brenoepics.events;

import io.github.brenoepics.MentionPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.eu.habbo.Emulator;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.events.users.UserDisconnectEvent;
import com.eu.habbo.plugin.events.users.UserLoginEvent;
import com.eu.habbo.plugin.events.users.UserTalkEvent;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class UserEvents implements EventListener {
    UserEvents() {
        // Prevent instantiation.
    }

    @EventHandler
    public static void onUserTalkEvent(UserTalkEvent event) {
       event.setCancelled(MentionPlugin.getManager().handle(event.habbo, event.chatMessage.getMessage()));
    }

    @EventHandler
    public static void onUserLoginEvent(UserLoginEvent event) {
        if (event.habbo != null) {
            try (final Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 final PreparedStatement statement = connection.prepareStatement("SELECT `blockmentions` FROM `users_settings` WHERE `user_id` = ? LIMIT 1")) {
                statement.setInt(1, event.habbo.getHabboInfo().getId());
                try (final ResultSet set = statement.executeQuery()) {
                    if (set.next()) {
                        event.habbo.getHabboStats().cache.put("blockmention", set.getString("blockmentions").equals("1"));
                    }
                }
            } catch (SQLException e) {
                log.error("[MentionPlugin]", e);
            }
        }

    }

    @EventHandler
    public static void onUserDisconnectEvent(UserDisconnectEvent event) {
        final boolean blockMention = (boolean) event.habbo.getHabboStats().cache.get("blockmention");
        try (final Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             final PreparedStatement statement = connection.prepareStatement("UPDATE `users_settings` SET `blockmentions` = ? WHERE `user_id` = ? LIMIT 1")) {
            statement.setString(1, blockMention ? "1" : "0");
            statement.setInt(2, event.habbo.getHabboInfo().getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error("[MentionPlugin]", e);
        }
    }

}