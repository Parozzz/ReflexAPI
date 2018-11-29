/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.reflex.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author Paros
 */
public class MapArray 
{
    private static final Logger logger = Logger.getLogger(MapArray.class.getSimpleName());
    
    private final Map<String, String> values;
    public MapArray(final String s)
    {
        this(s.split(","));
    }
    
    public MapArray(final String[] t)
    {
        values=new HashMap<>();

        Stream.of(t).map(s -> s.split(":")).forEach(array -> 
        {
            if(array.length == 1)
            {
                logger.log(Level.WARNING, "Wrong formatting of a value. It should be like {header}:{value}");
                return;
            }
            
            values.put(array[0].toLowerCase(), array[1]);
        });
    }

    public Map<String, String> getKeys()
    {
        return new HashMap<>(values);
    }
    
    public boolean hasKey(final String key)
    {
        return values.containsKey(key.toLowerCase());
    }
    
    public String getValue(final String key)
    {
        return values.get(key.toLowerCase());
    }
    
    public <T> T getValue(final String key, final Function<String, T> function)
    {
        return Optional.ofNullable(values.get(key.toLowerCase())).map(function).orElseGet(() -> null);
    }

    public <T> T getUpperValue(final String key, final Function<String, T> function)
    {
        return Optional.ofNullable(values.get(key.toLowerCase())).map(String::toUpperCase).map(function).orElseGet(() -> null);
    }
}
