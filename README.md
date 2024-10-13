# Shady Business
If you are visiting this page, you likely have already seen the YouTube series, which explains how this game works. If you don't care about all the specific details, go to the [**Installation and Important Info**](#installation-and-important-info) section to learn how to set this up. Anyway, I will now explain the inner workings of this plugin in excruciating detail.  

First listing some basic info that doesn’t really fit anywhere
- Players start with 4 lives
- You are able to give other players lives with the /givelife command, however cannot give away your last life
- Name colors go as follows:
- - 4+ lives: Dark Green
- - 3 lives: Green
- - 2 lives: Yellow
- - 1 life: Red
- When you are not red, you play the game as normal and are not allowed to incite attacks
- When you are red, you must abandon your team and are now tasked with killing any remaining player
- You will have to trust your players to not give away their lives before the role reveal
- Ghost players will not be able to use chat
- Keep inventory will be enabled
- Proximity chat is built into this plugin. Put a "." before a message to make it proximity.
- Boogeyman kills do not show up in chat  

The game centers around the idea of giving players special roles at the start of a session. Not every player will have a special role, and will be given the role "Villager." Players with the Villager role will have a larger chance of getting a role in the next session. Role descriptions and specifics will be announced to each player according to their role. Some special roles will have an objective to complete, while others have an ability. Players are for the most part allowed to tell others what role they are. However, you are not allowed to make a plan with a boogeyman as a victim to get a life. There are zero roles that persist on red, although (outside of twins) the roles will come back if you are given a life.
  
Here are a list of roles in the order of priority (If there are not enough players to fill all the roles, the roles at the top of the list will take priority)  
- **The Boogeyman**  
As long as there are 5 or more players that are past one life, There will be either two or three boogeymen each session, with a 50% chance of either number. Each boogeyman is alerted as to who the other boogeymen are, and **every boogeyman** has to get a kill before the session ends, or **all** boogeymen lose one life. Boogeymen are encouraged work together to accomplish the goal. If a boogeyman turns red, they will be exempt from boogeyman duties, and will not need to get a kill to ensure the other boogeymen do not lose a life. Of course, if they are given a life back, they rejoin the boogeymen. A red player that was previously a boogey is not allowed to tell the other people who the boogeymen are.
- **The Necromancer**
One player will be selected to have this role in each session after the first final death. This player is able to give one of their lives to an eliminated player if they choose to, however the eliminated player will not respawn with their items. The revived player will be teleported to the location of the player that revived them.
- **The Condemned**
Starting onward from session two, either two dark green or two light green names will be selected to be the condemned at the start of the session if enough are remaining. These players will be publicly announced to the server as the condemned. When there are around 30 minutes remaining in the session, as long as neither of the condemned has dropped to yellow, a vote will be cast, and the condemned that has the **most votes** when voting is finished will explode. The explosion gets larger each session. Pretty please don't give away your lives to get out of being condemned
- **The Victim**
If there are boogeymen in the session, there will be one victim. This player must try to get themselves killed by a boogeyman. If they succeed, they will gain a life instead of losing one from the boogeyman. The boogeyman that killed the victim will not be cured from this, and will be told that they did an oopsie.
- **The Investigator**  
If there are boogeymen in the session, there will be an investigator. The investigator is given one splash potion of revealing (called the orb of pondering). The “revealing” effect will show the investigator a red box if the affected player is a boogeyman, and a green box if the affected player is not. It will show a yellow box if the affected player is a cured boogeyman.
- **The Mimic**
This player is able to copy another player’s role once by **crouch + right clicking them with an empty hand**. There will be a one-life penalty for not copying a role before the end of the session. The copy is still used if a villager is right clicked, but a Condemned can not be copied because I did not want to code that.
- **The Twins**
Two players are selected to be twins at the start of the session, and they will not be told who the other twin is. These two players will have a maximum of 15 hearts, and will be set to that health at the start of the session. These players will have their health and deaths synced for the entire session. These two players will not be allowed to eat golden apples for the session, because that would break the health link. If either of these two players becomes red, the link will be broken, as roles cannot be maintained on red players. Twins cannot give or receive lives because I am lazy and don't want to deal with reestablishing the health link.
- **The Transporter**
This player can **crouch + right click a player with an empty hand** to select them for teleportation. Selecting the same player again will deselect that player, but selecting a different player will swap their place with the already selected player. This can be used 2 times per session, and is NOT allowed to be used maliciously.









# Installation and Important Info
If you are here because you want to download the plugin and play it with your friend group, here's how you can do it.

1. Create a spigot server or use some other fork of spigot that still has the spigot API. If you are using a free host, make sure the host allows you to install custom plugins, because I will not be trying to add this to any sort of Aternos plugin repo unless this got insanely popular. 
2. Download the latest release from the [**releases page**](https://github.com/Pm7-dev/Shady_Business/releases) and add it to the plugins folder of your server
3. Start your server
4. If you want the texture pack with the death sounds and custom item textures, close your server and set the following values in your `server.properties` file
   ```
   require-resource-pack=true
   resource-pack=https://github.com/Pm7-dev/Shady_Business/raw/refs/heads/main/texturepack.zip
   resource-pack-id=
   resource-pack-prompt=
   resource-pack-sha1=b8a8fac1d6cceeea20a171b6700c0fed57d8d9e0
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
14. If you find a bug or desire a change in the plugin, feel free to [**make an issue**](https://github.com/Pm7-dev/Shady_Business/issues) and I might get around to it. Please do understand that this plugin was created for the series, and any edits done after it is finished will only be made if I happen to have time.

# Credits
_Pm7 for writing all the code
Piffin380 for creating the central idea, inspired from Ultimate Werewolf  
All the Cheap Life members for contributing towards making a great season (presumably. This credit was written before the season started :3)