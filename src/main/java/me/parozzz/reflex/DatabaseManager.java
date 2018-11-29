/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.reflex;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Paros
 */
public class DatabaseManager 
{
    private final Map<String, Table> tables;
    private final String connection;
    protected DatabaseManager(final ReflexAPI api)
    {
        tables = new HashMap<>();
        connection = "jdbc:sqlite:"+api.getDataFolder().getAbsolutePath() + File.separator + "database.db";
        
        try {
            if(DriverManager.getConnection(connection) == null)
            {
                Logger.getLogger(DatabaseManager.class.getName()).log(Level.WARNING, "Connection to the database was not established on start");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void registerTable(final Table table)
    {
        String name = table.getTableName().toLowerCase();
        if(tables.containsKey(name))
        {
            throw new IllegalArgumentException("A table named "+table.getTableName()+" already exist in this database");
        }
        
        tables.put(name, table);
    }
    
    public Table getTable(final String tableName)
    {
        return tables.get(tableName);
    }
    
    public static abstract class Table
    {
        private final DatabaseManager manager;
        public Table()
        {
            this.manager = ReflexAPI.getAPI().getDatabase();
        }
        
        public abstract String getTableName();
        
        protected final void register()
        {
           manager.registerTable(this);
        }
        
        protected final synchronized Connection getConnection() throws SQLException
        {
            Connection connection = DriverManager.getConnection(manager.connection);
            if(connection == null)
            {
                Logger.getLogger(DatabaseManager.class.getName()).log(Level.WARNING, "Connection to the database was not established");
            }
            
            return connection;
        }
    }
}
