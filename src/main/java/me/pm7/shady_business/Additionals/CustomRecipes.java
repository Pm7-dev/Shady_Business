package me.pm7.shady_business.Additionals;

import me.pm7.shady_business.ShadyBusiness;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomRecipes {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();

    public static void setupCustomRecipes() {

        //TNT
        ItemStack tnt = new ItemStack(Material.TNT, 1);
        ItemMeta tntMeta = tnt.getItemMeta();
        tnt.setItemMeta(tntMeta);

        ShapedRecipe tntRecipe = new ShapedRecipe(new NamespacedKey(plugin, "tnt"), tnt);
        tntRecipe.shape(
                "PSP",
                "SGS",
                "PSP");
        tntRecipe.setIngredient('P', Material.PAPER);
        tntRecipe.setIngredient('S', Material.SAND);
        tntRecipe.setIngredient('G', Material.GUNPOWDER);


        //Nametag
        ItemStack nametag = new ItemStack(Material.NAME_TAG, 1);
        ItemMeta nametagMeta = nametag.getItemMeta();
        nametag.setItemMeta(nametagMeta);

        ShapedRecipe nametagRecipe = new ShapedRecipe(new NamespacedKey(plugin, "nametag"), nametag);
        nametagRecipe.shape(
                "   ",
                " S ",
                " P ");
        nametagRecipe.setIngredient('P', Material.PAPER);
        nametagRecipe.setIngredient('S', Material.STRING);


        //Popped Chorus Fruit
        ItemStack trimSelector = new ItemStack(Material.POPPED_CHORUS_FRUIT, 1);
        ItemMeta trimSelectorMeta = trimSelector.getItemMeta();
        trimSelectorMeta.setDisplayName(ChatColor.GREEN + "Trim Selector");
        trimSelectorMeta.setCustomModelData(2);
        trimSelector.setItemMeta(trimSelectorMeta);

        ShapedRecipe trimSelectorRecipe = new ShapedRecipe(new NamespacedKey(plugin, "popped_chorus_fruit"), trimSelector);
        trimSelectorRecipe.shape(
                " A ",
                "AIA",
                " A ");
        trimSelectorRecipe.setIngredient('A', Material.AMETHYST_SHARD);
        trimSelectorRecipe.setIngredient('I', Material.IRON_INGOT);


        //Saddle
        ItemStack saddle = new ItemStack(Material.SADDLE, 1);
        ItemMeta saddleMeta = saddle.getItemMeta();
        saddle.setItemMeta(saddleMeta);

        ShapedRecipe saddleRecipe = new ShapedRecipe(new NamespacedKey(plugin, "saddle"), saddle);
        saddleRecipe.shape(
                "LLL",
                "L L",
                "TIT");
        saddleRecipe.setIngredient('L', Material.LEATHER);
        saddleRecipe.setIngredient('I', Material.IRON_INGOT);
        saddleRecipe.setIngredient('T', Material.TRIPWIRE_HOOK);



        // add in all the recipes
        Bukkit.addRecipe(tntRecipe);
        Bukkit.addRecipe(nametagRecipe);
        Bukkit.addRecipe(trimSelectorRecipe);
        Bukkit.addRecipe(saddleRecipe);
    }
}
