/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import controller.*;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.io.IOException;
import rcch.RCCHClient;
import java.time.LocalDateTime;
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;
import database.MessengerDatabaseManager;
import java.util.ArrayList;
import javafx.stage.Modality;

/**
 *
 * @author James & Calvin
 */
public class ui extends Application{
    
    // UI Variables
    private Stage app_window;
    private RCCHClient user;
    private FXMLLoginController login_controller;
    private FXMLMessengerController msg_controller;
    
    @Override
    // Configure GUI applicaiton start action
    public void start(Stage stage) throws Exception {
        
        app_window = stage;   //Set main app stage as primary stage
        // Load login screen scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLLogin.fxml"));
        Parent root = (Parent)loader.load();
        Scene login_scene = new Scene (root);
        // Setup login controller
        FXMLLoginController login_controller = (FXMLLoginController) loader.getController();
        // Set application for login controller
        login_controller.setApp(this);
        // Set window title and content
        stage.setTitle("RCCH Login");
        // Set login screne content
        stage.setScene(login_scene);
        // Fix stage size
        stage.setResizable(false);
        stage.show();
        
    }
    
    /**
     * Method: openChatRoom()
     * @param username
     * Usage: Update UI window to transition to chat room interface
     */
    public void openChatRoom(String username){
        try{
            // Load chatroom fxml as new root node
            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("FXMLMessenger.fxml"));
            Parent root2 = (Parent)loader2.load();
            // Setup messenger controller
            FXMLMessengerController msg_controller = (FXMLMessengerController) loader2.getController();
            msg_controller.setApp(this);
            this.msg_controller = msg_controller;
            
            // Initialize new user client
            this.user = new RCCHClient("127.0.0.1", username);
            //user.setMessengerController(msg_controller);
            user.addUser();
            user.setApp(this);

            // Get new chatroom scene for gui
            Scene chatroom_scene = new Scene(root2);
            // Initialize chatroom chat area
            msg_controller.chatAreaSetup();
            // Set new chatroom scene and update stage title
            app_window.setScene(chatroom_scene);
            app_window.setTitle("RCCH Chat Room");
            app_window.show();
            // Setup user client
            user.serverListen();
        }
        catch(IOException e){
            System.out.println("Unable to load messenger fxml");
        }
        
        app_window.setOnCloseRequest(new EventHandler<WindowEvent>() {
           public void handle(WindowEvent ew){
               user.quit();
           } 
        });
    }
    
    /**
     * Method: send()
     * @param msg 
     * Usage: invoke sending operation sequence 
     */
    public void send(String msg){
        // Use RCCH client send function
        user.send(msg);
    }
    
    /**
     * Method updateChat()
     * @param line
     * Usage: invoke update operation sequence
     */
    public void updateChat(String line){
        msg_controller.updateChat(line);
    }
    
    public boolean checkUser(String username){
        // Create new database handle
        MessengerDatabaseManager db_manager = new MessengerDatabaseManager();
        // Get array list of current user names
        ArrayList<String> usernames = new ArrayList<String>();
        usernames = db_manager.getUsers();
        if (usernames.contains(username)){
            // Open new window to warn user for false username
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle("[SYS] Warning");
            Label label1 = new Label("Invalid username, please try again");
            Scene username_warning = new Scene(label1);
            stage.setScene(username_warning);
            stage.show();
            return false;
        }else{
            return true;
        }
            
        
    }
    
    // Applicaiton main function
    public static void main(String[] args){
        launch(args);
    }
    
}
