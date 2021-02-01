package marcos.devsoftware.skywars.utility;

import lombok.experimental.UtilityClass;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.configuration.ConfigurationFile;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

@UtilityClass
public class MessageUtility {

    public String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(format(message));
    }

    public void sendTitle(Player player, String title, String subTitle, int fadeIn, int displayTime, int fadeOut) {
        EntityPlayer craftPlayer = ((CraftPlayer) player).getHandle();
        PlayerConnection playerConnection = craftPlayer.playerConnection;

        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":" + title + "}"), fadeIn, displayTime, fadeOut);
        playerConnection.sendPacket(titlePacket);

        if (subTitle != null) {
            PacketPlayOutTitle subTitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":" + subTitle + "}"), fadeIn, displayTime, fadeOut);
            playerConnection.sendPacket(subTitlePacket);
        }
    }

    public String getMessageByTitle(String title, SkywarsPlugin skywarsPlugin) {
        ConfigurationFile messagesFile = new ConfigurationFile("messages.yml", skywarsPlugin.getDataFolder());
        return format(messagesFile.getConfiguration().getString(title));
    }

    public List<String> getListByTitle(String title, SkywarsPlugin skywarsPlugin) {
        ConfigurationFile messagesFile = new ConfigurationFile("messages.yml", skywarsPlugin.getDataFolder());
        return messagesFile.getConfiguration().getStringList(title);
    }
}