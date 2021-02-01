package marcos.devsoftware.skywars.utility;

import lombok.experimental.UtilityClass;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.configuration.ConfigurationFile;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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

    public ItemStack createItem(Material material, String displayName, String[] lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(MessageUtility.format(displayName));
        List<String> loreList = Arrays.asList(lore);
        loreList.forEach(MessageUtility::format);

        itemMeta.setLore(loreList);
        item.setItemMeta(itemMeta);

        return item;
    }

    public List<ItemStack> getItemsFromChest(String chestName, SkywarsPlugin skywarsPlugin) {
        ConfigurationFile chestFile = skywarsPlugin.getConfigurationManager().getChestsFile();
        YamlConfiguration configuration = chestFile.getConfiguration();
        if (configuration.getKeys(false).isEmpty()) return null;

        List<ItemStack> itemsList = new ArrayList<>();
        configuration.getStringList(chestName).forEach(itemValues -> {
            String[] itemInformationArray = itemValues.split(";");
            itemsList.add(new ItemStack(Material.getMaterial(itemInformationArray[0]), Integer.parseInt(itemInformationArray[1]), Short.parseShort(itemInformationArray[2])));
        });

        return itemsList;
    }

    public int randomSlot(Inventory inventory) {
        int inventorySize = inventory.getSize();
        int slot = new Random().nextInt(inventorySize - 1);

        if (inventory.getItem(slot) != null) {
            return randomSlot(inventory);
        }

        return slot;
    }
}