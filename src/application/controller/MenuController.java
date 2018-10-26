
package application.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;


/**
 * FXML Controller class
 *
 * @author Amit Kohli
 */
public class MenuController{
    
    @FXML
    private Label username;
    
   
    @FXML
    private void closeApplication(MouseEvent event) {
      System.exit(0);  
    }
   
    public void setUsername(String username){
       this.username.setText("Hello, " +username);
    }
   
    @FXML
    public void initialize(){
      
    }
    
}
