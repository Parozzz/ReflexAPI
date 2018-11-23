/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.reflex.NMS.nbt;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import me.parozzz.reflex.Debug;
import me.parozzz.reflex.MCVersion;
import me.parozzz.reflex.NMS.ReflectionUtil;

/**
 *
 * @author Paros
 */
public class NBTList extends NBTBase
{
    private static final Class<?> listClazz;
    private static final Constructor<?> constructor;
    
    private static final Method addToMethod;
    private static final Method getTypeMethod;
    private static final Method sizeMethod;
    
    private static Method removeMethod;
    private static Method getBaseMethod;
    private static Method getCompoundMethod;
    private static Method getStringMethod;
    private static Method getDoubleMethod;
    private static Method getFloatMethod;
    static
    {
        listClazz = ReflectionUtil.getNMSClass("NBTTagList");
        constructor = ReflectionUtil.getConstructor(listClazz);
        
        addToMethod = ReflectionUtil.getMethod(listClazz, "add", NBTBase.getNMSClass());
        getTypeMethod = ReflectionUtil.getMethod(listClazz, MCVersion.V1_8.isEqual()? "f" : "g"); 
        sizeMethod = ReflectionUtil.getMethod(listClazz, "size");
        
        if(MCVersion.nms().contains("1_8_R3"))
        {
            removeMethod = ReflectionUtil.getMethod(listClazz, "a", int.class);

            getBaseMethod = ReflectionUtil.getMethod(listClazz, "g", int.class); 
            getCompoundMethod = ReflectionUtil.getMethod(listClazz, "get", int.class); 
            getStringMethod = ReflectionUtil.getMethod(listClazz, "getString", int.class);  
            getDoubleMethod = ReflectionUtil.getMethod(listClazz, "d", int.class); 
            getFloatMethod = ReflectionUtil.getMethod(listClazz, "e", int.class); 
        }
        else if(MCVersion.nms().contains("1_9_R1") || MCVersion.nms().contains("1_9_R2") || MCVersion.nms().contains("1_10_R1") || MCVersion.nms().contains("1_11_R1"))
        {
            removeMethod = ReflectionUtil.getMethod(listClazz, "remove", int.class);

            getBaseMethod = ReflectionUtil.getMethod(listClazz, "h", int.class); 
            getStringMethod = ReflectionUtil.getMethod(listClazz, "getString", int.class);  
            getCompoundMethod = ReflectionUtil.getMethod(listClazz, "get", int.class);
            getDoubleMethod = ReflectionUtil.getMethod(listClazz, "e", int.class); 
            getFloatMethod = ReflectionUtil.getMethod(listClazz, "f", int.class); 
        }
        else if(MCVersion.nms().contains("1_12_R1"))
        {
            removeMethod = ReflectionUtil.getMethod(listClazz, "remove", int.class);

            getBaseMethod = ReflectionUtil.getMethod(listClazz, "i", int.class); 
            getStringMethod = ReflectionUtil.getMethod(listClazz, "getString", int.class);  
            getCompoundMethod = ReflectionUtil.getMethod(listClazz, "get", int.class);
            getDoubleMethod = ReflectionUtil.getMethod(listClazz, "f", int.class); 
            getFloatMethod = ReflectionUtil.getMethod(listClazz, "g", int.class);
        }
    }
    
    public static Class<?> getNMSClass()
    {
        return listClazz;
    }
    
    public NBTList()
    {
        super(Debug.validateConstructor(constructor));
    }

    public NBTList(final Object nbt) 
    {
        super(nbt);
    }
    
    public float getFloat(final int i)
    {
        return (float)Debug.validateMethod(getFloatMethod, nbtBase, i);
    }
    
    public double getDouble(final int i)
    {
        return (double)Debug.validateMethod(getDoubleMethod, nbtBase, i);
    }
    
    public String getString(final int i)
    {
        return (String)Debug.validateMethod(getStringMethod, nbtBase, i);
    }
    
    public NBTCompound getCompound(final int i)
    {
        return new NBTCompound(Debug.validateMethod(getCompoundMethod, nbtBase, i));
    }
    
    public void remove(final int i)
    {
        Debug.validateMethod(removeMethod, nbtBase, i);
    }
    
    public void addTag(final NBTBase nbt)
    {
        Debug.validateMethod(addToMethod, super.nbtBase, nbt.getNMSObject());
    }
    
    /*
    public NBTBase getTag(final NBTType type, final int i)
    {
        return new NBTBase(type, Debug.validateMethod(getBaseMethod,  super.nbtBase, i));
    }
    
    public Object getTag(final int i)
    {
        return Debug.validateMethod(getBaseMethod,  super.nbtBase, i);
    }*/
    
    public int size()
    {
        return (int)Debug.validateMethod(sizeMethod,  super.nbtBase);
    }
    
    public NBTType getListType()
    {
        return NBTType.getById((byte)Debug.validateMethod(getTypeMethod,  super.nbtBase));
    }
    
    @Override
    public NBTList clone()
    {
        return (NBTList) super.clone();
    }
}
