package marcos.devsoftware.skywars.commands;

import lombok.RequiredArgsConstructor;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.configuration.ConfigurationFile;
import marcos.devsoftware.skywars.configuration.ConfigurationUtility;
import marcos.devsoftware.skywars.utility.MessageUtility;
import marcos.devsoftware.skywars.utility.ToolsUtility;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class ChestCommand extends SkywarsModel {

    private final SkywarsPlugin skywarsPlugin;

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2 || !args[0].equalsIgnoreCase("create")) {
            MessageUtility.sendMessage(player, "&cDigite /skywars chest create <name>");
            return;
        }

        if (ToolsUtility.isEmpty(player.getInventory())) {
            MessageUtility.sendMessage(player, MessageUtility.getMessageByTitle("empty_inventory", skywarsPlugin));
            return;
        }

        ConfigurationFile chestFile = skywarsPlugin.getConfigurationManager().getChestsFile();

        List<ItemStack> itemStackList = new ArrayList<>();
        Arrays.stream(player.getInventory().getContents()).filter(Objects::nonNull).forEach(itemStackList::add);

        ConfigurationUtility.serializeItems(chestFile, args[1], itemStackList);

        MessageUtility.sendMessage(player, MessageUtility.getMessageByTitle("chest_sucess", skywarsPlugin));
    }
}