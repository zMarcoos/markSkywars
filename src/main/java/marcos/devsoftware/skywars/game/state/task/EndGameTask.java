package marcos.devsoftware.skywars.game.state.task;

import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.Game;
import org.bukkit.scheduler.BukkitRunnable;

public class EndGameTask extends BukkitRunnable {

    private final Game game;

    public EndGameTask(Game game, SkywarsPlugin skywarsPlugin) {
        this.game = game;
    }

    @Override
    public void run() {
        game.getGameController().getBoardManager().getBoards().forEach(((player, board) -> game.getGameController().getBoardManager().updateScore(player)));
    }
}