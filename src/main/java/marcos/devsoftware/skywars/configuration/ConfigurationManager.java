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

    public ConfigurationManager(SkywarsPlugin skywarsPlugin) {
        this.cagesFolder = ConfigurationUtility.createDirectory("cages", skywarsPlugin);
        this.gamesFolder = ConfigurationUtility.createDirectory("games", skywarsPlugin);
        this.chestsFile = ConfigurationUtility.createFile("chests.yml", skywarsPlugin);
        this.configurationFile = ConfigurationUtility.createFile("configuration.yml", skywarsPlugin);
        this.kitsFile = ConfigurationUtility.createFile("kits.yml", skywarsPlugin);
        this.messagesFile = ConfigurationUtility.createFile("messages.yml", skywarsPlugin);
    }
}