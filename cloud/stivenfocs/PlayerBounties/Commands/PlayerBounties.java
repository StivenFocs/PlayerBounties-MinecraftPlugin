package cloud.stivenfocs.PlayerBounties.Commands;

import cloud.stivenfocs.PlayerBounties.Loader;
import cloud.stivenfocs.PlayerBounties.Vars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import cloud.stivenfocs.stivenUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerBounties implements CommandExecutor, TabCompleter {

    stivenUtils util = new stivenUtils();
    public Loader plugin;

    public PlayerBounties(Loader plugin) {
        this.plugin = plugin;
    }

    /////////////////////////////

    /*public static Inventory playersList() {
        try {
            Inventory plist = Bukkit.createInventory(null, 54, "Choose a player");
            for (Player player : Bukkit.getOnlinePlayers()) {
                ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1, (short) SkullType.PLAYER.ordinal());
                SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
                meta.setOwner(player.getName());
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', player.getName()));
                skull.setItemMeta(meta);

                plist.addItem(skull);
            }

            return plist;
        } catch (Exception ex) {
            System.out.println("[PlayerBounties] An Exception occurred while trying to generate the player list selector.");
            ex.printStackTrace();
            return null;
        }
    }*/

    /////////////////////////////

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (args.length == 0) {
            if (sender.hasPermission("playerbounties.bounty") || Vars.isPermittedAdmin(sender)) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;

                    Vars.getSelectorHandler().refreshPlayerSelector();
                    p.openInventory(Vars.getSelectorHandler().getPlayerSelector());
                } else {
                    plugin.sendString(Vars.players_only, sender);
                }
            } else {
                plugin.sendString(Vars.insufficient_permissions, sender);
            }
        } else {
            if (args[0].equalsIgnoreCase("help")) {
                if (Vars.isPermittedAdmin(sender)) {
                    sender.sendMessage(stivenUtils.colorlist(Vars.help_admin).toArray(new String[0]));
                } else {
                    sender.sendMessage(stivenUtils.colorlist(Vars.help_user).toArray(new String[0]));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                if (Vars.isPermittedAdmin(sender)) {
                    if (Vars.reloadVars()) {
                        HashMap<UUID, Inventory> oInv_clone = (HashMap<UUID, Inventory>) Vars.opened_inventories.clone();
                        for(UUID puid : oInv_clone.keySet()) {
                            if (Bukkit.getPlayer(puid) != null) {
                                Bukkit.getPlayer(puid).closeInventory();
                            }
                            Vars.opened_inventories.remove(puid);
                        }

                        plugin.sendString(Vars.configuration_reloaded, sender);
                    } else {
                        plugin.sendString(Vars.an_error_ocurred, sender);
                    }
                } else {
                    plugin.sendString(Vars.insufficient_permissions, sender);
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("stats")) {
                if (args.length <= 1) {
                    if (sender instanceof Player) {
                        if (sender.hasPermission("playerbounties.stats")) {
                            sender.sendMessage("Work in progress");
                        } else {
                            plugin.sendString(Vars.insufficient_permissions, sender);
                        }
                    } else {
                        plugin.sendString(Vars.incomplete_command, sender);
                    }
                } else {
                    if (sender.hasPermission("playerbounties.stats.others")) {
                        if (Bukkit.getPlayerExact(args[1]) != null) {
                            sender.sendMessage("Work in progress");
                        } else {
                            plugin.sendString(Vars.unknow_player, sender);
                        }
                    } else {
                        plugin.sendString(Vars.insufficient_permissions, sender);
                    }
                }
                return true;
            }
            plugin.sendString(Vars.unknow_subcommand, sender);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            if (Vars.isPermittedAdmin(sender)) {
                suggestions.add("add");
                suggestions.add("reload");
            } else if (sender.hasPermission("playerbounties.bounty")) {
                suggestions.add("stats");
            }
        }
        return suggestions;
    }
}
