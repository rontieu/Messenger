/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import rcch.*;
import ui.ui;

/**
 *
 * @author James & Calvin
 */
public class FXMLMessengerController {
        /**
     * Messenger window controller fields
     */
    @FXML
    private JFXTextField tf_chatInput;      // Messager input textfield reference
    @FXML
    private TextArea chat_area;             // Chat room messenger area reference
    private ui application;
    
    
    /**
     * Method: Send()
     * @param event 
     * Usage: Event handle to send message to chat room members
     */
    @FXML
    public void Send(KeyEvent event){
        if (event.getCode().equals(KeyCode.ENTER)){
            // Display input text at system out for debug
            //System.out.println(tf_chatInput.getText());
            String msg = tf_chatInput.getText();
            // Clear Input text field
            tf_chatInput.setText("");
            // Send msg content to messenger application server
            //System.out.println(msg);
            //System.out.println(application+"");
            application.send(msg);
        }
    }
    
    @FXML
    /**
     * Method: chatAreaSetup
     * Usage: FXML action reference
     */
    public void chatAreaSetup(){
        // Configure text area to disable inline editing
        chat_area.setEditable(false);

    }
    
    @FXML
    /**
     * Method updateChat(s)
     * @param s
     * Usage: Update chat area of chat room window with provided string
     */
    public void updateChat(String s){
        // Update chat room message area
        chat_area.appendText(s+"\n");
    }
    
    /**
     * Method: setApp()
     * @param app 
     * Usage: Set/link ui application using current messenger controller
     */
    public void setApp(ui app){
        // Set application for current controller
        this.application = app;
        //System.out.println(app);
    }
}
