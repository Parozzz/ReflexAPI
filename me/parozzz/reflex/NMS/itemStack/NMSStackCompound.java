/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.reflex.NMS.itemStack;

import java.lang.reflect.Method;
import me.parozzz.reflex.NMS.ReflectionUtil;
import me.parozzz.reflex.NMS.nbt.NBTCompound;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Paros
 */
public class NMSStackCompound extends NBTCompound
{
    private final NMSStack nmsStack;
    public NMSStackCompound(final ItemStack itemStack)
    {
        this(new NMSStack(itemStack));
    }
    
    public NMSStackCompound(final NMSStack nmsStack)
    {
        super(nmsStack.getNMSTag());
        this.nmsStack = nmsStack;
    }
    
    public NMSStack getNMSStack()
    {
        return nmsStack;
    }
    
    public ItemStack getItemStack()
    {
        return nmsStack.getBukkitItem();
    }
    
    @Override
    public NMSStackCompound clone()
    {
        return new NMSStackCompound(this.nmsStack.clone());
    }
}
