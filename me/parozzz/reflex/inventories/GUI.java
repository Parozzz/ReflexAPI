/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.reflex.inventories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import me.parozzz.reflex.utilities.ItemUtil;
import me.parozzz.reflex.utilities.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Paros
 */
public abstract class GUI
{
    static
    {
        Bukkit.getPluginManager().registerEvents(new Listener()
        {
            @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
            private void onInventoryOpen(final InventoryOpenEvent e)
            {
                if(e.getInventory().getHolder() instanceof GUIHolder)
                {
                    GUI gui = ((GUIHolder)e.getInventory().getHolder()).getGUI();
                    Util.ifCheck(!gui.changed.contains(e.getPlayer().getUniqueId()), () -> gui.onOpen(e));
                }
            }
            
            @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
            private void onInventoryClick(final InventoryClickEvent e)
            {
                if(e.getInventory().getHolder() instanceof GUIHolder)
                {
                    e.setCancelled(true);
                    
                    GUI gui = ((GUIHolder)e.getInventory().getHolder()).getGUI();
                    if(e.getInventory().equals(e.getClickedInventory()))
                    {
                        gui.onClick(e);
                        Optional.ofNullable(e.getCurrentItem())
                                .filter(ItemUtil::nonNull)
                                .map(item -> gui.itemActions.get(e.getSlot()))
                                .filter(Objects::nonNull)
                                .ifPresent(consumer -> consumer.accept(e));
                    }
                    else if(e.getClickedInventory() != null)
                    {
                        gui.onBottomInventoryClick(e);
                    }
                    
                }
            }
            
            @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
            private void onInventoryClose(final InventoryCloseEvent e)
            {
                if(e.getInventory().getHolder() instanceof GUIHolder)
                {
                    GUI gui = ((GUIHolder)e.getInventory().getHolder()).getGUI();
                    Util.ifCheck(!gui.changed.contains(e.getPlayer().getUniqueId()), () -> gui.onClose(e));
                }
            }
            
        }, JavaPlugin.getProvidingPlugin(GUI.class));
    }

    private final GUI instance;
    private Inventory i;
    
    private boolean fixedSize = false;
    
    private final Map<Integer, Consumer<InventoryClickEvent>> itemActions;
    private final Set<UUID> changed; 
    public GUI(final String title, final int size)
    {
        changed = new HashSet<>();
        itemActions = new HashMap<>();
        
        instance = this;
        i = Bukkit.createInventory(new GUIHolder(), (size / 9) * 9, Util.cc(title));
    }
    
    /**
     * This constructor has size fixed based on how much items you want to add in
     * @param title 
     */
    public GUI(final String title)
    {
        this(title, 0);
        fixedSize = true;
    }
    
    /**
     * 
     * @param slot - The slot to set the item
     * @param item - The itemStack
     * @return - Return if the item has been successfully added
     */
    public final boolean setItem(final int slot, final ItemStack item)
    {
        return setItem(slot, item, e -> {});
    }
    
    /**
     * 
     * @param slot - The slot to set the item
     * @param item - The itemStack
     * @param consumer - The consumer to execute when the item is clicked
     * @return - Return if the item has been successfully added
     */
    public final boolean setItem(final int slot, final ItemStack item, final Consumer<InventoryClickEvent> consumer)
    {
        if(i.getSize() < slot)
        {
            if(fixedSize && slot < 54 && i.getSize() != 54)
            {
                this.resetInventory(((slot / 9) + 1) *9, i.getTitle());
            }
            else
            {
                return false;
            }
        }
        
        i.setItem(slot, item);
        itemActions.put(slot, consumer);
        return true;
    }
    /**
    * 
    * @param item - The itemStack to add
    * @return - The slot where the item has been added
    */    public final int addItem(final ItemStack item)
    {
        return addItem(item, e -> { });
    }
    
    /**
     * 
     * @param item - The itemStack to add
     * @param consumer - The consumer to execute if the item is clicked
     * @return - The slot where the item has been added
     */
    public final int addItem(final ItemStack item, final Consumer<InventoryClickEvent> consumer)
    {
        int slot = i.firstEmpty();
        if(slot == -1)
        {
            if(fixedSize && i.getSize() != 54)
            {
                this.resetInventory(i.getSize() + 9, i.getTitle());
                slot = i.firstEmpty();
            }
            else
            {
                return -1;
            }
        }
        
        i.setItem(slot, item);
        itemActions.put(slot, consumer);
        
        return slot;
    }
    
    public final void resetInventory(final int size, final String title)
    {
        List<HumanEntity> viewers = new ArrayList<>(i.getViewers());
        changed.addAll(viewers.stream().map(HumanEntity::getUniqueId).collect(Collectors.toSet()));
        
        Inventory newInventory = Bukkit.createInventory(new GUIHolder(), (size / 9) * 9, Util.cc(title));
        newInventory.setContents(i.getContents());
        viewers.forEach(he -> he.openInventory(newInventory));
        
        changed.clear();
        i = newInventory;
    }
    
    protected void onOpen(final InventoryOpenEvent e) {  }
    
    protected void onClose(final InventoryCloseEvent e) {  }
    
    protected void onClick(final InventoryClickEvent e) {  }
    
    protected void onBottomInventoryClick(final InventoryClickEvent e) {  }
    
    public final void open(final HumanEntity he)
    {
        he.openInventory(i);
    }
    
    private final class GUIHolder implements InventoryHolder
    {
        private GUI getGUI()
        {
            return instance;
        }

        @Override
        public Inventory getInventory()
        {
            return i;
        }
    }
}
