/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.parozzz.reflex.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Paros
 */
public interface IDatabase 
{
    public Connection getConnection() throws SQLException;
}
