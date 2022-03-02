package cloud.stivenfocs.PlayerBounties;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIExpansion extends PlaceholderExpansion {

    private final Loader plugin;
    public PAPIExpansion(Loader plugin) {
        this.plugin = plugin;
    }

    ///////////////////////

    @Override
    public String getAuthor() {
        return "StivenFocs";
    }

    @Override
    public String getIdentifier() {
        return "playerbounties";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("bounty")) {
            if (BountyHandler.isBounted(player.getUniqueId())) {
                return Vars.bounted_placeholder;
            }
            return Vars.not_bounted_placeholder;
        }
        if (params.equalsIgnoreCase("value")){
            if (BountyHandler.isBounted(player.getUniqueId())) {
                return String.valueOf(BountyHandler.getBounty(player.getUniqueId()).getValue());
            }
            return "";
        }

        return null;
    }

}