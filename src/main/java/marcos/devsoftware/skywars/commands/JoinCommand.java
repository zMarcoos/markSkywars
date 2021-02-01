package marcos.devsoftware.skywars.commands;

import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.Game;
import marcos.devsoftware.skywars.utility.MessageUtility;
import org.bukkit.entity.Player;

import java.util.Optional;

public class JoinCommand extends SkywarsModel {

    private final SkywarsPlugin skywarsPlugin;

    public JoinCommand(SkywarsPlugin skywarsPlugin) {
        this.skywarsPlugin = skywarsPlugin;
    }

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