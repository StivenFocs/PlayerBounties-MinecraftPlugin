package cloud.stivenfocs.PlayerBounties;

import cloud.stivenfocs.PlayerBounties.Bounty.Bounty;
import cloud.stivenfocs.PlayerBounties.Commands.PlayerBounties;
import cloud.stivenfocs.PlayerBounties.Events.PrimaryKillEvent;
import cloud.stivenfocs.PlayerBounties.Events.SelectorHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class Loader extends JavaPlugin {

    public void onEnable() {
        setupEconomy();
        Vars.plugin = this;
        Vars.reloadVars();

        getCommand("playerbounties").setExecutor(new PlayerBounties(this));
        getCommand("playerbounties").setTabCompleter(new PlayerBounties(this));

        Bukkit.getPluginManager().registerEvents(new PrimaryKillEvent(this), this);
        Bukkit.getPluginManager().registerEvents(Vars.getSelectorHandler(), this);

        for(String target_uuid : Vars.vars.dataConfig.getKeys(false)) {
            Boolean working = true;

            UUID player = UUID.fromString(target_uuid);
            UUID creator = null;
            int duration = 500;
            int value = Vars.killstreak_bounty_value;

            if (Vars.vars.dataConfig.getString(target_uuid + ".creator") == null) {
                working = false;
            } else {
                try {
                    creator = UUID.fromString(Vars.vars.dataConfig.getString(target_uuid + ".creator"));
                } catch (Exception ex) {
                    creator = null;
                }
            }
            if (Vars.vars.dataConfig.get(target_uuid + ".duration") == null) {
                working = false;
            } else {
                duration = Vars.vars.dataConfig.getInt(target_uuid + ".duration");
            }
            if (Vars.vars.dataConfig.get(target_uuid + ".value") == null) {
                working = false;
            } else {
                value = Vars.vars.dataConfig.getInt(target_uuid + ".value");
            }

            if (working) {
                try {
                    Vars.getBountyHandler().startTask(player, creator, duration, value, false);
                } catch (Exception ex) {
                    getLogger().severe("An error occurred while loading all available bounties..");
                    ex.printStackTrace();
                }
            } else {
                getLogger().warning("the bounty on '" + target_uuid + "' is not valid, deleting it.");
                Vars.vars.dataConfig.set(target_uuid, null);

                Vars.saveDataConfiguration();
                Vars.reloadDataConfiguration();
            }
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PAPIExpansion(this).register();
        }
    }

    public void onDisable() {
        for(Bounty bounty : Vars.getBountyHandler().bounties) {
            bounty.end();
            Bukkit.getScheduler().cancelTask(bounty.getTimerTaskId());
            Vars.getVars().dataConfig.set(bounty.getPlayer() + ".duration", bounty.getRemainingSeconds());
        }

        Vars.saveDataConfiguration();
        Vars.reloadDataConfiguration();

        SelectorHandler.closeAll(Vars.getSelectorHandler().getPlayerSelector());
        SelectorHandler.closeAll(Vars.getSelectorHandler().getTemplateSelector());
    }

    public void sendString(String text, CommandSender sender) {
        try {
            if (getConfig().getString(text) != null) {
                if (getConfig().getString(text).length() > 0) {
                    text = getConfig().getString(text);
                    if (Vars.prefix.length() > 0) {
                        text = Vars.prefix + text;
                    }
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', text.replaceAll("%version%", getDescription().getVersion())));
                }
            } else {
                if (text.length() > 0) {
                    if (Vars.prefix.length() > 0) {
                        text = Vars.prefix + text;
                    }
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', text.replaceAll("%version%", getDescription().getVersion())));
                }
            }
        } catch (Exception ex) {
            getLogger().info("An error occurred while trying to send a message");
            ex.printStackTrace();
        }
    }

    //////////////////////////////////////

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        Vars.econ = rsp.getProvider();
        return Vars.econ != null;
    }

}
