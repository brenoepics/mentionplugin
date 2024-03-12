package io.github.brenoepics;

import io.github.brenoepics.core.MentionManager;
import io.github.brenoepics.events.EmulatorEvents;
import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.HabboPlugin;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;


/**
 * The main class for the MentionPlugin.
 * Implements the EventListener interface and extends HabboPlugin.
 */
@Slf4j
public class MentionPlugin extends HabboPlugin implements EventListener {
    @Getter
    private static MentionPlugin instance = null;
    @Getter
    private static MentionManager manager;

    /**
     * Called when the plugin is enabled.
     * Registers the EmulatorEvents as an event listener and creates a new MentionManager.
     */
    @Override
    public void onEnable() {
        instance = this;
        Emulator.getPluginManager().registerEvents(this, new EmulatorEvents());
        manager = new MentionManager(new ArrayList<>());
    }

  /** Called when the plugin is disabled. */
  public void onDisable() {
    // TODO document why this method is empty
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
