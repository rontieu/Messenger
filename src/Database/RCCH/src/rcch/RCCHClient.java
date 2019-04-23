/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rcch;

import java.io.PrintWriter;
import java.util.Scanner;
import ui.ui;

// Networkc package 
import java.net.Socket;

// Local data time class
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author James & Hassan
 */
public class RCCHClient {
    
    // class variables
    String username;                // Client username
    String server_address;          // IP address of RCCH server
    int server_socket_num = 59001;  // Network socket number of RCCH server
    Socket server_socket;           // Network socket of RCCH server
    Scanner in;                     // Client-Server connection io
    PrintWriter out;                // Client-Server connection io
    //FXMLMessengerController messenger_controller;
    private ui application;
    private int client_run = 1;     // Flg to exectue client function
    
    // Class constructor
    public RCCHClient (String server_address, String username){
        // Instantiate class variable
        this.server_address = server_address;
        this.username = username;
        // Establish connection with RCCH server
        connectToServer();
    }
    
    /**
     * Method: setApp()
     * @param app 
     * Usage: configure related application for current RCCHClient object
     */
    public void setApp(ui app){
        this.application = app;
        //System.out.println(app);
    }
    
    /**
     * Method: addUser
     * @param username
     * Usage: Request to add current user to RCCH database.
     */
    public void addUser(){
        // Compose request messege to add current user to user database
        String request_msg = "USER_REG//" + this.username;
        out.println(request_msg);
        
    }
    
    /**
     * Method: send
     * @param msg 
     * Usage: Send provided message string to server to be included in the chat room.
     */
    public void send(String msg){
        // Get Current timestamp
        LocalDateTime local_date_time_obj = LocalDateTime.now();
        DateTimeFormatter date_time_format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        String timestamp = ""+local_date_time_obj.format(date_time_format);
        // Compose request messege to send chat message to server
        String chat_msg = "MSG//"+msg+"//"+timestamp;
        // Send msg content to server
        out.println(chat_msg);
    }
    
    /**
     * Method: connectToServer
     * @param
     * Usage: establish connection to server and configure client server io
     */
    public void connectToServer(){
        try{
            // Initialize connection
            server_socket = new Socket(server_address, server_socket_num);
            // Initialize client-server io
            in = new Scanner(server_socket.getInputStream());
            out = new PrintWriter(server_socket.getOutputStream(), true);
            
        }
        catch(Exception e){
            System.out.println("[ERROR] Unable to connect to RCCH server host.");
        }
    }
    
    /**
     * Method: quit()
     * Usage: Remove current user info from RCCH Server and database
     */
    public void quit(){
        // Compose message to remove user info from server
        String request_msg = "CLS//" + this.username;
        out.println(request_msg);
    }
    
    /**
     * Method: serverListen()
     * @param
     * Usage: main Client script execution to constantly listen for
     */
    public void serverListen(){
        // Continusouly run until client is terminated
        serverHandle svh = new serverHandle(in, application);
        Thread t = new Thread(svh);
        t.start();
    }
    
    // Innert serverHandle class (use to run as parallel thread)
    private static class serverHandle implements Runnable {
        
        // Class variable
        private Scanner in;
        //private PrintWriter out;
        private ui client_ui;
        
        public serverHandle(Scanner in, ui client_ui){
            this.in = in;
            this.client_ui = client_ui;
            //System.out.println(client_ui);
        }
        
        // Thread function declaration
        public void run(){
            
            // Set run flag
            boolean client_run = true;
            // Continuously run until run flag termination 
            while(client_run && in.hasNextLine()){
                // Retrieve new server input
                String server_input = in.nextLine();
                System.out.println(server_input);
                // Split message
                String[] msg_content = server_input.split("//");
                // Determine msge type
                String msg_type = msg_content[0];
                switch(msg_type){
                    case "SYS":
                        // Get line content
                        String system_line = "[SYS] "+msg_content[1];
                        client_ui.updateChat(system_line);
                        break;
                    case "MSG":
                        // Get messege owner and content
                        String msg_timestamp = msg_content[3];
                        String msg_owner = msg_content[1];
                        String msg_line = msg_content[2];
                        // Display message content in chat window
                        String new_line = "["+msg_timestamp+"] "+msg_owner+": "+msg_line;
                        client_ui.updateChat(new_line);
                        break;
                    case "CLS":
                        System.out.println("END");
                        client_run = false;
                        break;
                    default:
                        System.out.println("[ERROR] unknown server response.");
                        break;
                }
            }
        }
        
        
    }
    
}
