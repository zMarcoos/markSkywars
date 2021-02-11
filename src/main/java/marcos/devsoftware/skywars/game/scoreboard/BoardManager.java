package marcos.devsoftware.skywars.game.scoreboard;

import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import lombok.Getter;
import lombok.Setter;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.Game;
import marcos.devsoftware.skywars.utility.MessageUtility;
import marcos.devsoftware.skywars.utility.Replacer;
import marcos.devsoftware.skywars.utility.ToolsUtility;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class BoardManager {

    private final Game game;
    private final SkywarsPlugin skywarsPlugin;

    private final Map<Player, BPlayerBoard> boards;

    public BoardManager(Game game) {
        this.game = game;
        this.skywarsPlugin = game.getSkywarsPlugin();

        this.boards = new HashMap<>();
    }

    public BPlayerBoard createBoard(Player player) {
        if (boards.containsKey(player)) return boards.get(player);
        BPlayerBoard board = Netherboard.instance().createBoard(player, MessageUtility.format("&6&lSKYWARS"));

        PlayerTeam grayTeam = new PlayerTeam(board.getScoreboard(), "dead");
        grayTeam.setProperties(true, false, "&7");

        PlayerTeam greenTeam = new PlayerTeam(board.getScoreboard(), "alive");
        greenTeam.setProperties(true, true, "&a");

        PlayerTeam redTeam = new PlayerTeam(board.getScoreboard(), "enemy");
        redTeam.setProperties(true, true, "&c");

        boards.put(player, board);

        return board;
    }

    public void remove(Player player) {
        if (boards.containsKey(player)) {
            boards.get(player).delete();
            boards.remove(player);
        }
    }

    public void updateScore(Player player) {
        BPlayerBoard board = boards.get(player);
        String gameState = game.getGameState().getName();

        Replacer replacer = new Replacer();
        replacer.add("%current", game.getPlayers().size());

        if (gameState.equalsIgnoreCase("WaitingGameState")) {
            replacer.add("%map", game.getDisplayName());
            replacer.add("%max", game.getMaxPlayers());

        } else if (gameState.equalsIgnoreCase("StartingGameState")) {
            replacer.add("%map", game.getDisplayName());
            replacer.add("%max", game.getMaxPlayers());
            replacer.add("%time", game.getGameController().getTimeUntilStartMatch());

        } else if (gameState.equalsIgnoreCase("ActiveGameState") || gameState.equalsIgnoreCase("EndGameState")) {
            replacer.add("%kills", game.getGameController().getGameKillController().getPlayerKillMap().getOrDefault(player.getUniqueId(), 0));
            replacer.add("%refil", game.getGameController().getRefilTime() == 0 ? "Nenhum" : "Refil " + ToolsUtility.convertMilliToTime(game.getGameController().getRefilTime()));
        }

        YamlConfiguration configuration = skywarsPlugin.getConfigurationManager().getScoreboardFile().getConfiguration();

        List<String> lines = configuration.getStringList(gameState);
        int index = lines.size();
        for (String line : lines) {
            String newLine = replacer.replace(line);

            board.set(newLine, index--);
        }
    }
}