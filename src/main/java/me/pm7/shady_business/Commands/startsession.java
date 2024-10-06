package me.pm7.shady_business.Commands;

import me.pm7.shady_business.Objects.Nerd;
import me.pm7.shady_business.Objects.RoleData;
import me.pm7.shady_business.Objects.RoleType;
import me.pm7.shady_business.ScoreboardManager;
import me.pm7.shady_business.ShadyBusiness;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class startsession implements CommandExecutor {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();
    static  FileConfiguration config = plugin.getConfig();
    Random rand = new Random();
    boolean cooldown = false;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(commandSender.isOp()) {
            List<Nerd> nerds = plugin.getNerds();

            if(!config.getBoolean("started")) {
                Bukkit.broadcastMessage(ChatColor.RED + "Game Started!");

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if(p.getGameMode() == GameMode.SURVIVAL) {
                        Nerd nerd = new Nerd();
                        nerd.setUuid(p.getUniqueId());
                        nerd.setName(p.getName());
                        nerd.setLives(4);
                        nerd.setHadRoleLastSession(false);
                        nerd.setRole(RoleType.VILLAGER);

                        HashMap<RoleData, Object> data = new HashMap<>();
                        data.put(RoleData.VOTED, false);
                        nerd.setData(data);

                        nerds.add(nerd);

                        ScoreboardManager.UpdatePlayerScore(nerd);
                    }
                }

                // Actually make sure to set the data for the nether lol
                World world = Bukkit.getWorld("world");
                world.setDifficulty(Difficulty.HARD);
                world.setGameRule(GameRule.DO_INSOMNIA, false);
                world.setGameRule(GameRule.KEEP_INVENTORY, true);

                World nether = Bukkit.getWorld("world_nether");
                nether.setDifficulty(Difficulty.HARD);
                nether.setGameRule(GameRule.DO_INSOMNIA, false);
                nether.setGameRule(GameRule.KEEP_INVENTORY, true);

                config.set("started", true);
            } else {
                Bukkit.broadcastMessage(ChatColor.RED + "Session Started!");

                for(Nerd nerd : nerds) {
                    ScoreboardManager.UpdatePlayerScore(nerd);

                    if(nerd.getRole() == RoleType.VILLAGER) { nerd.setHadRoleLastSession(false); }
                    else { nerd.setHadRoleLastSession(true); }

                    nerd.setRole(RoleType.VILLAGER);

                    HashMap<RoleData, Object> data = new HashMap<>();
                    data.put(RoleData.VOTED, false);
                    nerd.setData(data);

                    Player p = Bukkit.getPlayer(nerd.getUuid());
                    if(p != null) {
                        p.getAttribute(Attribute . GENERIC_MAX_HEALTH).setBaseValue(20.0d);

                        // Clear the investigator potion lol
                        Inventory inv = p.getInventory();
                        for(ItemStack item : inv.getContents()) {
                            if(item == null) { continue; }
                            if(item.getType() == Material.SPLASH_POTION && item.getItemMeta().getItemName().equals("Orb of Pondering")) {
                                inv.remove(item);
                            }
                        }
                    }
                }
            }

            plugin.saveData();

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Bukkit.broadcastMessage(ChatColor.RED + "Roles will be chosen soon.");

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

                    // add one to the session count here, for some reason
                    int sessions = config.getInt("sessions");
                    sessions++;
                    config.set("sessions", sessions);

                    // Don't allow red names, ghosts, or offline players to get a role
                    List<Nerd> roleSelectable = new ArrayList<>(nerds);
                    for (Nerd nerd : nerds) {
                        if (nerd == null || nerd.getLives() <= 1 || Bukkit.getPlayer(nerd.getUuid()) == null) {
                            roleSelectable.remove(nerd);
                            continue;
                        }
                        if (!nerd.getHadRoleLastSession()) {
                            roleSelectable.add(nerd);
                        }
                    }

                    // Stupidest possible way of making either two or three boogeymen
                    boolean boogeyExists = false;
                    if(new HashSet<>(roleSelectable).size() > 4) {
                        boogeyExists = true;
                        for (int i = 0; i < rand.nextInt(2) + 2; i++) { addRole(RoleType.BOOGEYMAN, roleSelectable); }
                    }

                    // neck romancer
                    if(config.getBoolean("someoneDeadLol")) {
                        if(!roleSelectable.isEmpty()) { addRole(RoleType.NECROMANCER, roleSelectable); }
                    }

                    // role of scope creep
                    if(!roleSelectable.isEmpty() && sessions > 1) {
                        List<Nerd> darkGreen = new ArrayList<>();
                        for(Nerd nerd : roleSelectable) {
                            if(nerd.getLives() >= 4) {
                                darkGreen.add(nerd);
                            }
                        }
                        if(new HashSet<>(darkGreen).size() > 1) {
                            Nerd n1 = addCondemned(darkGreen);
                            roleSelectable.removeIf(nerd -> nerd.getUuid() == n1.getUuid());
                            Nerd n2 = addCondemned(darkGreen);
                            roleSelectable.removeIf(nerd -> nerd.getUuid() == n2.getUuid());
                        } else {
                            // If it cannot be done with dark greens, continue with light greens
                            List<Nerd> green = new ArrayList<>();
                            for(Nerd nerd : roleSelectable) {
                                if(nerd.getLives() == 3) {
                                    green.add(nerd);
                                }
                            }
                            if(new HashSet<>(green).size() > 1) {
                                Nerd n1 = addCondemned(green);
                                roleSelectable.removeIf(nerd -> nerd.getUuid() == n1.getUuid());
                                Nerd n2 = addCondemned(green);
                                roleSelectable.removeIf(nerd -> nerd.getUuid() == n2.getUuid());
                            }
                        }
                    }

                    // Victmdmsfk
                    if(boogeyExists) {
                        if (!roleSelectable.isEmpty()) { addRole(RoleType.VICTIM, roleSelectable); }
                    }

                    // What do you call an alligator in a vest?
                    if(boogeyExists) {
                        if(!roleSelectable.isEmpty()) { addRole(RoleType.INVESTIGATOR, roleSelectable); }
                    }

                    // mimc
                    if(!roleSelectable.isEmpty()) { addRole(RoleType.MIMIC, roleSelectable); }

                    // twinks :3
                    if(new HashSet<>(roleSelectable).size() > 1) {
                        Nerd twin = addTwin(roleSelectable);
                        Nerd twin2 = addTwin(roleSelectable);
                        twin.getData().put(RoleData.TWIN_PARTNER_NAME, twin2.getName());
                        twin2.getData().put(RoleData.TWIN_PARTNER_NAME, twin.getName());
                        // PRIME TWIN OF YOUR LIFE! (gotta do it) NOW! (don't wait and) LIVE IT! (today)
                        twin.getData().put(RoleData.TWIN_IS_PRIME, true);
                        twin2.getData().put(RoleData.TWIN_IS_PRIME, false);
                    }

                    // jan pi wawa tawa
                    if(!roleSelectable.isEmpty()) { addRole(RoleType.TRANSPORTER, roleSelectable); }
                    // side note: "wawa tawa" has become so fun to say that I now exclusively refer to the transporter as this in my head

                    //sou[p
                    for(Player p : Bukkit.getOnlinePlayers()) {
                        p.sendTitle(ChatColor.GREEN + "Your role is...", "", 10, 100, 20);
                        p.playSound(p, Sound.BLOCK_VAULT_ACTIVATE, 500, 0.5f);
                    }

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        for(Nerd nerd : plugin.getNerds()) {
                            Player p = Bukkit.getPlayer(nerd.getUuid());
                            if(p==null) { continue; }

                            p.playSound(p, Sound.ENTITY_BREEZE_SHOOT, 500, 1.0f);
                            p.playSound(p, Sound.ENTITY_BREEZE_SHOOT, 500, 2.0f);

                            // scope creep
                            nerd.getData().put(RoleData.VOTED, false);

                            switch (nerd.getRole()) {
                                case VILLAGER: {
                                    p.sendTitle(ChatColor.WHITE + "Villager", "", 10, 70, 20);
                                    p.sendMessage(ChatColor.WHITE + "You are a Villager! You have no special task to do today :D");
                                    break;
                                }
                                case BOOGEYMAN: {
                                    p.sendTitle(ChatColor.RED + "Boogeyman", "", 10, 70, 20);

                                    // waste of processing power
                                    String boogies;
                                    List<String> boogeymen = new ArrayList<>();
                                    for(Nerd boogey : plugin.getNerds()) {
                                        if(boogey.getRole() == RoleType.BOOGEYMAN && boogey.getUuid() != nerd.getUuid()) {
                                            boogeymen.add(boogey.getName());
                                        }
                                    }
                                    if(boogeymen.size() == 2) { boogies = "new friends " + boogeymen.get(0) + ", and " + boogeymen.get(1); }
                                    else { boogies = "new friend " + boogeymen.getFirst(); }

                                    p.sendMessage(ChatColor.RED + "You are a Boogeyman! You and your " + boogies + " must each get a kill before the end of the session, or each of you will lose one life.");

                                    nerd.getData().put(RoleData.BOOGEYMAN_CURED, false);
                                    break;
                                }
                                case INVESTIGATOR: {
                                    p.sendTitle(ChatColor.BLUE + "Investigator", "", 10, 70, 20);
                                    p.sendMessage(ChatColor.BLUE + "You are the Investigator! Your task is to figure out who the boogeymen are. You will now receive one splash potion of revealing, which will put a red box in chat if it hits a boogeyman, and a green box in chat if it doesn't. Look for large groups to get the most people!");

                                    Inventory inv = p.getInventory();
                                    if(inv.firstEmpty() == -1) {
                                        nerd.getData().put(RoleData.INVESTIGATOR_NEEDS_POTION, true);
                                    }
                                    else {
                                        nerd.getData().put(RoleData.INVESTIGATOR_NEEDS_POTION, false);

                                        ItemStack ponderingOrb = new ItemStack(Material.SPLASH_POTION);
                                        PotionMeta meta = (PotionMeta) ponderingOrb.getItemMeta();
                                        meta.setItemName("Orb of Pondering");
                                        meta.setRarity(ItemRarity.RARE);
                                        meta.setCustomModelData(2);
                                        meta.setColor(Color.RED);
                                        meta.addCustomEffect(new PotionEffect(PotionEffectType.LUCK, PotionEffect.INFINITE_DURATION, 1, true, false, false), true);
                                        ponderingOrb.setItemMeta(meta);
                                        inv.addItem(ponderingOrb);
                                    }
                                    break;
                                }
                                case NECROMANCER: {
                                    p.sendTitle(ChatColor.GOLD + "Necromancer", "", 10, 70, 20);
                                    p.sendMessage(ChatColor.GOLD + "You are the Necromancer! You are able to give one of your lives away to a ghost player with /givelife!");

                                    nerd.getData().put(RoleData.NECROMANCER_USED, false);
                                    break;
                                }
                                case VICTIM: {
                                    p.sendTitle(ChatColor.GREEN + "Victim", "", 10, 70, 20);
                                    p.sendMessage(ChatColor.GREEN + "You are the Victim! Your goal is to get killed by a boogeyman. If you are killed, you will gain a life instead of losing a life, and the boogeyman will not be cured.");

                                    nerd.getData().put(RoleData.VICTIM_COMPLETED, false);
                                    break;
                                }
                                case MIMIC: {
                                    p.sendTitle(ChatColor.LIGHT_PURPLE + "Mimic", "", 10, 70, 20);
                                    p.sendMessage(ChatColor.LIGHT_PURPLE + "You are the Mimic! You must right click to copy someone else's role before the end of the session or you will lose a life! To select a player, crouch and right click them with an empty hand.");
                                    break;
                                }
                                case TWINS: {
                                    p.sendTitle(ChatColor.YELLOW + "Twin", "", 10, 70, 20);
                                    p.sendMessage(ChatColor.YELLOW + "You are one of the Twins! Your health will be linked to another players' for the entire session or until one of you turn red. Good luck!");

                                    Player other = Bukkit.getPlayer(plugin.getNerd((String) nerd.getData().get(RoleData.TWIN_PARTNER_NAME)).getUuid());

                                    other.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30.0d);
                                    p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30.0d);
                                    other.setHealth(30.0D);
                                    p.setHealth(30.0D);

                                    Nerd twinPrime;
                                    Nerd otherTwin;
                                    if ((boolean) nerd.getData().get(RoleData.TWIN_IS_PRIME)) {
                                        twinPrime = nerd;
                                        otherTwin = plugin.getNerd((String) nerd.getData().get(RoleData.TWIN_PARTNER_NAME));
                                    } else {
                                        twinPrime = plugin.getNerd((String) nerd.getData().get(RoleData.TWIN_PARTNER_NAME));
                                        otherTwin = nerd;
                                    }

                                    twinPrime.getData().put(RoleData.TWIN_SHARED_HEALTH, 30.0d);
                                    otherTwin.getData().put(RoleData.TWIN_SHARED_HEALTH, -3.621d); // 3:

                                    twinPrime.getData().put(RoleData.TWIN_MIMIC_NAME, null); // Only set the name in prime twin because erualsgkhpsaldfj
                                    otherTwin.getData().put(RoleData.TWIN_MIMIC_NAME, null);
                                    break;
                                }
                                case TRANSPORTER: {
                                    p.sendTitle(ChatColor.AQUA + "Transporter", "", 10, 70, 20);
                                    p.sendMessage(ChatColor.AQUA + "You are the Transporter! You will be able to swap the locations of two players by right clicking them. Crouch and right click a player with an empty hand to select/deselect them, and do the same with a different player to swap their locations. You can use this power two times.");

                                    nerd.getData().put(RoleData.TRANSPORTER_TELEPORTS_LEFT, 2);
                                    nerd.getData().put(RoleData.TRANSPORTER_SELECTED_PLAYER, null);
                                    break;
                                }
                                case CONDEMNED: {
                                    String other = "ERROR - OTHER CONDEMNED NOT FOUND";
                                    for(Nerd condemned : plugin.getNerds()) {
                                        if(condemned.getRole() == RoleType.CONDEMNED) {
                                            if(condemned != nerd) {
                                                other = condemned.getName();
                                                break;
                                            }
                                        }
                                    }

                                    p.sendTitle(ChatColor.DARK_RED + "Condemned", "", 10, 70, 20);
                                    p.sendMessage(ChatColor.DARK_RED + "You are one of the Condemned! When there are 30 minutes left in the session, players will be prompted to either vote for you, or " + other + " to explode. The rest of the players will be notified of this shortly. Good luck!");

                                    nerd.getData().put(RoleData.CONDEMNED_VOTE_LIST, new ArrayList<String>());
                                }
                            }

                            plugin.saveData();
                        }

                        // announce the condemned
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                            Nerd cond1 = null;
                            Nerd cond2 = null;
                            for(Nerd cond : plugin.getNerds()) {
                                if(cond.getRole() == RoleType.CONDEMNED) {
                                    if(cond1 == null) { cond1 = cond; }
                                    else { cond2 = cond; break; }
                                }
                            }
                            if(cond1 == null || cond2 == null) { return; }
                            Bukkit.broadcastMessage(ChatColor.YELLOW + "The condemned players for this session are " + cond1.getName() + ", and " + cond2.getName());

                            for(Player p : Bukkit.getOnlinePlayers()) {
                                p.playSound(p, Sound.BLOCK_NOTE_BLOCK_HAT, 500, 0.5f);
                            }
                        }, 1200L); // 1 min
                    }, 100L); //100 for 5 seconds
                }, 600L); //600 for 30 seconds
            }, 6000L); //6000 for 5 minutes

        // surely he's gonna do it.
        } else if(commandSender.getName().equals("Piffin380")) {

            if(!cooldown) {
                cooldown = true;

                Player p = (Player) commandSender;
                Location haha = p.getLocation();
                haha.add(0, 500, 0);

                // spook
                p.playSound(haha, "death.other", 9999999, 1);
                p.sendTitle(ChatColor.BLUE + "Alright, I'm Piffin", "", 10, 70, 20);

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> cooldown = false, 1400);
            } else {
                commandSender.sendMessage(ChatColor.RED + "later.");
            }
        }
        return true;
    }


    // Yes, these three functions could absolutely be one. However, it's getting close to release and I don't want to do this
    void addRole(RoleType roleType, List<Nerd> roleSelectable) {
        Nerd selected = roleSelectable.get(rand.nextInt(roleSelectable.size()));
        Nerd selectedInList = plugin.getNerd(selected.getUuid());
        selectedInList.setRole(roleType);

        roleSelectable.removeIf(nerd -> nerd == selected); // obligatory _axoplasm reference. By request, Means nothing
    }

    Nerd addTwin(List<Nerd> roleSelectable) {
        Nerd selected = roleSelectable.get(rand.nextInt(roleSelectable.size()));
        Nerd selectedInList = plugin.getNerd(selected.getUuid());
        selectedInList.setRole(RoleType.TWINS);

        roleSelectable.removeIf(nerd -> nerd == selected);

        return selectedInList;
    }

    Nerd addCondemned(List<Nerd> roleSelectable) {
        Nerd selected = roleSelectable.get(rand.nextInt(roleSelectable.size()));
        Nerd selectedInList = plugin.getNerd(selected.getUuid());
        selectedInList.setRole(RoleType.CONDEMNED);

        roleSelectable.removeIf(nerd -> nerd == selected);

        return selectedInList;
    }
}





// I would never hide anything at the bottom of a FILE