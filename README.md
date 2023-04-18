# ready-bot

![Licence Badge](https://img.shields.io/github/license/burnscommalucas/ready-botlin?link=./LICENSE)
![Server Count Badge](https://img.shields.io/endpoint?url=https%3A%2F%2Flucas.burns.io%2Fshields%2Fguild-count)


A WoW inspired ready-check bot for [Discord](https://discord.com) servers. This bot is a replacement of the 
[classic bot](https://github.com/BurnsCommaLucas/ready-bot) with some new features.

[Click here to add this bot to your server.](https://discord.com/application-directory/389210640612589568) 
(You need to have server management  permissions to add the bot.)

If you like the bot, [vote for it on top.gg!](https://top.gg/bot/389210640612589568) It helps other people find the bot! 
Want to support development of this bot? Find the bot on [GitHub](https://github.com/BurnsCommaLucas/ready-botlin).

If you need help with ready-bot, you can open an issue in GitHub or head over to the 
[ready-bot support server](https://discord.gg/uwkF27Gt9M).

## Usage

Once you add the bot to your server, start a ready check for a number of users with:

```
/check count:<number>
```
or check for specific users with
```
/check mentions:<mention> <mention> ...
```
and have users ready-up with 
```
/ready
```
Full usage can be found by typing 
```
/help
```
Ready checks can be overridden by invoking the `/check` command again, and checks will only be performed if the `count` 
or number of `mentions` (of non-bot members) entered is greater than 0. The person who initiates the ready check may 
also respond to the check as ready.

If you experience any unusual behavior from the bot or think of a feature that could be added, please open an issue on 
GitHub.

---

## FAQ

### What updates are you planning?

#### This whole bot! 
Currently, ready-bot is built from the old [javascript code](https://github.com/BurnsCommaLucas/ready-bot) 
and in the coming weeks I'll be replacing it with this new version.
#### Role mentions! 
As part of this migration, I'm working to add the ability to mention custom roles in a `mentions` 
check. I won't be adding the `@here` or `@everyone` tags just yet since they require additional permissions for the bot
to use.
#### Persistent checks! 
Today, everything related to a check is stored in memory within the bot. This means when the bot 
restarts all checks are destroyed (😥). This migration will add database functionality so that no longer happens! This
is also a good future-proofing step in case the bot grows large enough that I need to have several instances of it 
running simultaneously.
#### Permissions! 
I'd like to allow more control for server admins over who can and can't create checks. Since checks are 
one-per-channel, anyone in the channel can maliciously overwrite the current check at any time. Adding a persistent 
database is the first step toward this goal.

### Why did you rewrite the bot in Kotlin?

Discord has added really excellent features for bots like slash commands and ephemeral messages, and I was able to add 
those to the final update of the old bot. Unfortunately, adding those features to my already brittle Javascript code 
pushed it past its limit and – due to my level of comfort with Javascript – I've been unable to properly diagnose and 
fix the issues that have cropped up.

Switching to Kotlin lets me make use of the experience I have from my day job; I can use tools I'm comfortable with, 
try out design patterns I see at work, and test new libraries or systems I might want to bring back to my job later.
Plus since this project is still just a one-person-enterprise, it behooves me to make it as easy for myself as possible 
to keep things from being a slog.