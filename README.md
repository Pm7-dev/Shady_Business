# Shady Business
This is the minecraft plugin used for the Youtube series Cheap Life: Shady Business. If you're here for a specific reason, you most definitely already know what this plugin does, so I will not write it down. If you do not know what this plugin does, you are either lost, or coming from a different project, which is highly unlikely because Cheap Life is probably the largest thing I will ever amount to. If you really want to know what this does, you can likely still find Piffin380's first video in this series, which probably has some competent into explaining most of what this is (this is written before that video is created, so I can only assume its competence. Please forgive me if it is inadequate.) If you are still reading this, you are either easily entertained or very bored. In either case, go have a look through the code for more uninteresting statements made by me.

# Installation and Important Info
If you are here because you want to download the plugin and play it with your friend group, here's how you can do it.

1. Create a spigot server or use some other fork of spigot that still has the spigot API. If you are using a free host, make sure the host allows you to install custom plugins, because I will not be trying to add this to any sort of Aternos plugin repo unless this got insanely popular. 
2. Download the latest release from the [releases page](https://github.com/Pm7-dev/Shady_Business/releases) and add it to the plugins folder of your server
3. Start your server
4. If you want the texture pack, close your server and set the following values in your `server.properties` file
    ```
   If you are reading this, you are here before the texture pack has been released. The pack will likely be available shortly after session 2 is finished.
   ```
5. Make sure your player has operator permissions
6. When you've gotten all of your players online, run `/startsession`. This will start the game, and will give a role to every player that is online after a few minutes. Players can still join after this command is run, but they will not have a role until the next session
7. If players forget what their role is, or forget some aspect about it, they can run `/info` to view the information about their role.
8. If a death should be reversed, an operator can run `/setlife <player> <amount>` to set a player's life count.
9. If a boogeyman gets a kill via an indirect method, they are allowed to run `/cure`. This command can be run by anyone, so you have to trust your players to keep things fair.
10. If the Condemned role has been run, towards the end of the session, an operator should run `/startvote`. This will start the vote, believe it or not.
11. If a player has accidentally cleared their chat, or has just logged in after the vote was started, they can run `/votemenu` to see the menu instead of clicking the chat message
12. At the end of the session, run `/endsession` to check if the boogeymen have succeeded, and if the mimic has copied a role
13. If something with a different plugin breaks, and you must use `/reload`, get every player to log off and log back on to see the name colors.
14. If you find a bug or desire a change in the plugin, feel free to [make an issue](https://github.com/Pm7-dev/Shady_Business/issues) and I might get around to it. Please do understand that this plugin was created for the series, and any edits done after it is finished is purely out of boredom.

# Credits
All programming was done by _Pm7 using the Spigot API  
The central idea of this was created by Piffin380, inspired from Ultimate Werewolf  
All the Cheap Life members for contributing towards making a great season (presumably)