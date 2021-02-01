package marcos.devsoftware.skywars.commands;

import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.utility.MessageUtility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SkywarsCommand implements CommandExecutor {

    private final SkywarsPlugin skywarsPlugin;

    private final JoinCommand joinCommand;
    private final ListCommand listCommand;
    private final SetupCommand setupCommand;
    private final StartCommand startCommand;

    public SkywarsCommand(SkywarsPlugin skywarsPlugin) {
        this.skywarsPlugin = skywarsPlugin;

        this.joinCommand = new JoinCommand(skywarsPlugin);
        this.listCommand = new ListCommand(skywarsPlugin);
        this.setupCommand = new SetupCommand(skywarsPlugin);
        this.startCommand = new StartCommand(skywarsPlugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (!player.hasPermission("markskywars.perm")) {
            MessageUtility.sendMessage(player, MessageUtility.format("&3&lMarkSkywars &f- &6&lzMarcoos"));
            return true;
        }

        if (args.length == 0) {
            MessageUtility.sendMessage(player, MessageUtility.getMessageByTitle("type_anything", skywarsPlugin));
            return true;
        }

        String argument = args[0];
        List<String> newArgs = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            if (i == 0) continue;

            newArgs.add(args[i]);
        }

        if (argument.equalsIgnoreCase("join")) {
            this.joinCommand.execute(player, newArgs.toArray(new String[0]));
        } else if (argument.equalsIgnoreCase("list")) {
            this.listCommand.execute(player, newArgs.toArray(new String[0]));
        } else if (argument.equalsIgnoreCase("setup")) {
            this.setupCommand.execute(player, newArgs.toArray(new String[0]));
        } else if (argument.equalsIgnoreCase("start")) {
            this.startCommand.execute(player, newArgs.toArray(new String[0]));
        } else {
            MessageUtility.sendMessage(player, MessageUtility.getMessageByTitle("no_have_option", skywarsPlugin));
        }

        return true;
    }
}