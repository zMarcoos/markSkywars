package marcos.devsoftware.skywars.commands;

import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.Game;
import marcos.devsoftware.skywars.game.state.ActiveGameState;
import marcos.devsoftware.skywars.utility.MessageUtility;
import org.bukkit.entity.Player;

import java.util.Optional;

public class StartCommand extends SkywarsModel {

    private final SkywarsPlugin skywarsPlugin;

    public StartCommand(SkywarsPlugin skywarsPlugin) {
        this.skywarsPlugin = skywarsPlugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        Optional<Game> gameOptional = skywarsPlugin.getGameManager().findGameByPlayer(player);
        if (!gameOptional.isPresent()) {
            MessageUtility.sendMessage(player, MessageUtility.getMessageByTitle("not_in_match", skywarsPlugin));
            return;
        }

        Game game = gameOptional.get();
        game.getGameController().setState(new ActiveGameState(), skywarsPlugin);
    }
}