package marcos.devsoftware.skywars.commands;

import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.Game;
import marcos.devsoftware.skywars.utility.MessageUtility;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.List;

public class ListCommand extends SkywarsModel {

    private final SkywarsPlugin skywarsPlugin;

    public ListCommand(SkywarsPlugin skywarsPlugin) {
        this.skywarsPlugin = skywarsPlugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        List<Game> games = skywarsPlugin.getGameManager().getGames();
        if (games.size() == 0) {
            MessageUtility.sendMessage(player, MessageUtility.getMessageByTitle("no_loaded_game", skywarsPlugin));
            return;
        }

        games.forEach(game -> MessageUtility.sendMessage(player, "&e" + game.getDisplayName() + " - " + game.getGameState().getName() + " - " + game.getPlayers().size() + "/" + game.getMaxPlayers()));
    }
}