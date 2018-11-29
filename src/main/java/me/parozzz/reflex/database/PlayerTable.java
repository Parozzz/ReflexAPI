/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.reflex.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import me.parozzz.reflex.utilities.TaskUtil;
import me.parozzz.reflex.utilities.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Paros
 */
public class PlayerTable 
{
    private final static Logger logger = Logger.getLogger(PlayerTable.class.getName());
    
    private final String GET_UUID = "SELECT * FROM players WHERE name = ?;";
    private final String ADD_PLAYER = "INSERT OR IGNORE INTO players (name, id) VALUES (?,?);";
    private final String REMOVE_NAME_PLAYER = "DELETE FROM players WHERE name = ?;";
    private final String REMOVE_UUID_PLAYER = "DELETE FROM players WHERE id = ?;";
    private final String GET_NAME = "SELECT * FROM players WHERE id = ?;";
    private final String UPDATE = "UPDATE players SET name = ?, id = ? WHERE id = ?;";
    
    private final IDatabase database;
    public PlayerTable(final IDatabase database, final JavaPlugin plugin)
    {
        this.database = database;
        
        Bukkit.getPluginManager().registerEvents(new Listener()
        {
            @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
            private void onPlayerJoin(final PlayerJoinEvent e)
            {
                Util.ifCheck(!e.getPlayer().hasPlayedBefore(), () -> addPlayer(e.getPlayer()), () -> updatePlayer(e.getPlayer()));
            }
        }, plugin);
        
        try(Connection con = database.getConnection()) {
            con.prepareStatement("CREATE TABLE IF NOT EXISTS players (id TEXT UNIQUE, name TEXT);").executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public void addAllOfflinePlayers()
    {
        if(Bukkit.getOfflinePlayers().length == 0)
        {
            return;
        }
        
        Stream.of(Bukkit.getOfflinePlayers()).forEach(op -> 
        {
            try(Connection con = database.getConnection(); PreparedStatement ps = con.prepareStatement(ADD_PLAYER)) {
                ps.setString(1, op.getName());
                ps.setString(2, op.getUniqueId().toString());
                ps.executeUpdate();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, null, ex);
            } 
        });
    }
    
    public void addPlayer(final OfflinePlayer op)
    {
        UUID uid = op.getUniqueId();
        String name = op.getName();
        TaskUtil.scheduleAsync(() -> addPlayer(uid, name));
    }
    
    private void addPlayer(final UUID u, final String name)
    {
        try (Connection con = database.getConnection(); PreparedStatement ps = con.prepareStatement(ADD_PLAYER)) {
            ps.setString(1, name);
            ps.setString(2, u.toString());

            ps.executeUpdate();
        } catch(final SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public void updatePlayer(final OfflinePlayer op)
    {
        UUID UID = op.getUniqueId();
        String name = op.getName();
        this.getPlayerUUID(name, false, oldUID -> 
        {
            if(oldUID == null)
            {
                String oldName = getName(UID);
                if(oldName != null)
                {
                    removePlayer(oldName);
                }

                addPlayer(UID, name);
            }
            else if(!oldUID.equals(UID))
            {
                updatePlayer(oldUID, UID, name);
                TaskUtil.scheduleSync(() -> addPlayer(Bukkit.getOfflinePlayer(oldUID)));
            }
        });
    }
    
    
    private void removePlayer(final String name)
    {
        try (Connection con = database.getConnection(); PreparedStatement ps = con.prepareStatement(REMOVE_NAME_PLAYER)) {
            ps.setString(1, name);
            ps.executeUpdate();
        } catch(final SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }    
    
    
    private void removePlayer(final UUID u)
    {
        try (Connection con = database.getConnection(); PreparedStatement ps = con.prepareStatement(REMOVE_UUID_PLAYER)) {
            ps.setString(1, u.toString());
            ps.executeUpdate();
        } catch(final SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }    
    
    
    public void getPlayerUUID(final String name, final boolean sync, final Consumer<UUID> consumer)
    {
        TaskUtil.scheduleAsync(() -> 
        {
            UUID u = this.getUUID(name);
            Util.ifCheck(sync, () -> TaskUtil.scheduleSync(() -> consumer.accept(u)), () -> consumer.accept(u));
        });
    }
    
    
    private @Nullable UUID getUUID(final String name)
    {
        try(Connection con = database.getConnection(); PreparedStatement ps = con.prepareStatement(GET_UUID)) {
            ps.setString(1, name);
            
            ResultSet set = ps.executeQuery();
            return !set.next() ? null : UUID.fromString(set.getString("id"));
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    
    private @Nullable String getName(final UUID u)
    {
        try(Connection con = database.getConnection(); PreparedStatement ps = con.prepareStatement(GET_NAME)) {
            ps.setString(1, u.toString());
            
            ResultSet set = ps.executeQuery();
            return !set.next() ? null : set.getString("name");
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private void updatePlayer(final UUID oldUID, final UUID UID, final String name)
    {
        try(Connection con = database.getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE)) {
            ps.setString(1, name);
            ps.setString(2, UID.toString());
            ps.setString(3, oldUID.toString());
            
            ps.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
