package cloud.stivenfocs.PlayerBounties;

import cloud.stivenfocs.PlayerBounties.Bounty.Bounty;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.VaultEco;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

public class BountyHandler {

    public static List<Bounty> bounties = new ArrayList<>();

    public static Boolean isBounted(UUID puid) {
        for(Bounty bounty : bounties) {
            System.out.println("is bounted: " + bounty);
            System.out.println(bounty.getPlayer().equals(puid));
            if (bounty.getPlayer().equals(puid)) {
                return true;
            }
        }
        return false;
    }

    public Bounty startTask(UUID player, UUID creator, int duration, int value, Boolean broadcast) {
        System.out.println("test: " + player);
        try {
            String creator_name = Vars.console_name;
            if (creator != null) {
                Vars.vars.dataConfig.set(player.toString() + ".creator", creator.toString());
                creator_name = Bukkit.getOfflinePlayer(creator).getName();
            } else {
                Vars.vars.dataConfig.set(player.toString() + ".creator", "console");
            }
            Vars.vars.dataConfig.set(player.toString() + ".duration", duration);
            Vars.vars.dataConfig.set(player.toString() + ".value", value);

            Vars.saveDataConfiguration();
            Vars.reloadDataConfiguration();

            Bounty bounty = new Bounty(player, creator, duration, value);
            bounties.add(bounty);

            if (Vars.bounty_broadcast_start.length() > 0) {
                String player_placeholder = "ErrorName";
                if (Bukkit.getOfflinePlayer(player) != null) {
                    player_placeholder = Bukkit.getOfflinePlayer(player).getName();
                }

                if (broadcast) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Vars.bounty_broadcast_start.replaceAll("%creator%", creator_name).replaceAll("%player%", player_placeholder)));
                }
            }

            Bukkit.getPluginManager().registerEvents(bounty, Vars.plugin);

            try {
                Vars.getSelectorHandler().refreshPlayerSelector();
            } catch (Exception ex) {}

            return bounty;
        } catch (Exception ex) {
            Vars.plugin.getLogger().severe("An error occurred while initializing a Bounty, bounty canceled.");
            ex.printStackTrace();
            return null;
        }
    }

    public void expireTask(Bounty bounty) {
        bounty.end();

        bounties.remove(bounty);
        List<Bounty> new_bounties = new ArrayList<>();
        for(Bounty bounty_ : bounties) {
            if (bounty_.getPlayer().equals(bounty.getPlayer())) {
                new_bounties.add(bounty_);
            }
        }
        bounties = new_bounties;

        Vars.vars.dataConfig.set(bounty.getPlayer().toString(), null);
        Vars.saveDataConfiguration();
        Vars.reloadDataConfiguration();

        String player_placeholder = "ErrorName";
        if (Bukkit.getOfflinePlayer(bounty.getPlayer()) != null) {
            player_placeholder = Bukkit.getOfflinePlayer(bounty.getPlayer()).getName();
        }
        final String player_placehold = player_placeholder;

        String creator_placeholder = "ErrorName";
        if (bounty.getCreator() != null) {
            creator_placeholder = Bukkit.getOfflinePlayer(bounty.getCreator()).getName();
        } else {
            creator_placeholder = Vars.console_name;
        }
        final String creator_placehold = creator_placeholder;

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Vars.bounty_broadcast_expire.replaceAll("%player%", player_placeholder).replaceAll("%creator%", creator_placeholder)));

        Bukkit.getScheduler().callSyncMethod(Vars.plugin, new Callable<Boolean>() {
            @Override
            public Boolean call() {
                for(String cmd : Vars.bounty_expire_commands) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%player%", player_placehold).replaceAll("%creator%", creator_placehold));
                }
                return true;
            }
        } );

        Vars.econ.depositPlayer(Bukkit.getOfflinePlayer(bounty.getPlayer()), bounty.getValue());
        if (Bukkit.getPlayer(bounty.getPlayer()) != null) {
            Vars.plugin.sendString(Vars.you_got_money.replaceAll("%value%", String.valueOf(bounty.getValue())), Bukkit.getPlayer(bounty.getPlayer()));
        }

        Vars.getSelectorHandler().refreshPlayerSelector();
    }

    public void killedTask(Bounty bounty) {
        bounty.end();

        for (Bounty b_ : bounties) {
            System.out.println(b_);
        }
        System.out.println(bounty);
        bounties.remove(bounty);

        Vars.vars.dataConfig.set(bounty.getPlayer().toString(), null);

        Vars.saveDataConfiguration();
        Vars.reloadDataConfiguration();

        String player_placeholder = "ErrorName";
        if (Bukkit.getOfflinePlayer(bounty.getPlayer()) != null) {
            player_placeholder = Bukkit.getOfflinePlayer(bounty.getPlayer()).getName();
        }
        final String player_placehold = player_placeholder;

        String creator_placeholder = "ErrorName";
        if (bounty.getCreator() != null) {
            creator_placeholder = Bukkit.getOfflinePlayer(bounty.getCreator()).getName();
        } else {
            creator_placeholder = Vars.console_name;
        }
        final String creator_placehold = creator_placeholder;

        String killer_placeholder = "ErrorName";
        if (Bukkit.getOfflinePlayer(bounty.getKiller()) != null) {
            killer_placeholder = Bukkit.getOfflinePlayer(bounty.getKiller()).getName();
        }
        final String killer_placehold = killer_placeholder;

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Vars.bounty_broadcast_win.replaceAll("%player%", player_placeholder).replaceAll("%killer%", killer_placeholder).replaceAll("%creator%", creator_placeholder)));

        Bukkit.getScheduler().callSyncMethod(Vars.plugin, new Callable<Boolean>() {
            @Override
            public Boolean call() {
                for(String cmd : Vars.bounty_claim_commands) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%player%", player_placehold).replaceAll("%creator%", creator_placehold).replaceAll("%claimer%", killer_placehold));
                }
                return true;
            }
        } );

        Vars.econ.depositPlayer(Bukkit.getOfflinePlayer(bounty.getKiller()), bounty.getValue());
        if (Bukkit.getPlayer(bounty.getKiller()) != null) {
            Vars.plugin.sendString(Vars.you_got_money.replaceAll("%value%", String.valueOf(bounty.getValue())), Bukkit.getPlayer(bounty.getKiller()));
        }

        Vars.getSelectorHandler().refreshPlayerSelector();
    }

}
