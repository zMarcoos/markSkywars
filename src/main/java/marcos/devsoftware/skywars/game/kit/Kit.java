package marcos.devsoftware.skywars.game.kit;

import lombok.Getter;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.configuration.ConfigurationFile;
import marcos.devsoftware.skywars.utility.ToolsUtility;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Kit {

    private final SkywarsPlugin skywarsPlugin;

    private final String displayName;
    private final Material icon;
    private final String[] lore;
    private final int slot;
    private final List<ItemStack> items;

    public Kit(String displayName, String[] lore, int slot, SkywarsPlugin skywarsPlugin) {
        this.skywarsPlugin = skywarsPlugin;

        ConfigurationFile kitsFile = skywarsPlugin.getConfigurationManager().getKitsFile();
        YamlConfiguration configuration = kitsFile.getConfiguration();
        ConfigurationSection section = configuration.getConfigurationSection(displayName);

        this.displayName = displayName;
        this.icon = Material.getMaterial(section.getString("icon"));
        this.lore = lore;
        this.slot = slot;

        this.items = new ArrayList<>();
        section.getStringList("items").forEach(line -> {
            String[] lineArray = line.split(",");
            String materialName = lineArray[0].toUpperCase();
            int amount = Integer.parseInt(lineArray[1]);

            if (lineArray.length <= 2) {
                items.add(new ItemStack(Material.getMaterial(materialName), amount));
            } else {
                short data = Short.parseShort(lineArray[2]);
                items.add(new ItemStack(Material.getMaterial(materialName), amount, data));
            }
        });
    }

    public ItemStack toItem() {
        return ToolsUtility.createItem(icon, displayName, lore);
    }
}