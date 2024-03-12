package io.github.brenoepics.events;

import com.eu.habbo.Emulator;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import com.eu.habbo.plugin.events.emulator.EmulatorStartShutdownEvent;
import io.github.brenoepics.MentionPlugin;
import io.github.brenoepics.logging.DatabaseLogger;
import io.github.brenoepics.utils.Extras;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmulatorEvents implements EventListener {
  public EmulatorEvents() {
    // Prevent instantiation.
  }

  @EventHandler
  public static void onEmulatorLoaded(EmulatorLoadedEvent event) {
    Extras.checkDatabase();
    Extras.loadTexts();
    Emulator.getPluginManager().registerEvents(MentionPlugin.getInstance(), new UserEvents());
    long minutes =
        TimeUnit.MINUTES.toMillis(
            Emulator.getConfig().getInt("mentionplugin.database.log_timeout_minutes", 30));
    Timer timer = new Timer();
    timer.schedule(DatabaseLogger.saveTask(), 0, minutes);
  }

  @EventHandler
  public static void onEmulatorStopped(EmulatorStartShutdownEvent event) {
    log.info("[MentionPlugin] Stopping...");
    if (!MentionPlugin.getManager().getMessages().isEmpty()) {
      DatabaseLogger.save(MentionPlugin.getManager().getMessages());
    }
  }
}
