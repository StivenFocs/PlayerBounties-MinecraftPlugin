package cloud.stivenfocs.PlayerBounties;

import cloud.stivenfocs.PlayerBounties.Bounty.Bounty;
import cloud.stivenfocs.PlayerBounties.Events.SelectorHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Vars {

    public static Loader plugin;

    private static FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    public static Vars vars;

    public static BountyHandler bountyhandler;

    public static Vars getVars() {
        if (vars == null) {
            vars = new Vars();
        }
        return vars;
    }

    public static BountyHandler getBountyHandler() {
        if (bountyhandler == null) {
            bountyhandler = new BountyHandler();
        }
        return bountyhandler;
    }

    public static SelectorHandler selectorhandler;

    public static SelectorHandler getSelectorHandler() {
        if (selectorhandler == null) {
            selectorhandler = new SelectorHandler(plugin);
        }
        return selectorhandler;
    }

    /////////

    public static Economy econ = null;

    public File dataFile = new File(plugin.getDataFolder() + "/" + "data.yml");
    public FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);

    public File pdataFile = new File(plugin.getDataFolder() + "/" + "stats.yml");
    public FileConfiguration pdataConfig = YamlConfiguration.loadConfiguration(pdataFile);

    //////////////////////////

    public static HashMap<UUID, Integer> killstreak = new HashMap<>();

    public static HashMap<UUID, Inventory> opened_inventories = new HashMap<>();

    //////////////////////////

    public static Boolean bounty_end_at_death = false;
    public static String console_name = "CONSole";
    public static List<String> bounty_expire_commands = new ArrayList<>();
    public static List<String> bounty_claim_commands = new ArrayList<>();
    public static Boolean killstreak_bounty_enabled = false;
    public static Integer killstreak_bounty_offset = 99;
    public static Integer killstreak_bounty_value = 500;
    public static Integer killstreak_bounty_duration = 83600;
    public static String player_selector_displayname = "";
    public static String template_selector_displayname = "";
    public static Boolean border_enabled = false;
    public static String border_color = "";
    public static String bounted_placeholder = "";
    public static String not_bounted_placeholder = "";

    public static String prefix = "";
    public static String configuration_reloaded = "";
    public static String you_got_money = "";
    public static String bounty_broadcast_start = "";
    public static String bounty_broadcast_win = "";
    public static String bounty_broadcast_expire = "";
    public static String an_error_ocurred = "";
    public static String insufficient_permissions = "";
    public static String incomplete_command = "";
    public static String unknow_subcommand = "";
    public static String unknow_player = "";
    public static String players_only = "";
    public static String not_enough_money = "";
    public static String not_yourself = "";
    public static String already_wanted = "";
    public static String template_displayname = "";
    public static List<String> template_lore = new ArrayList<>();
    public static List<String> help_admin = new ArrayList<>();
    public static List<String> help_user = new ArrayList<>();

    public static boolean reloadVars() {
        try {
            plugin.reloadConfig();

            try {
                for(UUID puid : opened_inventories.keySet()) {
                    if (Bukkit.getPlayer(puid) != null) {
                        Player p = Bukkit.getPlayer(puid);
                        p.closeInventory();
                    }
                }
            } catch (Exception ex) {
                Vars.plugin.getLogger().severe("An error occurred while trying to close all opened selector inventories");
            }

            getConfig().options().header("Plugin developed by StivenFocs with LOV");
            getConfig().options().copyDefaults(true);
            getVars().dataConfig.options().copyDefaults(true);
            getVars().pdataConfig.options().copyDefaults(true);

            getConfig().addDefault("options.bounty_end_at_death", false);
            getConfig().addDefault("options.console_name", "CONSOLE");
            List<String> new_expire_commands = new ArrayList<>();
            new_expire_commands.add("minecraft:give %player% diamond 1 0 {display:{Name:'§eexpired bounty: %player%'}}");
            new_expire_commands.add("minecraft:give %creator% dirt 1 0 {display:{Name:'§eexpired bounty: %player%'}}");
            getConfig().addDefault("options.bounty_expire.commands", new_expire_commands);
            List<String> new_claim_commands = new ArrayList<>();
            new_claim_commands.add("minecraft:give %claimer% diamond 1 0 {display:{Name:'§eclaimed bounty: %player%'}}");
            getConfig().addDefault("options.bounty_claim.commands", new_claim_commands);
            getConfig().addDefault("options.killstreak_bounty.enabled", false);
            getConfig().addDefault("options.killstreak_bounty.offset", 8);
            getConfig().addDefault("options.killstreak_bounty.value", 500);
            getConfig().addDefault("options.killstreak_bounty.duration", 84000);
            getConfig().addDefault("options.gui.player_selector", "&lSelect a Player");
            getConfig().addDefault("options.gui.teamplate_selector", "&lSelect a Teamplate");
            getConfig().addDefault("options.gui.border.enabled", true);
            getConfig().addDefault("options.gui.border.color", "BLACK"); // Work in progress
            getConfig().addDefault("options.bounted_placeholder", " &cBounty on");
            getConfig().addDefault("options.not_bounted_placeholder", "");
            if (getConfig().get("options.templates") == null) {
                getConfig().addDefault("options.templates.default.material", "NETHER_STAR");
                getConfig().addDefault("options.templates.default.displayname", "&bDefault Template");
                getConfig().addDefault("options.templates.default.value", 500);
                getConfig().addDefault("options.templates.default.price", 800);
                getConfig().addDefault("options.templates.default.duration", 84000);
            }

            getConfig().addDefault("messages.prefix", "");
            getConfig().addDefault("messages.you_got_money", "&aYou got $&f%value%");
            getConfig().addDefault("messages.configuration_reloaded", "&aConfiguration reloaded successfully");
            getConfig().addDefault("messages.bounty_broadcast_start", "&f%creator% &cplaced a bounty on &f%player%");
            getConfig().addDefault("messages.bounty_broadcast_win", "&f%killer% &aclaimed the bounty on &f%player%");
            getConfig().addDefault("messages.bounty_broadcast_expire", "&eThe bounty on &f%player% &eis expired.");
            getConfig().addDefault("messages.an_error_ocurred", "&cAn error occurred while doing this last task");
            getConfig().addDefault("messages.insufficient_permissions", "&cYou do not have enough permissions");
            getConfig().addDefault("messages.incomplete_command", "&cIncomplete command, something is missing...");
            getConfig().addDefault("messages.unknow_subcommand", "&cUnknow subcommand, use /playerbounties help");
            getConfig().addDefault("messages.unknow_player", "&cUnknow player, this player is not online.");
            getConfig().addDefault("messages.players_only", "&cThis command can be executed from players only.");
            getConfig().addDefault("messages.not_enough_money", "&cYou do not have enough money.");
            getConfig().addDefault("messages.not_yourself", "&cYou cannot do this to yourself.");
            getConfig().addDefault("messages.already_wanted", "&cThis player is already wanted");
            getConfig().addDefault("messages.template_displayname", "&b%name%");
            List<String> new_template_lore = new ArrayList<>();
            new_template_lore.add(" ");
            new_template_lore.add("&cValue: &f%value%");
            new_template_lore.add("&cDuration: &f%duration% seconds");
            new_template_lore.add(" ");
            new_template_lore.add("&aPrice $%price%");
            new_template_lore.add("&eClick to pay");
            getConfig().addDefault("messages.template_lore", new_template_lore);
            List<String> new_help_admin = new ArrayList<>();
            new_help_admin.add("&4* &c/bounty");
            new_help_admin.add("&4* &c/bounty help");
            new_help_admin.add("&4* &c/bounty reload");
            getConfig().addDefault("messages.help_admin", new_help_admin);
            List<String> new_help_user = new ArrayList<>();
            new_help_user.add("&4* &c/bounty");
            new_help_user.add("&4* &c/bounty help");
            getConfig().addDefault("messages.help_user", new_help_user);

            plugin.saveConfig();
            plugin.reloadConfig();
            reloadDataConfiguration();
            //reloadpDataConfiguration();

            bounty_end_at_death = getConfig().getBoolean("options.bounty_end_at_death", false);
            console_name = getConfig().getString("options.console_name", "CONSOLE");
            bounty_expire_commands = getConfig().getStringList("options.bounty_expire.commands");
            bounty_claim_commands = getConfig().getStringList("options.bounty_claim.commands");
            killstreak_bounty_enabled = getConfig().getBoolean("options.killstreak_bounty.enabled", false);
            killstreak_bounty_offset = getConfig().getInt("options.killstreak_bounty.offset", 99);
            killstreak_bounty_value = getConfig().getInt("options.killstreak_bounty.value", 500);
            killstreak_bounty_duration = getConfig().getInt("options.killstreak_bounty.duration", 84000);
            player_selector_displayname = getConfig().getString("options.gui.player_selector", "&lSelect a Player");
            template_selector_displayname = getConfig().getString("options.gui.teamplate_selector", "&lSelect a Teamplate");
            border_enabled = getConfig().getBoolean("options.gui.border.enabled");
            border_color = getConfig().getString("options.gui.border.color", "BLACK");
            bounted_placeholder = getConfig().getString("options.bounted_placeholder", " &cBounty on");
            not_bounted_placeholder = getConfig().getString("options.not_bounted_placeholder", "");

            prefix = getConfig().getString("messages.prefix", "");
            you_got_money = getConfig().getString("messages.you_got_money", "&aYou got $&f%value%");
            configuration_reloaded = getConfig().getString("messages.configuration_reloaded", "&aConfiguration reloaded successfully");
            bounty_broadcast_start = getConfig().getString("messages.bounty_broadcast_start", "&f%creator% &cplaced a bounty on &f%cplayer%");
            bounty_broadcast_win = getConfig().getString("messages.bounty_broadcast_win", "&f%killer% &aclaimed the bounty on &f%player%");
            bounty_broadcast_expire = getConfig().getString("messages.bounty_broadcast_expire", "&eThe bounty on &f%player% &eis expired.");
            an_error_ocurred = getConfig().getString("messages.an_error_ocurred", "&cAn error occurred while doing this last task");
            insufficient_permissions = getConfig().getString("messages.insufficient_permissions", "&cYou do not have enough permissions");
            incomplete_command = getConfig().getString("messages.incomplete_command", "&cIncomplete command, something is missing...");
            unknow_subcommand = getConfig().getString("messages.unknow_subcommand", "&cUnknow subcommand, use /playerbounties help");
            unknow_player = getConfig().getString("messages.unknow_player", "&cUnknow player, this player is not online.");
            players_only = getConfig().getString("messages.players_only", "&cThis command can be executed from players only.");
            not_enough_money = getConfig().getString("messages.not_enough_money", "&cYou do not have enough money.");
            not_yourself = getConfig().getString("messages.not_yourself", "&cYou cannot do this to yourself.");
            already_wanted = getConfig().getString("messages.already_wanted", "&cThis player is already wanted");
            template_displayname = getConfig().getString("messages.template_displayname", "&b%name%");
            template_lore = getConfig().getStringList("messages.template_lore");
            help_admin = getConfig().getStringList("messages.help_admin");
            help_user = getConfig().getStringList("messages.help_user");

            plugin.getLogger().info("Configuration reloaded");

            killstreak = new HashMap<>();

            SelectorHandler.closeAll(getSelectorHandler().getPlayerSelector());
            SelectorHandler.closeAll(getSelectorHandler().getTemplateSelector());
            getSelectorHandler().cancelInventories();
            SelectorHandler.chosen_player = new HashMap<>();

            return true;
        } catch (Exception ex) {
            plugin.getLogger().severe("An error occurred while trying to reload the whole configuration, disabling plugin..");
            ex.printStackTrace();
            plugin.getPluginLoader().disablePlugin(plugin);
            return false;
        }
    }

    public static void reloadDataConfiguration() {
        try {
            if (!getVars().dataFile.exists()) {
                getVars().dataFile.createNewFile();
            }
        } catch (Exception ex) {
            plugin.getLogger().severe("An error occurred while trying to get and/or create the data configuration file");
            ex.printStackTrace();
            return;
        }

        try {
            getVars().dataConfig = YamlConfiguration.loadConfiguration(getVars().dataFile);
        } catch (Exception ex) {
            plugin.getLogger().severe("An error occurred while trying to reload the data configuration file");
            ex.printStackTrace();
        }
    }

    public static void saveDataConfiguration() {
        try {
            if (!getVars().dataFile.exists()) {
                getVars().dataFile.createNewFile();
            }
        } catch (Exception ex) {
            plugin.getLogger().severe("An error occurred while trying to get and/or create the data configuration file");
            ex.printStackTrace();
            return;
        }

        try {
            getVars().dataConfig.save(getVars().dataFile);
        } catch (Exception ex) {
            plugin.getLogger().severe("An error occurred while trying to save into the data configuration file");
            ex.printStackTrace();
        }
    }

    public static void reloadpDataConfiguration() {
        try {
            if (!getVars().pdataFile.exists()) {
                getVars().pdataFile.createNewFile();
            }
        } catch (Exception ex) {
            plugin.getLogger().severe("An error occurred while trying to get and/or create the pdata configuration file");
            ex.printStackTrace();
            return;
        }

        try {
            getVars().pdataConfig = YamlConfiguration.loadConfiguration(getVars().pdataFile);
        } catch (Exception ex) {
            plugin.getLogger().severe("An error occurred while trying to reload the pdata configuration file");
            ex.printStackTrace();
        }
    }

    public static void savepDataConfiguration() {
        try {
            if (!getVars().pdataFile.exists()) {
                getVars().pdataFile.createNewFile();
            }
        } catch (Exception ex) {
            plugin.getLogger().severe("An error occurred while trying to get and/or create the pdata configuration file");
            ex.printStackTrace();
            return;
        }

        try {
            getVars().pdataConfig.save(getVars().pdataFile);
        } catch (Exception ex) {
            plugin.getLogger().severe("An error occurred while trying to save into the data configuration file");
            ex.printStackTrace();
        }
    }

    //////////////////////

    public static boolean isPermittedAdmin(CommandSender sender) {
        return sender.hasPermission("playerbounties.admin") || sender.hasPermission("playerbounties.*");
    }

}
