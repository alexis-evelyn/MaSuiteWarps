package dev.masa.masuitewarps.bukkit.commands;

import dev.masa.masuitecore.acf.BaseCommand;
import dev.masa.masuitecore.acf.annotation.*;
import dev.masa.masuitecore.acf.bukkit.contexts.OnlinePlayer;
import dev.masa.masuitecore.core.adapters.BukkitAdapter;
import dev.masa.masuitecore.core.channels.BukkitPluginChannel;
import dev.masa.masuitewarps.bukkit.MaSuiteWarps;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand extends BaseCommand {

    private MaSuiteWarps plugin;

    public WarpCommand(MaSuiteWarps plugin) {
        this.plugin = plugin;
    }

    @CommandAlias("warp")
    @CommandPermission("masuitewarps.warp")
    @Description("Warps to target")
    @CommandCompletion("@warps @masuite_players")
    @Conditions("cooldown:type=warps,bypass=masuitewarps.cooldown.override")
    public void teleportWarpCommand(CommandSender sender, @Single String name, @Optional @Single @CommandPermission("masuitewarps.warp.other") OnlinePlayer onlinePlayer) {
        if (!(sender instanceof Player) || onlinePlayer != null) {
            new BukkitPluginChannel(plugin, onlinePlayer.player, "Warp", onlinePlayer.player.getName(), name, true, true, true).send();
            return;
        }
        Player player = (Player) sender;

        // Check if player has permission to teleport to warp if per server warps is enabled
        if (plugin.perServerWarps) {
            if (!player.hasPermission("masuitewarps.warp.to." + name) && !player.hasPermission("masuitewarps.warp.to.*")) {
                plugin.formator.sendMessage(player, plugin.noPermission);
                return;
            }
        }

        new BukkitPluginChannel(plugin, player, "Warp", player.getName(), name.toLowerCase(),
                player.hasPermission("masuitewarps.warp.global"),
                player.hasPermission("masuitewarps.warp.server"),
                player.hasPermission("masuitewarps.warp.hidden")).send();
    }

    @CommandAlias("setwarp")
    @CommandPermission("masuitewarps.warp.set")
    @Description("Creates a new warp or updates an existing warp")
    @CommandCompletion("@warps hidden|global")
    public void setWarpCommand(Player player, @Single String name, @Optional @Single String setting) {
        Location loc = player.getLocation();
        String stringLocation = BukkitAdapter.adapt(loc).serialize();
        if (setting == null) {
            new BukkitPluginChannel(plugin, player, "SetWarp", 2, player.getName(), name, stringLocation).send();
            return;
        }

        if (setting.equalsIgnoreCase("hidden") || setting.equalsIgnoreCase("global")) {
            if (setting.equalsIgnoreCase("hidden") && !player.hasPermission("masuitewarps.warp.set.hidden")) {
                plugin.formator.sendMessage(player, plugin.noPermission);
                return;
            }
            if (setting.equalsIgnoreCase("global") && !player.hasPermission("masuitewarps.warp.set.global")) {
                plugin.formator.sendMessage(player, plugin.noPermission);
                return;
            }
            if (!setting.equalsIgnoreCase("global") && !setting.equalsIgnoreCase("hidden") && !player.hasPermission("masuitewarps.warp.set.server")) {
                plugin.formator.sendMessage(player, plugin.noPermission);
                return;
            }
            new BukkitPluginChannel(plugin, player, "SetWarp", 3, player.getName(), name, stringLocation, setting).send();
        }
    }

    @CommandAlias("delwarp|warpdel|deletewarp")
    @CommandPermission("masuitewarps.warp.delete")
    @Description("Deletes a warp")
    @CommandCompletion("@warps")
    public void delWarpCommand(Player player, @Single String name) {
        new BukkitPluginChannel(plugin, player, "DelWarp", player.getName(), name).send();
    }

    @CommandAlias("warps|listwarps|warplist")
    @CommandPermission("masuitewarps.warp.list")
    @Description("Lists all of the warps")
    @CommandCompletion("@warps")
    public void listWarpCommand(Player player) {
        new BukkitPluginChannel(plugin, player, "ListWarps", player.getName(), player.hasPermission("masuitewarps.list.global"), player.hasPermission("masuitewarps.list.server"), player.hasPermission("masuitewarps.list.hidden")).send();
    }

}