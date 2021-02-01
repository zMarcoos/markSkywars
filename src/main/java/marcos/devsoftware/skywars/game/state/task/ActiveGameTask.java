package marcos.devsoftware.skywars.game.state.task;

import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.Game;
import marcos.devsoftware.skywars.hologram.Hologram;
import marcos.devsoftware.skywars.utility.ToolsUtility;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ActiveGameTask extends BukkitRunnable {

    private final Game game;
    private final SkywarsPlugin skywarsPlugin;

    private int refilRounds;

    public ActiveGameTask(Game game, SkywarsPlugin skywarsPlugin) {
        this.game = game;
        this.skywarsPlugin = skywarsPlugin;

        this.refilRounds = 0;

        game.getGameController().getPlayerCageMap().forEach((uuid, blockLocationList) -> blockLocationList.forEach(blockLocation -> blockLocation.getWorld().getBlockAt(blockLocation).setType(Material.AIR)));
        game.getChestPoints().forEach((location, chestType) -> {
            Chest chest = (Chest) location.getWorld().getBlockAt(location).getState();

            Hologram hologram = new Hologram(location.clone().add(0.50, 1, 0.50), "&a3:00");
            game.getGameController().getHolograms().add(hologram);

            List<ItemStack> items = ToolsUtility.getItemsFromChest(chestType, skywarsPlugin);
            items.forEach(item -> chest.getInventory().setItem(ToolsUtility.randomSlot(chest.getInventory()), item));
        });
    }

    @Override
    public void run() {
        for (Hologram hologram : game.getGameController().getHolograms()) {
            if (hologram == null) continue;

            Location chestLocation = hologram.getLocation().clone().subtract(0.50, 1, 0.50);
            Chest chest = (Chest) chestLocation.getWorld().getBlockAt(chestLocation).getState();
            if (ToolsUtility.isEmpty(chest.getInventory())) {
                hologram.modifyText(0, "&cVazio!");
            } else {
                hologram.modifyText(0, ToolsUtility.convertMilliToTime(game.getGameController().getRefilTime()));
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

                Hologram hologram = new Hologram(location.clone().add(0.50, 1, 0.50), "&a3:00");
                game.getGameController().getHolograms().add(hologram);

                List<ItemStack> items = ToolsUtility.getItemsFromChest(chestType.concat("_refil"), skywarsPlugin);
                items.forEach(item -> chest.getInventory().setItem(ToolsUtility.randomSlot(chest.getInventory()), item));
            });

            this.refilRounds = this.refilRounds + 1;
        }

        game.getGameController().setRefilTime(game.getGameController().getRefilTime() - 1000);
    }
}