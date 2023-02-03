package tech.brenoepic;

import tech.brenoepic.core.MentionManager;
import tech.brenoepic.events.EmulatorEvents;
import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.HabboPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.brenoepic.core.types.timeout.MentionTimeout;

import java.util.ArrayList;


/**
 * The main class for the MentionPlugin.
 * Implements the EventListener interface and extends HabboPlugin.
 */
public class MentionPlugin extends HabboPlugin implements EventListener {
    public static final Logger LOGGER = LoggerFactory.getLogger(MentionPlugin.class);
    public static MentionPlugin INSTANCE = null;
    private static MentionManager manager;

    /**
     * Called when the plugin is enabled.
     * Registers the EmulatorEvents as an event listener and creates a new MentionManager.
     */
    @Override
    public void onEnable() {
        INSTANCE = this;
        Emulator.getPluginManager().registerEvents(this, new EmulatorEvents());
        manager = new MentionManager(new ArrayList<>());
    }

    /**
     * Called when the plugin is disabled.
     */
    public void onDisable() {
    }

    /**
     * Returns the MentionManager.
     * @return MentionManager manager.
     */
    public static MentionManager getManager() {
        return manager;
    }

    /**
     * Returns a boolean indicating if the given Habbo has the given permission.
     * Currently always returns false.
     * @param habbo The Habbo to check the permission for.
     * @param s The permission to check.
     * @return boolean false.
     */
    public boolean hasPermission(Habbo habbo, String s) {
        return false;
    }

    /**
     * Main method, not intended to be run separately.
     * Prints a message to the console.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        System.out.println("Don't run this separately!");
    }
}
