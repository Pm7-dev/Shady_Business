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

public class start implements CommandExecutor {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();
    static  FileConfiguration config = plugin.getConfig();
    Random rand = new Random();

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
                        nerd.setRole(RoleType.NONE);
                        nerd.setData(new HashMap<>());

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

                    if(nerd.getRole() == RoleType.VILLAGER) { nerd.setHadRoleLastSession(false); }
                    else { nerd.setHadRoleLastSession(true); }

                    nerd.setRole(RoleType.VILLAGER);
                    nerd.setData(new HashMap<>());

                    Bukkit.getPlayer(nerd.getUuid()).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0d);

                    // Clear the investigator potion lol
                    Player p = Bukkit.getPlayer(nerd.getUuid());
                    if(p != null) {
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

                    boolean boogeyExists = false;

                    // Stupidest possible way of making either two or three boogeymen
                    if(new HashSet<>(roleSelectable).size() > 3) {
                        boogeyExists = true;
                        for (int i = 0; i < rand.nextInt(2) + 2; i++) { addRole(RoleType.BOOGEYMAN, roleSelectable); }
                    }

                    // neck romancer
                    if(config.getBoolean("someoneDeadLol")) {
                        if(!roleSelectable.isEmpty()) { addRole(RoleType.NECROMANCER, roleSelectable); }
                    }

                    // Victmdmsfk
                    if(boogeyExists) {
                        if (!roleSelectable.isEmpty()) { addRole(RoleType.VICTIM, roleSelectable); }
                    }

                    // What do you call an alligator in a vest?
                    //if(boogeyExists) {
                        if(!roleSelectable.isEmpty()) { addRole(RoleType.INVESTIGATOR, roleSelectable); }
                    //}

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
                        p.playSound(p, "role.anticipation", 1, 1);
                    }

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        for(Nerd nerd : plugin.getNerds()) {
                            Player p = Bukkit.getPlayer(nerd.getUuid());
                            if(p==null) { continue; }

                            switch (nerd.getRole()) {
                                case VILLAGER: {
                                    p.sendTitle(ChatColor.WHITE + "Villager", "", 10, 70, 20);
                                    p.sendMessage(ChatColor.WHITE + "You are a Villager! You have no special task to do today :D");
                                    p.playSound(p, "role.villager", 1, 1);
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
                                    p.playSound(p, "role.boogeyman", 1, 1);

                                    nerd.getData().put(RoleData.BOOGEYMAN_CURED, false);
                                    break;
                                }
                                case INVESTIGATOR: {
                                    p.sendTitle(ChatColor.BLUE + "Investigator", "", 10, 70, 20);
                                    p.sendMessage(ChatColor.BLUE + "You are the Investigator! Your task is to figure out who the boogeymen are. You will now receive one splash potion of revealing, which will put a red box in chat if it hits a boogeyman, and a green box in chat if it doesn't. Look for large groups to get the most people!");
                                    p.playSound(p, "role.investigator", 1, 1);

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
                                    p.sendMessage(ChatColor.GOLD + "You are the Necromancer! You are able to give one of your lives away to a ghost player with /givelife! You can also toggle chat with the dead via /rolechat");
                                    p.playSound(p, "role.necromancer", 1, 1);

                                    nerd.getData().put(RoleData.NECROMANCER_USED, false);
                                    break;
                                }
                                case VICTIM: {
                                    p.sendTitle(ChatColor.GREEN + "Victim", "", 10, 70, 20);
                                    p.sendMessage(ChatColor.GREEN + "You are the Victim! Your goal is to get killed by a boogeyman. If you are killed, you will gain a life instead of losing a life, and the boogeyman will not be cured.");
                                    p.playSound(p, "role.victim", 1, 1);

                                    nerd.getData().put(RoleData.VICTIM_COMPLETED, false);
                                    break;
                                }
                                case MIMIC: {
                                    p.sendTitle(ChatColor.LIGHT_PURPLE + "Mimic", "", 10, 70, 20);
                                    p.sendMessage(ChatColor.LIGHT_PURPLE + "You are the Mimic! You must right click to copy someone else's role before the end of the session or you will lose a life!");
                                    p.playSound(p, "role.mimic", 1, 1);
                                    break;
                                }
                                case TWINS: {
                                    p.sendTitle(ChatColor.YELLOW + "Twin", "", 10, 70, 20);
                                    p.sendMessage(ChatColor.YELLOW + "You are one of the Twins! Your health will be linked to another players' for the entire session. Good luck!");
                                    p.playSound(p, "role.twin", 1, 1);

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
                                    otherTwin.getData().put(RoleData.TWIN_SHARED_HEALTH, -3.621d); // D:

                                    twinPrime.getData().put(RoleData.TWIN_MIMIC_NAME, null); // Only set the name in prime twin because erualsgkhpsaldfj
                                    otherTwin.getData().put(RoleData.TWIN_MIMIC_NAME, null);
                                    break;
                                }
                                case TRANSPORTER: {
                                    p.sendTitle(ChatColor.AQUA + "Transporter", "", 10, 70, 20);
                                    p.sendMessage(ChatColor.AQUA + "You are the Transporter! You will be able to swap the locations of two players by right clicking them. Right click one player with an empty hand to select them, and right click a second player with an empty hand to swap their locations. Right clicking the same player you first right clicked will deselect them. You can use this power two times.");
                                    p.playSound(p, "role.transporter", 1, 1);

                                    nerd.getData().put(RoleData.TRANSPORTER_TELEPORTS_LEFT, 2);
                                    nerd.getData().put(RoleData.TRANSPORTER_SELECTED_PLAYER, null);
                                    break;
                                }
                            }

                            plugin.saveData();
                        }
                    }, 100L); //100 for 5 seconds
                }, 20L); //600 for 30 seconds
            }, 60L); //6000 for 5 minutes
        } else if(commandSender.getName().equals("Piffin380")) {

        }
        return true;
    }

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
}





// I would never hide anything at the bottom of a FILE