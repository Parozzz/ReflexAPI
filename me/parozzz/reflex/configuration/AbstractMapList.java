/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.reflex.configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 *
 * @author Paros
 * @param <T>
 */
public abstract class AbstractMapList<T> implements Iterable<T>
{
    protected final Map<String, T> map;
    public AbstractMapList()
    {
        map = new HashMap<>();
    }
    
    public Set<String> getKeys()
    {
        return map.keySet();
    }
    
    public Map<String, T> getView()
    {
        return Collections.unmodifiableMap(map);
    }

    public void forEach(final BiConsumer<String, T> consumer)
    {
        map.forEach(consumer);
    }
    
    @Override
    public Iterator<T> iterator() 
    {
        return this.map.values().iterator();
    }
}
