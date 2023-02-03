package tech.brenoepic.core;

import com.eu.habbo.Emulator;

public class Variables {
    public static String FRIENDS_PREFIX = Emulator.getConfig().getValue("commands.cmd_mention_friends.prefix", "friends");
    public static int MENTION_MODE = Emulator.getConfig().getInt("mentionplugin.mode_user", 1);
    public static int EVERYONE_MODE = Emulator.getConfig().getInt("mentionplugin.mode_everyone", 1);
    public static int FRIENDS_MODE = Emulator.getConfig().getInt("mentionplugin.mode_friends", 1);
    public static int ROOM_MODE = Emulator.getConfig().getInt("mentionplugin.mode_room", 1);
    public static boolean SANITIZE = Emulator.getConfig().getBoolean("mentionplugin.sanitize", true);
}
