/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.reflex.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Paros
 */
public class ComplexMapList extends AbstractMapList<List<MapArray>>
{
    public ComplexMapList(final List<Map<?, ?>> list)
    {
        super();
        list.stream().map(Map::entrySet).forEach(set -> 
        {
            set.forEach(e -> map.computeIfAbsent(e.getKey().toString().toLowerCase(), temp -> new ArrayList<>()).add(new MapArray(e.getValue().toString().split(","))));
        });
    }
    
    public List<MapArray> getMapArrays(final String key)
    {
        return map.get(key.toLowerCase());
    }
}
