package cloud.stivenfocs.PlayerBounties.Bounty;

import cloud.stivenfocs.PlayerBounties.BountyHandler;
import cloud.stivenfocs.PlayerBounties.Vars;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Bounty implements Listener {
    Bounty This = this;
    UUID player;
    UUID creator = null;
    int duration;
    int value;
    int timerTaskId;
    int secondsPassed = 0;
    UUID killer = null;
    boolean ended = false;

    public Bounty(UUID player, UUID creator, int duration, int value) {
        try {
            this.player = player;
            this.creator = creator;
            this.duration = duration;
            this.value = value;

            Bukkit.getPluginManager().registerEvents(this, Vars.plugin);

            this.timerTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(Vars.plugin, new Runnable() {
                public void run() {
                    if (secondsPassed <= duration) {
                        secondsPassed++;
                        if (ended) {
                            Bukkit.getScheduler().cancelTask(timerTaskId);
                        }
                    } else {
                        end();
                        Vars.getBountyHandler().expireTask(This);
                    }
                }
            }, 0L, 20L).getTaskId();
        } catch (Exception ex) {
            Vars.plugin.getLogger().severe("An error occurred while creating a bounty, bounty canceled.");
            ex.printStackTrace();
        }
    }

    public UUID getPlayer() {
        return this.player;
    }

    public UUID getCreator() {
        return this.creator;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getRemainingSeconds() {
        return (duration - secondsPassed);
    }

    public int getValue() {
        return this.value;
    }

    public int getTimerTaskId() {
        return this.timerTaskId;
    }

    public void setKiller(UUID killer) {
        this.killer = killer;
    }

    public UUID getKiller() {
        return this.killer;
    }

    public String toString() {
        String string_creator = "CONSOLE";
        if (this.creator != null) {
            string_creator = this.creator.toString();
        }
        return "{" + player.toString() + "," + string_creator + "," + duration + "," + value + "," + timerTaskId + "}";
    }

    public void end() {
        if (!this.ended) {
            this.ended = true;
            Bukkit.getScheduler().cancelTask(this.timerTaskId);
            List<Bounty> temp_bounties = new ArrayList<>();
            for(Bounty bounty : BountyHandler.bounties) {
                if (!bounty.getPlayer().equals(this.getPlayer())) {
                    temp_bounties.add(bounty);
                }
            }
            BountyHandler.bounties = temp_bounties;
        }
    }

    //////////////////////////

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerDeath(PlayerDeathEvent event) {
        if (!ended) {
            Player p = event.getEntity();
            if (p.getUniqueId() != null && p.getUniqueId().equals(this.player)) {
                if (this.killer != null) {
                    this.end();
                    Vars.getBountyHandler().killedTask(This);
                    Bukkit.getScheduler().cancelTask(timerTaskId);
                }
            }
        }
    }

}
