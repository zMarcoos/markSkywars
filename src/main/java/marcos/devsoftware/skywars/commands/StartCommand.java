package marcos.devsoftware.skywars.commands;

import lombok.RequiredArgsConstructor;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.Game;
import marcos.devsoftware.skywars.game.state.ActiveGameState;
import marcos.devsoftware.skywars.game.state.EndGameState;
import marcos.devsoftware.skywars.utility.MessageUtility;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class StartCommand extends SkywarsModel {

    private final SkywarsPlugin skywarsPlugin;

    @Override
    public void execute(Player player, String[] args) {
        Optional<Game> gameOptional = skywarsPlugin.getGameManager().findGameByPlayer(player);
        if (!gameOptional.isPresent()) {
            MessageUtility.sendMessage(player, MessageUtility.getMessageByTitle("not_in_match", skywarsPlugin));
            return;
        }
        Game game = gameOptional.get();

        if (args[0].equalsIgnoreCase("start")) {
            game.getGameController().setState(new ActiveGameState(), skywarsPlugin);

        } else {
            game.getGameController().setState(new EndGameState(), skywarsPlugin);
        }

    }
}