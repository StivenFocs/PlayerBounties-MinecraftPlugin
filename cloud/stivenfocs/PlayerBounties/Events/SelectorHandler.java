package cloud.stivenfocs.PlayerBounties.Events;


import cloud.stivenfocs.PlayerBounties.BountyHandler;
import cloud.stivenfocs.PlayerBounties.Loader;
import cloud.stivenfocs.PlayerBounties.Vars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SelectorHandler implements Listener {

    public Loader plugin;

    public static List<UUID> players_in_menu = new ArrayList<>();

    public static HashMap<UUID, UUID> chosen_player = new HashMap<>();

    public static Inventory player_selector = null;
    public static Inventory template_selector = null;
    public static HashMap<ItemStack, String> templates_items = new HashMap<>();

    ///////////////////////////////

    public SelectorHandler(Loader plugin) {
        this.plugin = plugin;

        getPlayerSelector();
        getTemplateSelector();
    }

    ///////////////////////////////

    public Inventory getPlayerSelector() {
        if (player_selector == null) {
            player_selector = Bukkit.createInventory(null, 54, "Choose a player");
        }
        return player_selector;
    }

    public void refreshPlayerSelector() {
        player_selector.clear();

        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!BountyHandler.isBounted(onlinePlayer.getUniqueId())) {
                ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1, (short) SkullType.PLAYER.ordinal());
                SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
                meta.setOwner(onlinePlayer.getName());
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', onlinePlayer.getName()));
                skull.setItemMeta(meta);

                player_selector.addItem(skull);
            }
        }
    }

    ///////////////////////////////

    public Inventory getTemplateSelector() {
        if (template_selector == null) {
            template_selector = Bukkit.createInventory(null, 54, "Choose a bounty template");
        }
        return template_selector;
    }

    public void refreshTemplateSelector() {
        template_selector.clear();
        templates_items = new HashMap<>();

        int slot = 0;
        for(String template : plugin.getConfig().getConfigurationSection("options.templates").getKeys(false)) {
            String template_path = "options.templates." + template;

            ItemStack templateItem = new ItemStack(Material.OAK_SIGN, 1);
            ItemMeta templateMeta = templateItem.getItemMeta();
            templateMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(template_path + ".displayname")));
            List<String> itemLore = new ArrayList<>();
            itemLore.add(ChatColor.translateAlternateColorCodes('&', " "));
            itemLore.add(ChatColor.translateAlternateColorCodes('&', "&cValue &f%value%".replaceAll("%value%", plugin.getConfig().getString(template_path + ".value"))));
            itemLore.add(ChatColor.translateAlternateColorCodes('&', "&cDuration &f%duration%".replaceAll("%duration%", plugin.getConfig().getString(template_path + ".duration"))));
            itemLore.add(ChatColor.translateAlternateColorCodes('&', ""));
            itemLore.add(ChatColor.translateAlternateColorCodes('&', "&ePrice: &f%price%".replaceAll("%price%", plugin.getConfig().getString(template_path + ".price"))));
            itemLore.add(ChatColor.translateAlternateColorCodes('&', "&eLeft click to pay"));
            templateMeta.setLore(itemLore);
            templateItem.setItemMeta(templateMeta);

            int count = 1;
            while(true) {
                if (template_selector.contains(templateItem)) {
                    count++;
                    templateItem.setAmount(count);
                } else {
                    break;
                }
            }

            template_selector.setItem(slot, templateItem);
            slot++;
            templates_items.put(templateItem, template);
        }
    }

    ///////////////////////////////

    public static void closeAll(Inventory gui) {
        List<HumanEntity> temp_viewers = new ArrayList<>();

        for(HumanEntity human : gui.getViewers()) {
            temp_viewers.add(human);
        }

        for(HumanEntity human : temp_viewers) {
            Player p = (Player) human;
            p.closeInventory();
        }
    }

    ///////////////////////////////

    @EventHandler
    public void onGUIClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();

        System.out.println(chosen_player.toString());
        UUID t_chosen_player = chosen_player.get(p.getUniqueId());

        if (event.getCurrentItem() != null) {
            if (event.getClickedInventory().equals(player_selector)) {
                event.setCancelled(true);

                if (Bukkit.getPlayerExact(event.getCurrentItem().getItemMeta().getDisplayName()) != null) {
                    Player clicked_player = Bukkit.getPlayerExact(event.getCurrentItem().getItemMeta().getDisplayName());

                    if (!BountyHandler.isBounted(clicked_player.getUniqueId())) {
                        if (!clicked_player.getUniqueId().equals(p.getUniqueId())) {
                            chosen_player.put(p.getUniqueId(), clicked_player.getUniqueId());
                            p.closeInventory();
                            refreshTemplateSelector();
                            p.openInventory(getTemplateSelector());
                        } else {
                            p.closeInventory();
                            plugin.sendString(Vars.not_yourself, p);
                        }
                    } else {
                        p.closeInventory();
                        plugin.sendString(Vars.already_wanted, p);
                    }
                } else {
                    refreshPlayerSelector();
                }
            }
            if (event.getClickedInventory().equals(template_selector)) {
                event.setCancelled(true);

                ItemStack clicked = event.getCurrentItem();
                if (templates_items.containsKey(clicked)) {
                    p.closeInventory();

                    String clicked_template = templates_items.get(clicked);
                    String template_path = "options.templates." + clicked_template;

                    int price = plugin.getConfig().getInt(template_path + ".price");
                    int value = plugin.getConfig().getInt(template_path + ".value");
                    int duration = plugin.getConfig().getInt(template_path + ".duration");

                    if (Vars.econ.getBalance(p) >= price) {

                        try {
                            Vars.getBountyHandler().startTask(t_chosen_player, p.getUniqueId(), duration, value, true);
                            chosen_player.remove(p.getUniqueId());
                        } catch (Exception ex) {
                            System.out.println(ex);
                        } finally {
                            Vars.econ.withdrawPlayer(p, price);
                        }
                    } else {
                        plugin.sendString(Vars.not_enough_money, p);
                    }
                } else {
                    refreshTemplateSelector();
                }
            }
        }
    }

    @EventHandler
    public void onGUIClose(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
    }

    ///////////////////////////////

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        refreshPlayerSelector();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        UUID puid = p.getUniqueId();

        if (players_in_menu.contains(puid)) {
            players_in_menu.remove(puid);
        }

        refreshPlayerSelector();
    }

}
