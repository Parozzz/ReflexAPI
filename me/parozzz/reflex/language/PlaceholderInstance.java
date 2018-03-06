/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.reflex.language;

import org.bukkit.command.CommandSender;

/**
 *
 * @author Paros
 */
public class PlaceholderInstance 
{
    private String message;
    protected PlaceholderInstance(final String message)
    {
        this.message = message;
    }
    
    public PlaceholderInstance parsePlaceholder(final String holder, final String parser)
    {
        if(message != null && !message.isEmpty())
        {
            message = message.replace(holder, parser);
        }
        return this;
    }
    
    public void sendMessage(final CommandSender cs)
    {
        if(message != null && !message.isEmpty())
        {
            cs.sendMessage(message);
        }
    }
    
    public String getMessage()
    {
        return message;
    }
}
