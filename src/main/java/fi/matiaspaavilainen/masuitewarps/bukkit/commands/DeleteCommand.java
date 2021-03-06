package fi.matiaspaavilainen.masuitewarps.bukkit.commands;

import fi.matiaspaavilainen.masuitecore.bukkit.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.channels.BukkitPluginChannel;
import fi.matiaspaavilainen.masuitecore.core.configuration.BukkitConfiguration;
import fi.matiaspaavilainen.masuitewarps.bukkit.MaSuiteWarps;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteCommand implements CommandExecutor {

    private MaSuiteWarps plugin;

    public DeleteCommand(MaSuiteWarps p) {
        plugin = p;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) {
            return false;
        }

        BukkitConfiguration config = new BukkitConfiguration();
        Formator formator = new Formator();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            if (plugin.in_command.contains(cs)) {
                formator.sendMessage(cs, config.load(null, "messages.yml").getString("on-active-command"));
                return;
            }

            plugin.in_command.add(cs);

            Player p = (Player) cs;
            if (args.length == 1) {
                new BukkitPluginChannel(plugin, p, new Object[]{"DelWarp", p.getName(), args[0]}).send();
            } else {
                formator.sendMessage(cs, config.load("warps", "syntax.yml").getString("warp.delete"));
            }
            plugin.in_command.remove(cs);

        });

        return true;
    }
}
