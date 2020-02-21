/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fallintooblivion;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author jferr
 */
public class PinController implements Initializable {
    @FXML
    private Label lPin;
    @FXML
    private Label lFile;
    
    private static String pin;
    
    private static String file;
    
    
  
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lFile.setText(file);
        lPin.setText(pin);
    }    
    
    public static void setFile(String file){
        PinController.file=file;
    }
    
    public static void setPin(String pin){
        PinController.pin=pin;
    }

}
