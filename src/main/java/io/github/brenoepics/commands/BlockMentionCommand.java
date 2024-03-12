package io.github.brenoepics.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Class representing the `BlockMentionCommand` command. It is used to block or unblock mentions for
 * a user.
 *
 * @author BrenoEpic#9671
 */
public class BlockMentionCommand extends Command {

  /**
   * Constructor for the `BlockMentionCommand` command. It takes in the permission and command keys
   * needed to execute the command.
   *
   * @param permission the permission required to execute the command
   * @param keys the command keys used to execute the command
   */
  public BlockMentionCommand(final String permission, final String[] keys) {
    super(permission, keys);
  }

  /**
   * Handles the execution of the `BlockMentionCommand` command.
   *
   * @param gameClient the game client that sent the command
   * @param strings any additional parameters passed with the command
   * @return a boolean indicating if the command was successfully handled
   */
  public boolean handle(final GameClient gameClient, final String[] strings) {
    Habbo habbo = gameClient.getHabbo();
    final boolean blockMention = (boolean) habbo.getHabboStats().cache.get("blockmention");
    habbo.whisper(getMessage(blockMention));
    habbo.getHabboStats().cache.put("blockmention", !blockMention);

    return true;
  }

  private static String getMessage(boolean blockMention) {
    return Emulator.getTexts()
        .getValue(
            !blockMention ? "commands.cmd_mention.success.on" : "commands.cmd_mention.success.off");
  }
}
