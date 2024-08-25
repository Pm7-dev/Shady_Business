package me.pm7.shady_business.Commands;

import me.pm7.shady_business.Objects.Nerd;
import me.pm7.shady_business.Objects.RoleData;
import me.pm7.shady_business.Objects.RoleType;
import me.pm7.shady_business.ShadyBusiness;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.messaging.ChannelNameTooLongException;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


// Cow bell, Bass, Snare, Hat, Pling
public class vote implements CommandExecutor, Listener {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();
    static boolean voting = false;

    // This file handles both /startvote and /votemenu because having them separate would be confusing.
    // it will also handle giving the forced vote menu after the timer is up.

    // sorry if the comments of this one are less interesting than the others. mi wile pini e ni. mi tawa.

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(command.getName().equalsIgnoreCase("startvote")) {
            if (!sender.isOp() || voting) {
                return true;
            }

            // just in case an operator does an oopsie
            for(Nerd nerd : plugin.getNerds()) {
                if(nerd.getRole() == RoleType.CONDEMNED && nerd.getLives() < 3) {
                    return true;
                }
            }

            // this little tiny thign mkeas everythign easeir
            voting = true;

            // Create the text given to prompt voting
            BaseComponent[] component = new ComponentBuilder()
                    .append("Condemned player voting has started!\n").color(ChatColor.YELLOW.asBungee())
                    .append("Click here").color(ChatColor.BLUE.asBungee()).bold(true)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/votemenu"))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Open the voting menu").color(ChatColor.GREEN.asBungee()).create()))
                    .append(" to open the voting menu.").color(ChatColor.YELLOW.asBungee()).bold(false)
                    .create();

            // Send it to everyone except the condemned
            for (Nerd nerd : plugin.getNerds()) {
                Player p = Bukkit.getPlayer(nerd.getUuid());
                if (p == null) { continue; }
                if (nerd.getRole() != RoleType.CONDEMNED) {
                    p.spigot().sendMessage(component);
                } else {
                    p.sendMessage(ChatColor.YELLOW + "Condemned player voting has started!");
                }

                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 500, 0.5f);
            }

            // After 8 minutes, if not everyone has voted, force the voting menu onto their screen
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

                // if everyone has already voted before this point, voting will be false
                if(!voting) { return; }


                // get both condemned
                Nerd cond1 = null;
                Nerd cond2 = null;
                for(Nerd nerd : plugin.getNerds()) {
                    if(nerd.getRole() == RoleType.CONDEMNED) {
                        if(cond1 == null) { cond1 = nerd; }
                        else { cond2 = nerd; break; }
                    }
                }
                if(cond1 == null || cond2 == null) {
                    System.out.println("Very bad error just happened with dealing cnndemenred voting. !");
                    return;
                }

                ItemStack one = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                ItemMeta metaOne = one.getItemMeta();
                metaOne.setItemName(cond1.getName());
                metaOne.setLore(List.of("Click to vote"));
                one.setItemMeta(metaOne);

                ItemStack two = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                ItemMeta metaTwo = two.getItemMeta();
                metaTwo.setItemName(cond2.getName());
                metaTwo.setLore(List.of("Click to vote"));
                two.setItemMeta(metaTwo);

                for (Nerd nerd : plugin.getNerds()) {
                    if(nerd.getData().get(RoleData.VOTED) != null && (boolean) nerd.getData().get(RoleData.VOTED)) { continue; }
                    if(nerd.getRole() == RoleType.CONDEMNED) { continue; }

                    Player p = Bukkit.getPlayer(nerd.getUuid());
                    if(p == null) { continue; }

                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 500, 0.5f);
                    p.sendMessage(ChatColor.RED + "You will now be forced to vote. If you do not select a player within the next 4 minutes you also get to explode.");

                    Inventory inv = Bukkit.createInventory(p, 9, "Which player should explode?");
                    inv.setItem(2, one);
                    inv.setItem(6, two);

                    p.openInventory(inv);
                }

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

                    // if everyone has already voted before this point, voting will be false
                    if(!voting) { return; }
                    voting = false;
                    endVote();

                }, 1200L); // 4800
                
            }, 1200L); // 9600

        } else if (command.getName().equalsIgnoreCase("votemenu")) {
            if(!voting) { return true; }
            Nerd nerd = plugin.getNerd(sender.getName());
            if(nerd == null) { return true; }
            if(nerd.getData().get(RoleData.VOTED) != null && (boolean) nerd.getData().get(RoleData.VOTED)) { return true; }

            Player p = (Player) sender;

            // get both condemned
            Nerd cond1 = null;
            Nerd cond2 = null;
            for(Nerd cond : plugin.getNerds()) {
                if(cond.getRole() == RoleType.CONDEMNED) {
                    if(cond1 == null) { cond1 = cond; }
                    else { cond2 = cond; break; }
                }
            }
            if(cond1 == null || cond2 == null) {
                System.out.println("Very bad error just happened with dealing cnndemenred voting. !");
                return true;
            }

            ItemStack one = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta metaOne = one.getItemMeta();
            metaOne.setItemName(cond1.getName());
            metaOne.setLore(List.of("Click to vote"));
            one.setItemMeta(metaOne);

            ItemStack two = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta metaTwo = two.getItemMeta();
            metaTwo.setItemName(cond2.getName());
            metaTwo.setLore(List.of("Click to vote"));
            two.setItemMeta(metaTwo);

            Inventory inv = Bukkit.createInventory(p, 9, "Which player should explode?");
            inv.setItem(2, one);
            inv.setItem(6, two);

            p.openInventory(inv);
        }
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if(!e.getView().getTitle().equals("Which player should explode?") || e.getInventory().getSize() != 9) { return; }
        if(!voting) { e.getWhoClicked().closeInventory(); return; }

        e.setCancelled(true);
        if(e.getSlot() != 2 && e.getSlot() != 6) { return; }

        Nerd nerd = plugin.getNerd(e.getWhoClicked().getUniqueId());
        if(nerd == null || (nerd.getData().get(RoleData.VOTED) != null && (boolean) nerd.getData().get(RoleData.VOTED))) { e.getWhoClicked().closeInventory(); return; }
        ItemStack item = e.getCurrentItem();
        if(item == null) { return; }
        ItemMeta meta = item.getItemMeta();
        Nerd clicked = plugin.getNerd(meta.getItemName());
        if(clicked == null) { return; }

        List<String> data = (List<String>) clicked.getData().get(RoleData.CONDEMNED_VOTE_LIST);
        if(data==null) {data = new ArrayList<>();}
        data.add(e.getWhoClicked().getName());

        nerd.getData().put(RoleData.VOTED, true);
        Player whoClicked = (Player) e.getWhoClicked();
        whoClicked.sendMessage(ChatColor.GREEN + "Vote submitted successfully!");
        whoClicked.playSound(whoClicked, Sound.BLOCK_AMETHYST_BLOCK_STEP, 500, 2.0f);


        e.getWhoClicked().closeInventory();

        // check if voting can be ended
        boolean finished = true;
        for (Player p : Bukkit.getOnlinePlayers()) {

            Nerd pn = plugin.getNerd(p.getUniqueId());
            if(pn == null) { continue; }
            HashMap<RoleData, Object> nd = pn.getData();

            if(pn.getRole() == RoleType.CONDEMNED) { continue; }

            if(!((boolean) nd.get(RoleData.VOTED))) {
                finished = false;
                break;
            }
        }

        if(finished) {
            voting = false;
            endVote();
        }

    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        // just check if the player has voted, if they have, close inv correctly
        if(!e.getView().getTitle().equals("Which player should explode?") || e.getInventory().getSize() != 9) { return; }
        if(!voting) { return; }

        Nerd nerd = plugin.getNerd(e.getPlayer().getUniqueId());
        if(nerd == null || (nerd.getData().get(RoleData.VOTED) != null && ((boolean) nerd.getData().get(RoleData.VOTED)))) { return; }

        HumanEntity p = e.getPlayer();
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> p.openInventory(e.getInventory()), 2L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(voting) {
            e.getPlayer().sendMessage(ChatColor.YELLOW + "There is an ongoing vote! use /votemenu to select.");
        }
    }


    // get ready for the worst thing you will ever see
    static int tick = 0;
    static boolean anticipationOver = false;
    static int runnable;
    void endVote() {
        Bukkit.broadcastMessage(ChatColor.YELLOW + "The voting period has now ended, and the votes will be counted shortly.");
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 500, 0.5f);
        }

        // get both condemned
        Nerd cond1 = null;
        Nerd cond2 = null;
        for(Nerd cond : plugin.getNerds()) {
            if(cond.getRole() == RoleType.CONDEMNED) {
                if(cond1 == null) { cond1 = cond; }
                else { cond2 = cond; break; }
            }
        }
        if(cond1 == null || cond2 == null) {
            System.out.println("Very bad error just happened with dealing cnndemenred voting. ! (but in the ending bvote this time)");
            return;
        }

        final Nerd n1 = cond1;
        final Nerd n2 = cond2;

        runnable = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

            // "anticipation mode" is when the plugin will alternate names in the chat. when anticipation mode is over, it will continue printing the winner's votes until it is out
            final List<String> votesOne =  Arrays.asList("haii", "wan", "tu", "mute", "mute", "mute", "mute", "mute" ); //(List<String>) n1.getData().get(RoleData.CONDEMNED_VOTE_LIST);
            final List<String> votesTwo = Arrays.asList("haii", "wan", "tu", "mute", "mute", "mute", "mute", "mute", "mute", "mute", "mute", "mute", "mute" );//(List<String>) n2.getData().get(RoleData.CONDEMNED_VOTE_LIST);
            if(!anticipationOver) {

                // only run this on every other tick
                if(tick%2==0) {

                    // the index to be getting from each list for this run
                    int index = tick / 2;

                    // check if there is a vote for this index in the first condemned vote list
                    if(index < votesOne.size()) {

                        // if there is, broadcast a vote for this player, and then run the 12 tick wait to check for the second player
                        System.out.println("index : " + index);

                        Bukkit.broadcastMessage(ChatColor.YELLOW + n1.getName() + "!");
                        for(Player p : Bukkit.getOnlinePlayers()) { p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 500, ((float) index * 0.075f) + 0.5f); } //play sound
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

                            // check if there is a vote for this index in the second condemned vote list
                            if(index < votesTwo.size()) {

                                // if there is, broadcast a vote for this player, and do nothing because the loop will run again
                                System.out.println("index : " + index);

                                Bukkit.broadcastMessage(ChatColor.BLUE + n2.getName() + "!");
                                for(Player p : Bukkit.getOnlinePlayers()) { p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 500, ((float) index * 0.075f) + 0.5f); } //play sound

                            } else {

                                // if there is not, we know that the first player must have more votes, so broadcast one more of them and exit the anticipation mode
                                System.out.println("index : " + index);

                                Bukkit.broadcastMessage(ChatColor.YELLOW + n1.getName() + "!");
                                for(Player p : Bukkit.getOnlinePlayers()) { p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 500, ((float) index * 0.075f) + 0.5f); } //play sound

                                tick = index;
                                anticipationOver = true;
                            }
                        }, 12L);

                    } else {

                        // if there isn't, check if there is one for that index in the second condemned vote list
                        if(index < votesTwo.size()) {

                            // if there is, we know that the second player must have more votes, so broadcast one more of them and exit the anticipation mode
                            System.out.println("index : " + index);

                            Bukkit.broadcastMessage(ChatColor.BLUE + n2.getName() + "!");
                            for(Player p : Bukkit.getOnlinePlayers()) { p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 500, ((float) index * 0.075f) + 0.5f); } //play sound

                            tick = index;
                            anticipationOver = true;

                        } else {

                            // if there still is not, declare a tie
                            tieThingsOff(n1, n2, ((float) (votesOne.size()-1) * 0.075f) + 0.5f);
                            Bukkit.getScheduler().cancelTask(runnable);

                        }
                    }
                }

            } else {
                
                // now that anticipation mode is over, we can just start printing every tick
                if(votesOne.size() > votesTwo.size()) {
                    if(tick < votesOne.size()) {

                        // while still ticking, play the same note instead of upwards ones or something
                        System.out.println("index : " + tick);

                        Bukkit.broadcastMessage(ChatColor.YELLOW + n1.getName() + "!");
                        for(Player p : Bukkit.getOnlinePlayers()) { p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 500, ((float) (votesTwo.size()-1) * 0.075f) + 0.5f); } //play sound

                    } else {

                        // begin the ending of the animation and escape this garbage runnable
                        killLoser(n1, ((float) (votesOne.size()-1) * 0.075f) + 0.5f);
                        Bukkit.getScheduler().cancelTask(runnable);
                    }
                } else {
                    if(tick < votesTwo.size()) {

                        // while still ticking, play the same note instead of upwards ones or something
                        System.out.println("index : " + tick);

                        Bukkit.broadcastMessage(ChatColor.BLUE + n2.getName() + "!");
                        for(Player p : Bukkit.getOnlinePlayers()) { p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 500, ((float) (votesOne.size()-1) * 0.075f) + 0.5f); } //play sound

                    } else {

                        // begin the ending of the animation and escape this garbage runnable
                        killLoser(n2, ((float) (votesOne.size()-1) * 0.075f) + 0.5f);
                        Bukkit.getScheduler().cancelTask(runnable);
                    }
                }
            }

            tick++; // "tick" will actually be 12 ticks

        }, 60L, 12L); // after 60 ticks, run this every 12 ticks

    }

    public static boolean exploding = false; // the use case for this is actually in the Twins listener
    void killLoser(Nerd nerd, float pitch) {

        tick = 0;
        anticipationOver = false;

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Bukkit.broadcastMessage(ChatColor.RED + "Bye :D");
            for(Player p : Bukkit.getOnlinePlayers()) { p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 500, pitch); } //play sound
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Player p = Bukkit.getPlayer(nerd.getUuid());
                if(p == null) {
                    Bukkit.broadcastMessage(ChatColor.RED + "aw man.");
                    nerd.removeLife();
                } else {
                    exploding = true;
                    p.getWorld().createExplosion(p.getLocation(), 5, false);
                    p.damage(999999.9d); // surely nobody will survive this

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> exploding = false, 5L);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> p.sendMessage(ChatColor.BLUE + "These are the people who voted for you. \n" + nerd.getData().get(RoleData.CONDEMNED_VOTE_LIST)), 600);

                }
            }, 8L);
        }, 30L);

    }

    void tieThingsOff(Nerd n1, Nerd n2, float pitch) {

        tick = 0;
        anticipationOver = false;

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "Oh, there's a tie. I see.");
            for(Player p : Bukkit.getOnlinePlayers()) { p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 500, pitch); } //play sound
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Bukkit.broadcastMessage(ChatColor.BLUE + "Well, one of you still has to die. Hm...");
                for(Player p : Bukkit.getOnlinePlayers()) { p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 500, pitch); } //play sound
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "Oh well, I'll just pick one");
                    for(Player p : Bukkit.getOnlinePlayers()) { p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 500, pitch); } //play sound
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

                        Nerd selected;
                        if(new Random().nextInt(2) == 0) { selected = n1; }
                        else { selected = n2; }

                        Player p = Bukkit.getPlayer(selected.getUuid());
                        if(p == null) {
                            Bukkit.broadcastMessage(ChatColor.RED + "The chosen Condemned, " + selected.getName() + " is offline. One life has been removed.");
                            selected.removeLife();
                        } else {
                            exploding = true;
                            p.getWorld().createExplosion(p.getLocation(), 5, false);
                            p.damage(999999.9d); // surely nobody will survive this

                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> exploding = false, 5L);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> p.sendMessage(ChatColor.BLUE + "These are the people who voted for you. \n" + selected.getData().get(RoleData.CONDEMNED_VOTE_LIST)), 600);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                for(Player p1 : Bukkit.getOnlinePlayers()) {
                                    Nerd n = plugin.getNerd(p1.getUniqueId());
                                    if(n==null) {continue;}

                                    if(n.getRole() == RoleType.CONDEMNED) { continue; }
                                    if(n.getData().get(RoleData.VOTED) == null || !((boolean) n.getData().get(RoleData.VOTED))) {
                                        Bukkit.broadcastMessage(ChatColor.RED + p1.getName() + " forgot to vote, imagine.");
                                        p1.setHealth(0.0d);
                                    }
                                }
                            }, 700L);

                        }
                    }, 8L);
                }, 35L);
            }, 35L);
        }, 30L);
    }
}
