package tech.brenoepic.events;

import tech.brenoepic.logging.DatabaseLogger;
import com.eu.habbo.Emulator;
import tech.brenoepic.MentionPlugin;
import tech.brenoepic.utils.Extras;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import com.eu.habbo.plugin.events.emulator.EmulatorStartShutdownEvent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class EmulatorEvents implements EventListener {
    @EventHandler
    public static void onEmulatorLoaded(EmulatorLoadedEvent event) {
        Extras.checkDatabase();
        Extras.loadTexts();
        Emulator.getPluginManager().registerEvents(MentionPlugin.INSTANCE, new UserEvents());
        Timer timer = new Timer();
        long minutes = TimeUnit.MINUTES.toMillis(Emulator.getConfig().getInt("mentionplugin.database.log_timeout_minutes", 30));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!MentionPlugin.getManager().getMessages().isEmpty())
                    DatabaseLogger.save(MentionPlugin.getManager().getMessages());
            }
        }, 0, minutes);
    }

    @EventHandler
    public static void onEmulatorStopped(EmulatorStartShutdownEvent event) {
        MentionPlugin.LOGGER.info("[MENTION-PLUGIN] Stopping...");
        if (!MentionPlugin.getManager().getMessages().isEmpty())
            DatabaseLogger.save(MentionPlugin.getManager().getMessages());
    }
}
