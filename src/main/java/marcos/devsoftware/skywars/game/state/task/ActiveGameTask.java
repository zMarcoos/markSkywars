package marcos.devsoftware.skywars.game.state.task;

import lombok.Getter;
import lombok.Setter;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.Game;
import marcos.devsoftware.skywars.hologram.Hologram;
import marcos.devsoftware.skywars.utility.ToolsUtility;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;

public class ActiveGameTask extends BukkitRunnable {

    private final Game game;
    private final SkywarsPlugin skywarsPlugin;

    private int refilRounds;
    @Getter @Setter
    private int timeUntilDamage;

    public ActiveGameTask(Game game, SkywarsPlugin skywarsPlugin) {
        this.game = game;
        this.skywarsPlugin = skywarsPlugin;

        this.refilRounds = 0;
        this.timeUntilDamage = 10;

        game.getGameController().getPlayerCageMap().forEach((uuid, blockLocationList) -> blockLocationList.forEach(blockLocation -> blockLocation.getWorld().getBlockAt(blockLocation).setType(Material.AIR)));
        game.getChestPoints().forEach((location, chestType) -> {
            Chest chest = (Chest) location.getWorld().getBlockAt(location).getState();

            Hologram hologram = new Hologram(location.clone().add(0.50, 1, 0.50), "&a3:00");
            game.getGameController().getChestHologramMap().put(chest, hologram);

            List<ItemStack> items = ToolsUtility.getItemsFromChest(chestType, skywarsPlugin);
            items.forEach(item -> chest.getInventory().setItem(ToolsUtility.randomSlot(chest.getInventory()), item));
        });
    }

    @Override
    public void run() {
        for (Map.Entry<Chest, Hologram> singleChestMap : game.getGameController().getChestHologramMap().entrySet()) {
            if (singleChestMap.getKey() == null) continue;

            Chest chest = singleChestMap.getKey();
            Hologram hologram = singleChestMap.getValue();

            hologram.modifyText(0, ToolsUtility.convertMilliToTime(game.getGameController().getRefilTime()));
            if (ToolsUtility.isEmpty(chest.getInventory())) {
                if (hologram.getHologramLines().size() == 1) {
                    hologram.addLine("&cVazio!");
                } else {
                    hologram.modifyText(1, "&cVazio!");
                }
            } else {
                if (hologram.getHologramLines().size() == 2) {
                    hologram.removeLine(1);
                }
            }
        }

        if (game.getGameController().getRefilTime() == 0) {
            if (refilRounds == 0 || refilRounds == 1) {
                game.getGameController().setRefilTime(180000);
            } else if (refilRounds == 2) {
                game.getGameController().setRefilTime(0);
            } else return;

            game.getChestPoints().forEach((location, chestType) -> {
                Chest chest = (Chest) location.getWorld().getBlockAt(location).getState();
                chest.getInventory().clear();

                List<ItemStack> items = ToolsUtility.getItemsFromChest(chestType.concat("_refil"), skywarsPlugin);
                items.forEach(item -> chest.getInventory().setItem(ToolsUtility.randomSlot(chest.getInventory()), item));
            });

            this.refilRounds = this.refilRounds + 1;
        }

        game.getGameController().setRefilTime(game.getGameController().getRefilTime() - 1000);

        if (getTimeUntilDamage() > 0) {
            setTimeUntilDamage(getTimeUntilDamage() - 1);
        }
    }
}