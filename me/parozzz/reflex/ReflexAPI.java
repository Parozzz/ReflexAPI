/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.reflex;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import me.parozzz.reflex.NMS.ReflectionUtil;
import me.parozzz.reflex.NMS.entity.EntityPlayer;
import me.parozzz.reflex.events.ArmorHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Paros
 */
public class ReflexAPI extends JavaPlugin
{
    public enum Property
    {
        ENTITYPLAYER_LISTENER, ARMOREVENTS_LISTENER;
    }
    
    public static ReflexAPI getAPI()
    {
        return JavaPlugin.getPlugin(ReflexAPI.class);
    }
    
    
    
    @Override
    public void onEnable()
    {
        getDataFolder().mkdir();
    }
    
    private DatabaseManager database;
    public DatabaseManager getDatabase()
    {
        return Optional.ofNullable(database).orElseGet(() -> database = new DatabaseManager(this));
    }
    
    private final Set<Property> registered = new HashSet<>();
    public void addProperty(final Property... properties)
    {
        Stream.of(properties).forEach(property -> 
        {
            if(!registered.add(property))
            {
                return;
            }
            
            try {
                switch(property)
                {
                    case ENTITYPLAYER_LISTENER:
                        Bukkit.getPluginManager().registerEvents((Listener)ReflectionUtil.getMethod(EntityPlayer.class, "getListener").invoke(null), this);
                        break;
                    case ARMOREVENTS_LISTENER:
                        Bukkit.getPluginManager().registerEvents(new ArmorHandler(), this);
                        break;
                }
            }catch(final Exception ex){
                Logger.getLogger(ReflexAPI.class.getSimpleName()).log(Level.SEVERE, null, ex);
            }

        });
    }
}
