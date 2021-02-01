package marcos.devsoftware.skywars.game.state;

import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.GameState;
import marcos.devsoftware.skywars.game.state.task.ActiveGameTask;
import marcos.devsoftware.skywars.utility.MessageUtility;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class ActiveGameState extends GameState {

    private ActiveGameTask activeGameTask;

    @Override
    public void onEnable(SkywarsPlugin skywarsPlugin) {
        super.onEnable(skywarsPlugin);

        this.activeGameTask = new ActiveGameTask(getGame(), skywarsPlugin);
        this.activeGameTask.runTaskTimer(skywarsPlugin, 0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (this.activeGameTask != null) this.activeGameTask.cancel();
    }

    @EventHandler
    private void onBreakBlock(BlockBreakEvent event) {
        if (!getGame().getGameController().inGame(event.getPlayer())) return;

        if (event.getBlock().getType() == Material.CHEST) {
            MessageUtility.sendMessage(event.getPlayer(), MessageUtility.getMessageByTitle("cant_break", getGame().getSkywarsPlugin()));
            event.setCancelled(true);
        }
    }
}