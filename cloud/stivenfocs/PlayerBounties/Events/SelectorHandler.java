package cloud.stivenfocs.PlayerBounties.Events;


import cloud.stivenfocs.PlayerBounties.BountyHandler;
import cloud.stivenfocs.PlayerBounties.Loader;
import cloud.stivenfocs.PlayerBounties.Vars;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
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
            player_selector = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', Vars.player_selector_displayname));
        }
        return player_selector;
    }

    public void refreshPlayerSelector() {
        if (player_selector != null) {
            player_selector.clear();
        } else {
            player_selector = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', Vars.player_selector_displayname));
        }

        if (Vars.border_enabled) {
            ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
            ItemMeta borderMeta = border.getItemMeta();
            borderMeta.setDisplayName(" ");
            border.setItemMeta(borderMeta);

            player_selector.setItem(0, border);
            player_selector.setItem(1, border);
            player_selector.setItem(2, border);
            player_selector.setItem(3, border);
            player_selector.setItem(4, border);
            player_selector.setItem(5, border);
            player_selector.setItem(6, border);
            player_selector.setItem(7, border);
            player_selector.setItem(8, border);

            player_selector.setItem(9, border);
            player_selector.setItem(18, border);
            player_selector.setItem(27, border);
            player_selector.setItem(36, border);

            player_selector.setItem(17, border);
            player_selector.setItem(26, border);
            player_selector.setItem(35, border);
            player_selector.setItem(44, border);

            player_selector.setItem(45, border);
            player_selector.setItem(46, border);
            player_selector.setItem(47, border);
            player_selector.setItem(48, border);
            player_selector.setItem(49, border);
            player_selector.setItem(50, border);
            player_selector.setItem(51, border);
            player_selector.setItem(52, border);
            player_selector.setItem(53, border);
        }

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

    public void cancelInventories() {
        player_selector = null;
        template_selector = null;
    }

    ///////////////////////////////

    public Inventory getTemplateSelector() {
        if (template_selector == null) {
            template_selector = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', Vars.template_selector_displayname));
        }
        return template_selector;
    }

    public void refreshTemplateSelector() {
        if (template_selector != null) {
            template_selector.clear();
        } else {
            template_selector = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', Vars.template_selector_displayname));
        }
        templates_items = new HashMap<>();

        int slot = 0;

        if (Vars.border_enabled) {
            slot = 10;

            ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
            ItemMeta borderMeta = border.getItemMeta();
            borderMeta.setDisplayName(" ");
            border.setItemMeta(borderMeta);

            template_selector.setItem(0, border);
            template_selector.setItem(1, border);
            template_selector.setItem(2, border);
            template_selector.setItem(3, border);
            template_selector.setItem(4, border);
            template_selector.setItem(5, border);
            template_selector.setItem(6, border);
            template_selector.setItem(7, border);
            template_selector.setItem(8, border);

            template_selector.setItem(9, border);
            template_selector.setItem(18, border);
            template_selector.setItem(27, border);
            template_selector.setItem(36, border);

            template_selector.setItem(17, border);
            template_selector.setItem(26, border);
            template_selector.setItem(35, border);
            template_selector.setItem(44, border);

            template_selector.setItem(45, border);
            template_selector.setItem(46, border);
            template_selector.setItem(47, border);
            template_selector.setItem(48, border);
            template_selector.setItem(49, border);
            template_selector.setItem(50, border);
            template_selector.setItem(51, border);
            template_selector.setItem(52, border);
            template_selector.setItem(53, border);
        }

        for(String template : plugin.getConfig().getConfigurationSection("options.templates").getKeys(false)) {
            try {
                String template_path = "options.templates." + template;

                ItemStack templateItem = new ItemStack(Material.valueOf(plugin.getConfig().getString(template_path + ".material")), 1);
                ItemMeta templateMeta = templateItem.getItemMeta();
                templateMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Vars.template_displayname.replaceAll("%name%", plugin.getConfig().getString(template_path + ".displayname"))));
                templateMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                templateMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                List<String> lore = cloud.stivenfocs.stivenUtils.colorlist(Vars.template_lore);
                List<String> placeholdered_lore = new ArrayList<>();
                for (String line : lore) {
                    placeholdered_lore.add(line.replaceAll("%value%", plugin.getConfig().getString(template_path + ".value")).replaceAll("%duration%", plugin.getConfig().getString(template_path + ".duration")).replaceAll("%price%", this.plugin.getConfig().getString(template_path + ".price")));
                }
                templateMeta.setLore(placeholdered_lore);
                templateItem.setItemMeta(templateMeta);

                int count = 1;
                while (true) {
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
            } catch (Exception ex) {
                plugin.getLogger().warning("Unable to build the '" + template + "' template's button, didn't added to the template inventory");
            }
        }
    }

    ///////////////////////////////

    public static void closeAll(Inventory gui) {
        List<HumanEntity> temp_viewers = new ArrayList<>();

        try {
            for (HumanEntity human : gui.getViewers()) {
                temp_viewers.add(human);
            }
        } catch (Exception ex) {}

        try {
            for (HumanEntity human : temp_viewers) {
                Player p = (Player) human;
                p.closeInventory();
            }
        } catch (Exception ex) {}
    }

    ///////////////////////////////

    @EventHandler
    public void onGUIClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        if (event.getCurrentItem() == null) return;

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
