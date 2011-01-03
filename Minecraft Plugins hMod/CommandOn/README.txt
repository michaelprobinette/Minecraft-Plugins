*** CommandOn Readme by Vandolis ***

This plugin allows you to specify lists of group/player specific commands to be run when a player dies, respawns, logs in or logs out. Now completely usable through in game commands only, with the exception of the properties file.

On first start up of your server the plugin will create 3 files. CommandOn.txt (Lists of commands), CommandOnPlayers.txt (Lists of players and what they are set to ignore), and CommandOn.properties. The only thing you need to change in the .properties file is to set your groups allowed to perform certain tasks.

editlistgroups=admins
editignoregroups=all

By default it will look like this. The editlistgroups key are the groups that can edit the actual command lists, recommended to leave this default. The editignoregroups are the groups that can set what command lists they want to ignore.

The CommandOn.txt holds lists of commands that look like this:

## ondeath
default:%blue looks like [name] died.
## onlogin
admins:/time day
nogroup:/warp testing
default:/playerlist
## onlogout
default:%purple [name] has left the server.
## ondeath
default:%red Looks like [name] has died, too bad.
## onrespawn
default:/home:@yellow welcome back to the living.
## onserverstart
default:#list:#save-all

The three things to note here are in the "%purple [name] has left the server." and "@yellow welcome back to the living." These are the only things outside of commands you should remember. Adding a '%' in front of text will broadcast it to all players online, whereas a '@' in front will only message the player involved. You can also add a color by putting the color directly after the symbol. By putting [name] somewhere it will replace it with the players name when ran. So if John logged off everyone would see in purple letters "John has left the server"

CommandOnPlayers.txt holds the ignore information and will look like this after some use:

## Billy
ondeath:name
onlogin:group:default
## Cody
ondeath:all
onlogin:group:default
onrespawn:name
## Jim
name
onrespawn:group
onlogin:group:default
## Vandolis
ondeath:name
onlogin:group
default
## Tom
ondeath:name
onlogin:group:default


It is simply "## PlayerName" followed on the next line by the first tag and section you want to ignore. Additional sections for a tag can be added by placing ':' between. This is much easier through the ingame commands, as is most of the editing.

For a list of tags or sections, use the "/co help tags" or "/co help sections" commands.

As suggested by RustyDagger you can now have a player.sendMessage from the file. Formatting is "@Message to send" or "@BLUE Colored message".

You can now have a message broadcasted to everyone by using "%Message" or "%COLOR Message" Example is above. Colors are:

BLACK, BLUE, DARKPURPLE, GOLD, GRAY, GREEN, LIGHTBLUE, LIGHTGRAY, LIGHTGREEN, LIGHTPURPLE, NAVY, PURPLE, RED, ROSE, WHITE, YELLOW
Examples: "@red This is in red.", "@blue This is in blue.", "%gold This is in gold.", "%navy This is in navy."

You can now use a couple of variables in your commands. These are: 

"[NAME]", "[PLAYERX]", "[PLAYERY]", "[PLAYERZ]", "[ATTACKER]", "[DEFENDER]", "[WINNER]", "[LOSER]", "[CAUSE]". 
Please note that they are case sensitive and must be in all caps.

[NAME] - Inserts the players name here.
[PLAYERX] - Inserts the players x position here.
[PLAYERY] - Inserts the players y position here.
[PLAYERZ] - Inserts the players z position here.
[ATTACKER] - Inserts the attackers name here. For PvP.
[DEFENDER] - Inserts the defenders name here. For PvP.
[WINNER] - Inserts the winners name here. For PvP.
[LOSER] - Inserts the loosers name here. For PvP.
[CAUSE] - Inserts the cause of death here. For deaths due to things like falling, burning, etc.

You can now specify server console commands to be ran by adding '#' before a console command. Also added the OnServerStart tag which is run on the plugins initialization. Only use the default section for this tag. You can use both '#' commands and '/' commands, and any '/' commands will be added to a que that is ran by the first person to log on.

*** Install: ***

Download CommandOn.zip and extract it in your /bin/plugins/ folder in your server. Now open server.properties in an editor and change the line that says:
plugins=
to:
plugins=CommandOn

*** InGame Commands! ***

Plugin command: "/commandon" (Or /co if you don't feel like typing.)
/commandon [help,me,ignore,allow,admin]

Help usage:
"/commandon help [command]" Displays specific help info for the command. If no command is given lists the commands.

Me usage:
"/commandon me [ignore,command]" Displays the info for the player. Ignore will display ignored command lists, command will list any name specific commands for you. If you just do "/co me" it will run both.

Ignore usage (Uses the "editignoregroups" key in the properties file):
"/commandon ignore [tag] [section]" Will ignore that specific command list next time it needs to run for you. If you want to ignore all of a certain section, for example "default" just put that in place of the tag. So the command would be "/commandon ignore default"

Allow usage:
"/commandon allow [tag] [section]" Exact opposite of ignore.

The following are available to any admins, as well as anyone in a group specified in the properties file "editlistgroups" key.
Admin usage:
"/commandon admin [list,add,remove]" Admin commands for setting up and maintaining the plugin.
"/commandon admin list [all,tag]" Lists the command lists for the given tag. Lists all if you put "all" in place of the tag.
"/commandon admin add [tag] [section] [command]" Adds the given command to the tag and section specified.
"/commandon admin remove [tag] [section] [command]" Opposite of add. Make sure you spell the command EXACTLY.

Example commands:
"/co admin add onpvpstart default %blue [ATTACKER] has begun fighting [DEFENDER]!" - When a fight is started a message will be sent out to everyone saying the message with the attackers and defenders name in their variables.
"/co admin remove onpvpstart default %blue [ATTACKER] has begun fighting [DEFENDER]!" - This will remove the prior command. Note that it is spelled exactly.
"/co admin add onlogin nogroup /warp testing" - Warps anyone without a group to the testing area when they log on.
"/co admin add onpvpkill default /pay 200" - Pays the winner of the battle. (My private economy mod, might be different for actual ones.)
"/co admin add onpvpkill default /collectbounty [LOOSER]" - Gives the winner the bounty on the loosers head. (My private PvP mod, might be different for actual ones.)
"/co admin add onpvpkill default /addbounty [WINNER]" - Adds the winner to the bounty board. (My private PvP mod, might be different for actual ones.)
"/co admin add onpvpdeath default @purple Y U NO STAY ALIVE?" - Yes.
"/co ignore onlogin default" - Ignores the default section of onlogin.
"/co allow onlogin default" - Allows the default section of onlogin.
"/co me" - Shows any command lists under your name, as well as any ignored tags and sections.
"/co list all" - Lists all of the commands under their tags. This can get spammy when you use this plugin as much as I do so instead you can use...
"/co list onlogin" - Lists all of the commands under the tag onlogin.

*** Tags ***
OnServerStart - Called when the plugin is loaded. Use '#' in front of a command for it to be executed by the server, or use a '/' command and it will be ran by the first person to log on.
OnLogin - Called when the player connects to the server, is ran immediately. No odd behavior known.
OnLogout - Called when the player leaves the server. Note the the player has disconnected, so don't use any /commands. Normally just use it to broadcast something.
OnDeath - Called as soon as the player dies. Do not put any /warps in here, as the player is dead.
OnRespawn - Called as soon as the player clicks respawn. Sometimes if there is a /warp in here the server will complain that the player moved wrongly, disregard this and aquire diamonds.
OnDeathFall - Called when the player dies from fall damage.
OnDeathWater - Called when the player dies from drowning.
OnDeathFire - Called when the player is burnt to a crisp.
OnDeathLava - Called when the player is burnt to ashes.
OnDeathCactus - Called when the player is killed by a cactus. Nothing funny about it.
OnDeathExplosion - Called when the player is blown to bits and pieces.
OnDeathMob - Called when the player is killed by a mob. This will be ran along with any specific ondeath tag.
OnDeathCreeper - Called when the player is killed by a creeper. Ran with OnDeathMob.
OnDeathSkeleton - Called when the player is killed by a skeleton. Ran with OnDeathMob.
OnDeathSpider - Called when the player is killed by a spider. Ran with OnDeathMob.
OnDeathZombie - Called when the player is killed by a zombie. Ran with OnDeathMob.
OnDeathPigZombie - Called when the player is killed by a pig zombie. Ran with OnDeathMob.
OnDeathSlime - Called when the player is killed by a slime. Ran with OnDeathMob.
OnDeathGhast - Called when the player is killed by a Ghast. Ran with OnDeathMob.
OnPvPStart - Called when a player starts to fight another player. Use [ATTACKER] and [DEFENDER].
OnPvPEnd - Called when the battle has ended. Use [WINNER] and [LOOSER].
OnPvPKill - Called by the winner of the battle.
OnPvPDeath - Called by the looser of the battle.
OnDeathSuicide - Called when a player kills himself.


*** Sections ***
Group - A group name.
Name - A player name.
Default - Ran by everyone.
Nogroup - Ran by anyone without a group.


*** Changelog: ***
v1.1.6: More bug fixes that Slowriot noticed. Added Ondeath(PigZombie, Ghast, Slime). Added OnDeathSuicide. Source is now available on Github.

v1.1.5: Fixed the /co me command. Took out a good deal of unnessesary code. Added a readme to slim down the post on hey0.net. Includes a couple example commands.
v1.1.4: Fixed the pvp tags.
v1.1.3: Fixed a BUNCH of bugs that Slowriot found, thank you very much for that. These include some spamming problems with the new tags, and I can promise that it will not spam anymore, I felt like an idiot when I found the right solution. Played around with the pvp tags a bit, new variables [WINNER] and [LOOSER] were added, might add a weapon next build, but I am feeling tired :/ Also added a [CAUSE] variable that will give the cause of the death, for mob tags use [ATTACKER]. Now has mob specific tags, as well as a general mob tag. Specific mob tags are OnDeathCreeper, OnDeathZombie, OnDeathSpider, OnDeathSkeleton, OnDeathMob. Will add nether mobs next release.
v1.1.2: Disregard this, was a complete failure and didn't make it to mediashare, might have snuck out on Updatr.
v1.1.1: Fixed a nullpointer bug found by Revenger, thanks for pointing that out!
v1.1.0: Added new tags for specific types of death, typically formatted as OnDeath+Cause. Examples are OnDeathWater, OnDeathFire, OnDeathExplosion, OnDeathLava, OnDeathFall, OnDeathCactus. Also added a couple of PvP tags which I have yet to test (Nobody wanted to get on my server and kill me, go figure.) but hopefully will work. The tags are OnPvPKill, OnPvPDeath, OnPvPStart and OnPvPEnd.
v1.0.3: Complied for 132 and added Updatr support.
v1.0b: Added the OnServerStart tag, as well as the ability to run server console commands by adding a '#' before the command.
v1.0a: Added ingame commands to handle most of the functions. Use "/commandon help" for more info. Changed the code completely. Not even kidding, decompile and compare between the last version, its nuts. It *should* now load the data at the beginning and be done reading, hopefully stopping it from constantly opening more. One thing to note though is that everytime somebody makes a change to the list, or to their ignores it gets written to the file immediately. I spent a solid hour or two debugging, so I hope it should be mostly bug free. But if you find a bug please let me know.
v0.8b: Thanks to Cryoma for pointing out two major bugs with broadcasting. Broadcast spamming is fixed as well as the [NAME] tag when no color is provided. Also fixed a bug where a player specific command list would not be ran if the player was not in a group.
v0.8a: Added ability to broadcast to all players with '%' with the same color system as with '@'. Implemented system to allow ignored command lists on a player by player basis. Not very elegant but it is still a WIP. Minor bug fixes. Implemented a 'nogroup' section in CommandOn.txt. This list will be ran if the player does not currently belong to any groups, aka new players. Complete code revamp.
v0.7-v0.8: Added a lot of things, went to update post, checked one more thing in game, found out everything was broken. Repeat this about 4 times.
v0.6b: Added ability to sendMessage from the file. See above for details.
v0.6: Changed plugin name to CommandOn. Changed how the .txt is read, as well as included two new flags, "## OnLogin" and "## OnDeath". So far I have noticed that there is a server warning if a /warp command is used in the OnDeath list, but it still performs it. Working on a fix for that.
v0.5: Ok finally got it working mostly properly. You can now make any player in any group use any command.... any. So you can make a guest warp to your house right on the login, even if they are not even allowed to edit terrain. And yes I am proud of this, finally got it working after ALL DAY working on it, turns out I was just an idiot and had it right from the beginning but as I found out "null/timeday" is not a command :s neither is "null/time day" although that was pretty close.
So try it out peeps and let me know what you think.
v0.0-v0.3: Whole lotta boring stuff. Got it to a semi-working stage and then soaked a couple hours into my fight with .setCommands

*** Thanks: ***
Thank you to hey0 for this amazing mod, and to the community that keeps it alive. Also a huge thanks to everyone that gave me ideas or reported bugs. And thanks for downloading! I'll do my best to keep it in a working state, and add new features as requested.

Special thanks goes out to Vicflo for the idea for this plugin, and to RustyDagger for keeping the ideas coming! And another special thanks to Slowriot, that bug report was formatted beautifully and was very well recieved.


*** My testing CommandOn.txt ***
## onlogin
vandolis:@Colors.red Hello
admins:@You are an admin
mods:@You are a mod
nogroup:@Why are you not in a group?
## onrespawn
default:/home:/warp storage
## ondeathfall
default:@blue You have fallen
## ondeathcactus
default:@Red You died from a cactus!
## ondeathlava
default:@Blue you died from lava!
## ondeathfire
default:@RED FIRE!!
## ondeathwater
default:@blue water!!:@[NAME] drown himself.
## ondeathexplosion
default:@red so many pieces!
## ondeathcreeper
default:@purple Ahhh a creeper!
## onpvpkill
default:@yellow well done
## onpvpdeath
default:@yellow Better luck next time.
## ondeathmob
default:@You got killed by a mob!
## ondeath
default:%red Looks like [NAME] has died!
## onserverstart
default:/warp storage
## ondeathzombie
default:@A zombie killed you
## ondeathspider
default:@you got killed by a spider
## ondeathskeleton
default:@you got killed by a skeleton
## onpvpstart
default:%blue [ATTACKER] has started fighting [DEFENDER]
## onpvpend
default:%purple [WINNER] has defeated [LOOSER] in mortal combat!