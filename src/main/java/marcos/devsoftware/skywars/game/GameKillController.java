package marcos.devsoftware.skywars.game;

import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import lombok.Getter;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.utility.MessageUtility;
import marcos.devsoftware.skywars.utility.Replacer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
public class GameKillController {

    private final Game game;
    private final SkywarsPlugin skywarsPlugin;

    private final Map<UUID, Integer> playerKillMap;
    private final Map<UUID, UUID> playerKillCacheMap;
    private final Map<UUID, Integer> playerKillStreakMap;

    public GameKillController(Game game, SkywarsPlugin skywarsPlugin) {
        this.game = game;
        this.skywarsPlugin = skywarsPlugin;

        this.playerKillMap = new HashMap<>();
        this.playerKillCacheMap = new HashMap<>();
        this.playerKillStreakMap = new HashMap<>();
    }

    public void killPlayer(Player player) {
        BPlayerBoard board = game.getGameController().getBoardManager().getBoards().get(player);
        board.getScoreboard().getTeams().forEach(team -> team.removePlayer(player));

        board.getScoreboard().getTeam("dead").addPlayer(player);

        Replacer replacer = new Replacer();
        replacer.add("%player", player.getName());

        String deathMessage = replacer.replace(MessageUtility.getMessageByTitle("death_by_void", skywarsPlugin));

        Player killer = Bukkit.getPlayer(playerKillCacheMap.get(player.getUniqueId()));
        if (killer != null) {
            replacer.add("%killer", killer.getName());

            addKillCache(player, killer);
            deathMessage = replacer.replace(MessageUtility.getMessageByTitle("death_by_player", skywarsPlugin));
        }

        game.getGameController().activeSpectatorSettings(player);
        game.getGameController().sendGameMessage(deathMessage);
    }

    public void addKillCache(Player player, Player killer) {
        playerKillMap.put(killer.getUniqueId(), playerKillMap.getOrDefault(killer.getUniqueId(), 0) + 1);
        playerKillCacheMap.put(player.getUniqueId(), killer.getUniqueId());
        playerKillStreakMap.put(killer.getUniqueId(), playerKillStreakMap.getOrDefault(killer.getUniqueId(), 0) + 1);

        int killStreak = playerKillStreakMap.get(killer.getUniqueId());
        if (killStreak >= 2) {
            Replacer replacer = new Replacer();
            replacer.add("%killer", killer.getName());

            String killStreakMessage = getKillType(killStreak);
            game.getGameController().sendGameMessage(replacer.replace(killStreakMessage));
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                playerKillCacheMap.remove(player.getUniqueId());
                playerKillStreakMap.put(killer.getUniqueId(), 0);
            }
        }.runTaskLater(skywarsPlugin, 20 * 10);
    }

    public String getKillType(int amount) {
        Optional<GameKillType> killTypeString = Arrays.stream(GameKillType.values()).filter(killType -> killType.getAmount() == amount).findAny();
        return killTypeString.map(GameKillType::getName).orElse(null);
    }

    @Getter
    public enum GameKillType {
        DOUBLE_KILL("&e%killer fez um &6&lDOUBLE KILL", 2),
        TRIPLE_KILL("&e%killer fez um &b&lTRIPLE KILL", 3),
        QUADRA_KILL("&e%killer fez um &d&lQUADRA KILL", 4),
        MOONSTER_KILL("&e%killer fez um &c&lMOONSTER KILL", 5);

        private final int amount;
        private final String name;

        GameKillType(String name, int amount) {
            this.name = name;
            this.amount = amount;
        }

        public String getName() {
            if (amount > 5) {
                return "&e%killer &lESTÁ ENFURECIDO!";
            }

            return name;
        }
    }
}