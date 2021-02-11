package marcos.devsoftware.skywars.configuration;

import lombok.Getter;
import marcos.devsoftware.skywars.SkywarsPlugin;

import java.io.File;

@Getter
public class ConfigurationManager {

    private final File cagesFolder;
    private final File gamesFolder;
    private final ConfigurationFile chestsFile;
    private final ConfigurationFile configurationFile;
    private final ConfigurationFile kitsFile;
    private final ConfigurationFile messagesFile;
    private final ConfigurationFile scoreboardFile;

    public ConfigurationManager(SkywarsPlugin skywarsPlugin) {
        this.cagesFolder = ConfigurationUtility.createDirectory("cages", skywarsPlugin.getDataFolder());
        this.gamesFolder = ConfigurationUtility.createDirectory("games", skywarsPlugin.getDataFolder());
        this.chestsFile = ConfigurationUtility.createFile("chests.yml", skywarsPlugin);
        this.configurationFile = ConfigurationUtility.createFile("configuration.yml", skywarsPlugin);
        this.kitsFile = ConfigurationUtility.createFile("kits.yml", skywarsPlugin);
        this.messagesFile = ConfigurationUtility.createFile("messages.yml", skywarsPlugin);
        this.scoreboardFile = ConfigurationUtility.createFile("scoreboard.yml", skywarsPlugin);
    }
}