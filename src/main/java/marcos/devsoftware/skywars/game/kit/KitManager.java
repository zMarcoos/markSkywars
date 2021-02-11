package marcos.devsoftware.skywars.game.kit;

import lombok.Getter;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.utility.MessageUtility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KitManager {

    private final SkywarsPlugin skywarsPlugin;
    private final YamlConfiguration configuration;

    @Getter
    private final Map<UUID, Kit> playerKitMap;

    public KitManager(SkywarsPlugin skywarsPlugin) {
        this.skywarsPlugin = skywarsPlugin;

        this.playerKitMap = new HashMap<>();

        this.configuration = skywarsPlugin.getConfigurationManager().getKitsFile().getConfiguration();
    }

    public Kit getPlayerKit(Player player) {
        return playerKitMap.get(player.getUniqueId());
    }

    public boolean hasKit(Player player) {
        return playerKitMap.containsKey(player.getUniqueId());
    }

    public void setKit(Player player, String kitName) {
        Kit playerKit = getPlayerKit(player);
        if (playerKit == null) {
            Kit kit = getKitByName(kitName);
            playerKitMap.put(player.getUniqueId(), kit);

            MessageUtility.sendMessage(player, MessageUtility.getMessageByTitle("kit_selected", skywarsPlugin).replace("%kit", kit.getDisplayName()));
            return;
        }

        if (playerKit.getDisplayName().equalsIgnoreCase(kitName)) {
            MessageUtility.sendMessage(player, MessageUtility.getMessageByTitle("already_selected", skywarsPlugin));
            return;
        }

        playerKitMap.put(player.getUniqueId(), playerKit);
        MessageUtility.sendMessage(player, MessageUtility.getMessageByTitle("kit_changed", skywarsPlugin));
    }

    public void openKitsInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 27, "Kits");

        configuration.getKeys(false).forEach(kitName -> {
            Kit kit = getKitByName(kitName);
            if (kit == null) return;

            inventory.setItem(kit.getSlot(), kit.toItem());
        });

        player.openInventory(inventory);
    }

    public Kit getKitByName(String kitName) {
        ConfigurationSection section = configuration.getConfigurationSection(kitName);
        String displayName = section.getString("displayName");
        String[] lore = section.getStringList("lore").toArray(new String[0]);
        int slot = section.getInt("slot");

        return new Kit(displayName, lore, slot, skywarsPlugin);
    }
}