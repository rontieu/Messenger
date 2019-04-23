/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rcch;

// Application controller class
import controller.*;

// Netowrk IO class
import java.io.PrintWriter;
import java.util.Scanner;

// Networkc package import
import java.net.Socket;
import java.net.ServerSocket;

// Utility class
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import database.MessengerDatabaseManager;

/**
 *
 * @author James & Hassan
 */
public class RCCHServer {
    
    // Class variable
    private static Set<String> users = new HashSet<>();
    private static Set<PrintWriter> user_writers = new HashSet<>();
    private static MessengerDatabaseManager db_manager = new MessengerDatabaseManager();

    /**
     * Inner handler class used to manage each client
     */
    private static class Handler implements Runnable {
        
        // Class variabl
        private String username;        // Username of the connected client
        private Socket socket;          // Socket for client server connection
        private Scanner in;             // Client-server connection IO
        private PrintWriter out;        // Client-server connection IO
        
        // Class Constructor
        public Handler(Socket socket){
            // Setup network socket
            this.socket = socket;
            // Initialize client server IO stream 
            try{
                in = new Scanner(this.socket.getInputStream());
                out = new PrintWriter(this.socket.getOutputStream(), true);
            }
            catch(Exception e){
                System.out.println("Unable to establish IO stream for socket.");
            }
            
        }
        
        public void run(){
            
            boolean client_available = true;
            try{
                //System.out.println("Connected");
                // Continusouly check for client input
                while (client_available){
                    // Get client message
                    String client_input = in.nextLine();
                    String[] msg_content = client_input.split("//");
                    // Determine msg type
                    String msg_type = msg_content[0];
                    System.out.println("    "+client_input);
                    System.out.println("    Type:"+msg_type);
                    switch(msg_type){
                        case "USER_REG":
                            // Configure username for current user
                            this.username = msg_content[1];
                            // Register current user
                            db_manager.addUser(username);
                            synchronized(users){
                                if (!username.isEmpty() && !users.contains(username)){
                                    users.add(username);
                                    out.println("SYS// Welcome to RCCH Chat Room!");
                                    System.out.println("    User '"+username+"' registered.");
                                    //break;
                                }
                            }
                            // Notify all users new user has been added
                            for (PrintWriter writer : user_writers){
                                // Generate message
                                String msg = "SYS// User '"+username+"' has joined the chat.";
                                // Send message
                                writer.println(msg);
                            }
                            // Update list of available writers
                            user_writers.add(out);
                            break;
                        case "MSG":
                            // Extract message ontent
                            System.out.println("MSG:"+client_input);
                            String timestamp = msg_content[2];
                            String line = msg_content[1];
                            // Add message entry to database
                            db_manager.addMessage(username, line,timestamp);
                            // Send out message to all clients 
                            String new_line = "MSG//"+username+"//"+line+"//"+timestamp;
                            for (PrintWriter writer : user_writers){
                                writer.println(new_line);
                            }
                            break;
                        case "CLS":
                            // Send termination message
                            out.println("CLS//"+username);
                            // Remove current user from database and user list
                            db_manager.deleteUser(username);
                            users.remove(username);
                            user_writers.remove(out);
                            // Notify all users someone left the chat
                            for (PrintWriter writer : user_writers){
                                // Generate message
                                String msg = "SYS// User '"+username+"' has left the chat.";
                                // Send message
                                writer.println(msg);
                            }
                            // Un-set client availability
                            client_available = false;
                            break;
                        default:
                            System.out.println("["+username+"] Unknown user request.");
                            break;
                    }
                }
            }
            catch (Exception e){
                System.out.println("[SYSTEM] Error, server execution incomplete.");
            }
        }
    }
    
    // class main fucntion
    public static void main (String[] args) throws Exception {
        
        // Initialize RCCH Server
        System.out.println("[System] RCCH Server");
        System.out.println("    Status... Running");
        // Create pool of thread available to handle each user connection
        ExecutorService pool = Executors.newFixedThreadPool(500);
        // Listen for new connection from socket
        // Create new listener
        try(ServerSocket listener = new ServerSocket(59001)){
            while(true){
                pool.execute(new Handler(listener.accept()));
            }
        }

    }
}
