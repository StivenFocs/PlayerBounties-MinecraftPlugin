package cloud.stivenfocs.PlayerBounties.Events;

import cloud.stivenfocs.PlayerBounties.Bounty.Bounty;
import cloud.stivenfocs.PlayerBounties.BountyHandler;
import cloud.stivenfocs.PlayerBounties.Loader;
import cloud.stivenfocs.PlayerBounties.Vars;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.UUID;

public class PrimaryKillEvent implements Listener {

    public static Loader plugin;

    private static FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    public PrimaryKillEvent(Loader plugin) {
        this.plugin = plugin;
    }

    //////////////////////////

    public static HashMap<UUID, UUID> lastHit = new HashMap<>();

    //////////////////////////

    HashMap<UUID, Integer> hitExpireTask = new HashMap<>();

    @EventHandler
    void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();
            if (event.getDamager() instanceof Player) {
                Player damager = (Player) event.getDamager();

                if (!lastHit.containsKey(victim.getUniqueId()) || !lastHit.get(victim.getUniqueId()).equals(damager.getUniqueId())) {
                    if (hitExpireTask.containsKey(victim.getUniqueId())) {
                        Bukkit.getScheduler().cancelTask(hitExpireTask.get(victim.getUniqueId()));
                        hitExpireTask.remove(victim.getUniqueId());
                    }

                    final Boolean[] start = {true};
                    hitExpireTask.put(victim.getUniqueId(), Bukkit.getScheduler().runTaskTimerAsynchronously(Vars.plugin, new Runnable() {
                        public void run() {
                            if (start[0]) {
                                lastHit.put(victim.getUniqueId(), damager.getUniqueId());
                            } else {
                                int taskId = hitExpireTask.get(victim.getUniqueId());
                                hitExpireTask.remove(victim.getUniqueId());
                                lastHit.remove(victim.getUniqueId());
                                Bukkit.getScheduler().cancelTask(taskId);
                            }
                        }
                    }, 0L, 20L * 4).getTaskId());
                }
            } else {
                lastHit.remove(victim.getUniqueId());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();

        if (lastHit.containsKey(victim.getUniqueId())) {
            UUID killer_uuid = lastHit.get(victim.getUniqueId());

            for(Bounty bounty : BountyHandler.bounties) {
                if (bounty.getPlayer().equals(victim.getUniqueId())) {
                     bounty.setKiller(killer_uuid);
                }
            }
            //lastKill.put(victim.getUniqueId(), killer_uuid);

            Vars.killstreak.remove(victim.getUniqueId());
            lastHit.remove(victim.getUniqueId());

            if (Vars.killstreak.containsKey(killer_uuid)) {
                Vars.killstreak.put(killer_uuid, Vars.killstreak.get(killer_uuid) + 1);
            } else {
                Vars.killstreak.put(killer_uuid, 1);
            }

            if (Vars.killstreak_bounty_enabled) {
                System.out.println();
                if (Vars.killstreak.get(killer_uuid) >= Vars.killstreak_bounty_offset) {
                    Vars.killstreak.remove(killer_uuid);
                    if (!BountyHandler.isBounted(killer_uuid)) {
                        Vars.getBountyHandler().startTask(killer_uuid, null, Vars.killstreak_bounty_duration, Vars.killstreak_bounty_value, true);
                    }
                }
            } else {
                Vars.killstreak.remove(killer_uuid);
            }
        }
    }

}
