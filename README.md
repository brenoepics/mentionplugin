# Mention Plugin for Arcturus Morningstar 3.5.x

> [!WARNING]  
> Main branch is currently under development (4.0) and may not be stable.

A simple and easy-to-use plugin for mentioning users in a chat room.
You can mention users using their username, all friends with @friends,
everyone with @everyone, everyone awake with @here or the entire room with @room.
The @ symbol can be placed anywhere in the message.
The plugin also includes features such as blocking mentions, setting a mention timeout,
choosing between bubble alert or whisper, and logging with a database or Discord.

## How to use

To use the plugin,
simply type `@username`, `@friends`, `@everyone`,
or `@room`
followed by your message.

## How to install

- Download a pre-compiled version of the plugin [here](https://github.com/brenoepics/MentionPlugin/releases/).
- Run the SQL script provided.
- Move the `MentionPlugin-3.0-jar-with-dependencies.jar` file to your emulator's plugins folder and restart the
  emulator.
- Set up user permissions by modifying the `acc_mention`,
  `acc_mention_everyone`, `acc_mention_friends`, and `acc_mention_room`
  fields in your database's permissions table.
- Update the permissions by typing `:update_permissions` in the hotel or restarting the emulator.
- Start mentioning users by using the mentioned keywords.

## Configuration

The plugin includes the following configuration options:

### Emulator Settings

| Key                                                  | Default Value | Meaning                     |
|------------------------------------------------------|---------------|-----------------------------|
| `commands.cmd_mention_friends.prefix`                | `friends`     |                             |
| `commands.cmd_mention.message_error.delete`          | `true`        |                             |
| `commands.cmd_mention.message_success.delete`        | `false`       |                             |
| `commands.cmd_mention.follow.enabled`                | `true`        |                             |
| `commands.cmd_mention.message.show_username.enabled` | `true`        |                             |
| `commands.cmd_mention_everyone.follow.enabled`       | `true`        |                             |
| `commands.cmd_mention_regex`                         | `@(\\w+)`     |                             |
| `commands.cmd_mention_max`                           | `5`           | max users in same message   |
| `mentionplugin.sanitize`                             | `true`        |                             |
| `mentionplugin.mode_user`                            | `1`           | 1 for bubble, 2 for whisper |
| `mentionplugin.mode_everyone`                        | `1`           | 1 for bubble, 2 for whisper |
| `mentionplugin.mode_friends`                         | `1`           | 1 for bubble, 2 for whisper |
| `mentionplugin.timeout_user`                         | `10`          |                             |
| `mentionplugin.timeout_everyone`                     | `5`           |                             |
| `mentionplugin.timeout_friends`                      | `60`          |                             |
| `mentionplugin.timeout_room`                         | `20`          |                             |
| `mentionplugin.logging_database`                     | `true`        |                             |
| `mentionplugin.database.log_timeout_minutes`         | `30`          |                             |

### Permissions

| Key                  | Default Value |
|----------------------|---------------|
| acc_mention          | 1             |
| acc_mention_friends  | 1             |
| acc_mention_everyone | 0             |
| acc_mention_room     | 2             |
| cmd_blockmention     | 1             |

These permissions can be adjusted in the permissions table in your database.
To do so,
open your database and go to the permissions table
and change the values of the mentioned keys as per your requirements.

> [!NOTE]
> To take the changes into effect, you will have to type `:update_permissions` in your hotel or restart the emulator.

## FAQ

### 1. Why am I disconnected when I get a mention in flash client?

Flash Client Error

Your image link is not a valid .png or .gif, and flash client disconnects when receiving the bubble alert.
To resolve this issue, change the key commands.cmd_mention_everyone.look in the emulator_texts to a valid .png or .gif.

### 2. How to report a bug or suggest a feature?

You can report a bug or suggest a feature by creating an issue on the GitHub repository or contacting me on Discord:
brenoepic

