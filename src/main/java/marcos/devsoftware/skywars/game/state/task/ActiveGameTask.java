package marcos.devsoftware.skywars.game.state.task;

import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.Game;
import marcos.devsoftware.skywars.hologram.Hologram;
import marcos.devsoftware.skywars.utility.ToolsUtility;
import org.bukkit.scheduler.BukkitRunnable;

public class ActiveGameTask extends BukkitRunnable {

    private final Game game;
    private final SkywarsPlugin skywarsPlugin;

    private int refilRounds;

    public ActiveGameTask(Game game, SkywarsPlugin skywarsPlugin) {
        this.game = game;
        this.skywarsPlugin = skywarsPlugin;

        this.refilRounds = 0;
    }

    @Override
    public void run() {
        if (game.getGameController().getRefilTime() == -1) {

        } else {
            if (game.getGameController().getRefilTime() == 0) {
                if (refilRounds == 1) {
                    game.getGameController().setRefilTime(180000);
                } else if (refilRounds == 2) {
                    game.getGameController().setRefilTime(-1);
                }

                refilRounds++;
            }

            for (Hologram hologram : game.getGameController().getHolograms()) {
                if (hologram == null) continue;

                hologram.modifyText(0, ToolsUtility.convertMilliToTime(game.getGameController().getRefilTime()));
            }

            game.getGameController().setRefilTime(game.getGameController().getRefilTime() - 1000);
        }
    }
}