package marcos.devsoftware.skywars.commands;

import lombok.RequiredArgsConstructor;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.Game;
import marcos.devsoftware.skywars.utility.MessageUtility;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class JoinCommand extends SkywarsModel {

    private final SkywarsPlugin skywarsPlugin;

    @Override
    public void execute(Player player, String[] args) {
        Optional<Game> gameOptional = skywarsPlugin.getGameManager().findOpenGame();
        if (!gameOptional.isPresent()) {
            MessageUtility.sendMessage(player, MessageUtility.getMessageByTitle("no_game_available", skywarsPlugin));
            return;
        }

        Game game = gameOptional.get();
        game.getGameController().joinMatch(player);
    }
}