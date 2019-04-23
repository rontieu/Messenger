/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author James && Ron
 */
public class MessengerDatabaseManager {
    
    // Class variable
    //private String database_path;
    private Connection c;         // Connection to messenger database
    
    // Constructor
    public MessengerDatabaseManager (){
    }
    
    /**
     * Method: addUser()
     * @param username 
     * Usage: Add new user to the RCCH database user table
     */
    public void addUser(String username){
        try{
            // Geneate sql query to add user
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:D://Users/James/Documents/CPS/Database/RCCH.db");
            Statement s = c.createStatement();
            String sql = "INSERT INTO User( Name ) VALUES('"+username+"')";
            s.executeUpdate(sql);
            s.close();
            c.close();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Method: deleteUsesr()
     * @param username 
     * Usage: Remove specified user from the RCCH database.
     */
    public void deleteUser(String username){
        try{
            // Geneate sql query to add user
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:D://Users/James/Documents/CPS/Database/RCCH.db");
            Statement s = c.createStatement();
            String sql = "DELETE FROM User WHERE Name = '"+username+"'";
            s.executeUpdate(sql);
            s.close();
            c.close();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Method: addMessage
     * @param username
     * @param body
     * @param timestamp 
     * Usage: Add specified message information as a new message entry in the RCCH database
     */
    public void addMessage (String username, String body, String timestamp){
        try{
            // Geneate sql query to add user
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:D://Users/James/Documents/CPS/Database/RCCH.db");
            Statement s = c.createStatement();
            String sql = "INSERT INTO Message(UserName, Body, Timestamp) VALUES ('"+username+"', '"+body+"', datetime('"+timestamp+"'))";
            s.executeUpdate(sql);
            s.close();
            c.close();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Method: getUsers
     * @return 
     * Usage: Get a list of all users online as an ArrayList<String>
     */
    public ArrayList<String> getUsers(){
        
        ArrayList<String> users = new ArrayList<String>();
        try{
            // Geneate sql query to add user
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:D://Users/James/Documents/CPS/Database/RCCH.db");
            Statement s = c.createStatement();
            String sql = "SELECT Name FROM User";
            // Execute Query
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()){
                String username = rs.getString("Name");
                // Add current username to the list
                users.add(username);
            }
            s.close();
            c.close();
            
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        // Return populated usernames
        return users;
    }
    
}
