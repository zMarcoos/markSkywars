package marcos.devsoftware.skywars.game.state.task;

import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.Game;
import marcos.devsoftware.skywars.game.state.ActiveGameState;
import marcos.devsoftware.skywars.utility.MessageUtility;
import org.bukkit.scheduler.BukkitRunnable;

public class StartingGameTask extends BukkitRunnable {

    private final Game game;
    private final SkywarsPlugin skywarsPlugin;

    public StartingGameTask(Game game, SkywarsPlugin skywarsPlugin) {
        this.game = game;
        this.skywarsPlugin = skywarsPlugin;

        game.getGameController().sendGameMessage(MessageUtility.getMessageByTitle("match_starting", skywarsPlugin).replace("%time", String.valueOf(game.getGameController().getTimeUntilStartMatch())));
    }

    @Override
    public void run() {
        if (game.getGameController().getTimeUntilStartMatch() == 0) {
            game.getGameController().setState(new ActiveGameState(), skywarsPlugin);
        }

        if (game.getGameController().getTimeUntilStartMatch() == 15 || game.getGameController().getTimeUntilStartMatch() < 6 && game.getGameController().getTimeUntilStartMatch() > 0) {
            game.getGameController().sendGameMessage("&eO jogo começa em &f" + game.getGameController().getTimeUntilStartMatch() + (game.getGameController().getTimeUntilStartMatch() == 1 ? " &esegundo." : " &esegundos."));
        }

        game.getGameController().setTimeUntilStartMatch(game.getGameController().getTimeUntilStartMatch() - 1);
    }
}