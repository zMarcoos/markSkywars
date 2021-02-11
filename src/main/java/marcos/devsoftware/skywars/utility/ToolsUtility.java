package marcos.devsoftware.skywars.utility;

import lombok.experimental.UtilityClass;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.configuration.ConfigurationFile;
import marcos.devsoftware.skywars.configuration.ConfigurationUtility;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@UtilityClass
public class ToolsUtility {

    public String convertMilliToTime(long milliseconds) {
        long minutes = ((milliseconds / 1000) / 60);
        long seconds = ((milliseconds / 1000) % 60);

        return MessageUtility.format("&a" + minutes + ":" + (seconds < 10 ? "0" + seconds : seconds));
    }

    public ItemStack createItem(Material material, String displayName, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(MessageUtility.format(displayName));
        if (lore != null) {
            List<String> loreList = new ArrayList<>();
            Arrays.asList(lore).forEach(line -> loreList.add(MessageUtility.format(line)));

            itemMeta.setLore(loreList);
        }

        item.setItemMeta(itemMeta);

        return item;
    }

    public List<ItemStack> getItemsFromChest(String chestName, SkywarsPlugin skywarsPlugin) {
        ConfigurationFile chestFile = skywarsPlugin.getConfigurationManager().getChestsFile();
        return ConfigurationUtility.deserializeItems(chestFile, chestName);
    }

    public int randomSlot(Inventory inventory) {
        int inventorySize = inventory.getSize();
        int slot = new Random().nextInt(inventorySize - 1);

        if (inventory.getItem(slot) != null) {
            return randomSlot(inventory);
        }

        return slot;
    }

    public boolean isEmpty(Inventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null) return false;
        }

        return true;
    }

    public boolean sameLocation(Location locationOne, Location locationTwo) {
        return locationOne.getBlockX() == locationTwo.getBlockX() && locationOne.getBlockY() == locationTwo.getBlockY() && locationOne.getBlockZ() == locationTwo.getBlockZ();
    }

    public void spawnFirework(Player player) {
        Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffects(FireworkEffect
                .builder()
                .withColor(Color.BLUE, Color.GREEN, Color.YELLOW)
                .with(FireworkEffect.Type.BALL_LARGE)
                .build());
        fireworkMeta.setPower(1);

        firework.setFireworkMeta(fireworkMeta);
    }
}