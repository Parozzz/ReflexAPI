/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.reflex.language;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.parozzz.reflex.utilities.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author Paros
 */
public class LanguageManager
{
    private final static Logger logger = Logger.getLogger(LanguageManager.class.getName());
    
    private final Map<String, String> messageMap;
    /**
     * Convert the configuration section composed of a Map<String, String> to an organized converted map with transled color codes. If any of the subKey of the ConfigurationSection is not a String, it will be skipped. Internally call the loadSection method.
     * @param path The path of the config
     * @thorws IllegalArgumentException If the path is null
     */
    public LanguageManager(final ConfigurationSection path)
    {
        if(path == null)
        {
            throw new IllegalArgumentException("The path is null.");
        }
        
        messageMap = new HashMap<>();
        this.loadSection(path);
    }
    
    /**
     * Initialize the internal map
     */
    public LanguageManager()
    {
        messageMap = new HashMap<>();
    }
    
    /**
     * Load the section
     * @param path The section to load
     */
    public final void loadSection(final ConfigurationSection path)
    {
        messageMap.clear(); //Clearing the map in case already has some values in it
        for(String key : path.getKeys(false))
        {
            if(!path.isString(key))
            {
                logger.log(Level.WARNING, "One of the value is not a String [{0}]. Skipping.", key);
                continue;
            }
            
            String message = Util.cc(path.getString(key));
            this.messageMap.put(key.toLowerCase(), message);
        }
    }
    
    /**
     * Return the message to the associated key (Is automatically lowerCased)
     * @param key The key of the message.
     * @return The message if the key is present, null otherwise.
     */
    public String getMessage(final String key)
    {
        return key == null ? null : messageMap.get(key.toLowerCase());
    }
    
    /**
     * Get an instance of PlaceholderInstance class of the message registered to the passed key.
     * @param key The key of the message the get.
     * @return The instance of the PlaceholderInstance.
     */
    public PlaceholderInstance getPlaceholder(final String key)
    {
        String message = this.getMessage(key);
        return new PlaceholderInstance(message);
    }
    
    /**
     * Send the message associated with the key to the CommandSender paramenter
     * @param cs The CommandSender to send the message to.
     * @param key The key associated with the message.
     * @return True if the message has been sent, false otherwise.
     */
    public boolean sendMessage(final CommandSender cs, final String key)
    {
        String message = key == null ? null : messageMap.get(key.toLowerCase());
        if(message == null || message.isEmpty())
        {
            return false;
        }
        
        cs.sendMessage(message);
        return true;
    }
    
    /**
     * Iterate through all the pair key-value inside the main map
     * @param consumer The consumer to execute for each pair
     */
    public void forEach(final BiConsumer<String, String> consumer)
    {
        messageMap.forEach(consumer);
    }
}
