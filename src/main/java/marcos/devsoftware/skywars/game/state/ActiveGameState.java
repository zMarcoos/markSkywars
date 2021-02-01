package marcos.devsoftware.skywars.game.state;

import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.GameState;
import marcos.devsoftware.skywars.game.state.task.ActiveGameTask;
import marcos.devsoftware.skywars.hologram.Hologram;
import marcos.devsoftware.skywars.utility.ToolsUtility;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

public class ActiveGameState extends GameState {

    private ActiveGameTask activeGameTask;

    @Override
    public void onEnable(SkywarsPlugin skywarsPlugin) {
        super.onEnable(skywarsPlugin);

        getGame().getGameController().getPlayerCageMap().forEach((uuid, blockLocationList) -> blockLocationList.forEach(blockLocation -> blockLocation.getWorld().getBlockAt(blockLocation).setType(Material.AIR)));
        getGame().getChestPoints().forEach((location, chestType) -> {
            Chest chest = (Chest) location.getWorld().getBlockAt(location).getState();

            Hologram hologram = new Hologram(location.clone().add(0.50, 1, 0.50), "&a3:00");
            getGame().getGameController().getHolograms().add(hologram);

            List<ItemStack> items = ToolsUtility.getItemsFromChest(chestType, skywarsPlugin);
            items.forEach(item -> chest.getInventory().setItem(ToolsUtility.randomSlot(chest.getInventory()), item));
        });

        this.activeGameTask = new ActiveGameTask(getGame(), skywarsPlugin);
        this.activeGameTask.runTaskTimer(skywarsPlugin, 0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (this.activeGameTask != null) this.activeGameTask.cancel();
    }
}