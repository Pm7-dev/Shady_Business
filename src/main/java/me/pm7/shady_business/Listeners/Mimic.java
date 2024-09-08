package me.pm7.shady_business.Listeners;

import me.pm7.shady_business.Objects.Nerd;
import me.pm7.shady_business.Objects.RoleData;
import me.pm7.shady_business.Objects.RoleType;
import me.pm7.shady_business.ShadyBusiness;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Mimic implements Listener {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();
    boolean used = false;

    @EventHandler
    public void onRightClick(PlayerInteractAtEntityEvent e) {

        // I swear the amount of garbage return statements before anything cool can start grows more and more with each season
        if(!e.getHand().equals(EquipmentSlot.HAND)) { return; }
        if(!(e.getRightClicked() instanceof Player pClicked)) { return; }
        Player p = e.getPlayer();
        if(!p.isSneaking()) { return; }
        Nerd nerd = plugin.getNerd(p.getName());
        if(nerd == null) { return; }
        if(nerd.getRole() != RoleType.MIMIC) { return; }
        if(nerd.getLives() <= 1) { return; }
        Nerd clicked = plugin.getNerd(pClicked.getName());
        if(clicked == null) { return; }
        if(used) { return; }

        if(clicked.getLives() <= 1) {
            p.sendMessage(ChatColor.RED + "That player is on red lol");
            return;
        }

        // play a funny sound
        p.playSound(p, Sound.ENTITY_BREEZE_CHARGE, 500, 0.75f);

        used = true;

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

            used = false;
            if (!p.isOnline()) { return; }

            p.playSound(p, Sound.ENTITY_BREEZE_SHOOT, 500, 1.0f);
            p.playSound(p, Sound.ENTITY_BREEZE_SHOOT, 500, 2.0f);

            switch (clicked.getRole()) {
                case MIMIC: {
                    //uhhhhhhhhhhh... Don't think this is meant to happen

                    System.out.println("Ha!"); // Ha!
                    break;
                }
                case VILLAGER: {
                    p.sendMessage(ChatColor.WHITE + "You are now a Villager! Continue about the rest of your session as if nothing out of the ordinary happened."); // wildfire??

                    nerd.setRole(RoleType.VILLAGER);
                    break;
                }
                case BOOGEYMAN: {
                    // This is still, in fact, a waste of processing power.
                    String boogies;
                    List<String> boogeymen = new ArrayList<>();
                    for(Nerd boogey : plugin.getNerds()) {
                        if(boogey.getRole() == RoleType.BOOGEYMAN && boogey.getUuid() != nerd.getUuid()) {
                            boogeymen.add(boogey.getName());
                            Player boog = Bukkit.getPlayer(boogey.getUuid());
                            if(boog==null) { continue; }
                            boog.sendMessage(ChatColor.RED + p.getName() + " has mimicked a boogeyman.");
                            boog.sendMessage(ChatColor.RED + "You should probably go talk to them");
                        }
                    }
                    if(boogeymen.size() == 3) { boogies = "new friends " + boogeymen.getFirst() + ", " + boogeymen.get(1) + ", and " + boogeymen.getLast(); }
                    else { boogies = "new friends " + boogeymen.getFirst() + ", and " + boogeymen.getLast(); }

                    p.sendMessage(ChatColor.RED + "You are now a Boogeyman! Quickly go meet up with your " + boogies + ". They have now been alerted that you have joined them.");

                    nerd.getData().put(RoleData.BOOGEYMAN_CURED, false);

                    nerd.setRole(RoleType.BOOGEYMAN);
                    break;
                }
                case INVESTIGATOR: {
                    p.sendMessage(ChatColor.BLUE + "You are now an Investigator! You will now receive one Orb of Pondering, which will put a red box in chat if a player it hits is a boogeyman, and a green box in chat if a player it hits is not. Look for large groups to get the most people!");

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

                    nerd.setRole(RoleType.INVESTIGATOR);
                    break;
                }
                case VICTIM: {
                    p.sendMessage(ChatColor.GREEN + "You are now a Victim! Your goal is now to be killed by a boogeyman. If you are killed, you will gain a life instead of losing a life, and the boogeyman will not be cured.");

                    nerd.setRole(RoleType.VICTIM);
                    break;
                }
                case NECROMANCER: {
                    p.sendMessage(ChatColor.GOLD + "You are now a Necromancer! You are able to give one of your lives away to a ghost player with /givelife! You can also toggle chat with the dead via /rolechat");

                    nerd.getData().put(RoleData.NECROMANCER_USED, false);

                    nerd.setRole(RoleType.NECROMANCER);
                    break;
                }
                case TRANSPORTER: {
                    p.sendMessage(ChatColor.AQUA + "You are now a Transporter! You will be able to swap the locations of two players by right clicking them. Right click one player with an empty hand to select them, and right click a second player with an empty hand to swap their locations. Right clicking the same player you first right clicked will deselect them. You can use this power two times.");

                    nerd.getData().put(RoleData.TRANSPORTER_TELEPORTS_LEFT, 2);
                    nerd.getData().put(RoleData.TRANSPORTER_SELECTED_PLAYER, null);

                    nerd.setRole(RoleType.TRANSPORTER);
                    break;
                }
                case TWINS: { // I hate this so much
                    p.sendMessage(ChatColor.YELLOW + "You are now one of the Twins! er... Triplets? Your health will be linked to both of the current twins for the rest of the session. Good Luck!");

                    //Collect the prime and secondary twins
                    Nerd prime = null;
                    Nerd secondary = null;
                    for (Nerd n : plugin.getNerds()) {
                        if(n.getRole() == RoleType.TWINS) {
                            if((Boolean) n.getData().get(RoleData.TWIN_IS_PRIME)) { prime = n; }
                            else { secondary = n; }
                        }
                    }
                    if(prime == null || secondary == null) {
                        System.out.println("BIIIIG ERROR WITH MIMICIN G QITwNS HUSTL HAPPEND!!!! ! 1"); //blasphemous
                        return;
                    }

                    prime.getData().put(RoleData.TWIN_MIMIC_NAME, nerd.getName());
                    secondary.getData().put(RoleData.TWIN_MIMIC_NAME, nerd.getName()); // probably won't be used but good to have??

                    nerd.getData().put(RoleData.TWIN_IS_PRIME, false);
                    nerd.getData().put(RoleData.TWIN_PARTNER_NAME, prime.getName());
                    nerd.getData().put(RoleData.TWIN_MIMIC_NAME, nerd.getName());
                    nerd.getData().put(RoleData.TWIN_SHARED_HEALTH, -3.926); // :D

                    p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30.0d);
                    p.setHealth((Double) prime.getData().get(RoleData.TWIN_SHARED_HEALTH));

                    nerd.setRole(RoleType.TWINS);
                    break;
                }

                // nope creep
                case CONDEMNED: {
                    p.sendMessage(ChatColor.RED + "That was a condemned player. I am not going to code this.");
                }
            }
        }, 18L);
    }
}
