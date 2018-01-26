/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.reflex.inventories;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 *
 * @author Paros
 */
public class PagedGUI 
{
    public PagedGUI(final String title)
    {
        
    }
    
    private class PagedHolder implements InventoryHolder
    {
        
        @Override
        public Inventory getInventory() 
        {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
}
