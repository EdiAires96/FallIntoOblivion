/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fallintooblivion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jferr
 */

public class FallIntoOblivionController implements Initializable {

    @FXML
    private TextField TF1;
    
    private static int flagMonotorizing=0;
    
    @FXML
    private Button Button_monotorizar;
    
    @FXML
    private Button Button_stop;
    
    @FXML
    private void help(ActionEvent event){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.getDialogPane().setMinWidth(750);
            alert.setTitle("Fall Into Oblivion");
            alert.setHeaderText("Ajuda");
            alert.setContentText("Este programa permite ao utilizador a monitorização de uma pasta, de 10 em 10 segundos, "
                    + "para cifrar ficheiros contidos na mesma. É associado um PIN a cada ficheiro que mais tarde possibilita a "
                    + "recuperação do ficheiro atribuído. O utilizador dispõe de 3 tentativas de introdução do PIN para reaver "
                    + "o ficheiro em causa, se exceder as tentativas que lhe são concedidas, o ficheiro será eliminado.\n\n"
                    + "Se o utilizador pretender mudar a pasta a monitorizar, é necessário alterar o caminho da pasta no ficheiro"
                    + " \"Directory.txt\", na pasta do programa.\n Ex.: C:\\Users\\Asus\\Desktop\\FallIntoOblivion\n"
                    + "Pasta a ser monitorizada será neste caso : FallIntoOblivion\n\n"
                    + "Botão \"Monitorizar\"\n A acção que vai preceder a ação deste botão será a monitorização da pasta acima referida.\n"
                    + "Este método irá mostrar o PIN respetivo de cada ficheiro não cifrado e que se encontra na pasta. Recomenda-se que o PIN "
                    + "seja memorizado ou guardado pelo utilizador. O PIN irá ser mais tarde utilizado para a recuperação do ficheiro de forma segura.\n\n"
                    + "Botão \"Reaver Ficheiro\"\nRedirecionamento para uma janela que irá permitir a recuperação do ficheiro.\n\n"
                    + "Botão \"Parar de Monitorizar\"\nTerminação da tarefa de monitorização da pasta escolhida.");

            alert.showAndWait();
    }
    
    @FXML
    public void stopMonotorizing(ActionEvent event){
        flagMonotorizing=1;
        Button_monotorizar.setDisable(false);
        Button_stop.setDisable(true);
    }

    public static int getFlagMonotorizing() {
        return flagMonotorizing;
    }

    public static void setFlagMonotorizing(int flagMonotorizing) {
        FallIntoOblivionController.flagMonotorizing = flagMonotorizing;
    }
    
    
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        int flag=0;
        System.out.println("Fall Into Oblivion");
              
        File f2 = new File("Directory.txt");
        
        if(!f2.exists()){
            System.out.println("Insira o caminho onde deseja colocar a diretoria: ");
            String dir = TF1.getText();

            int index = dir.indexOf('\\');
            String caminho = dir.substring(0, index);
            File f = new File(caminho);
            if(!f.exists()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("File Into Oblivion");
                alert.setHeaderText("Caminho inválido");
                alert.setContentText("O caminho é inválido. Certifique-se que a pasta de raiz é válida.\n\n Exemplo: C:\\User\\Ambiente de trabalho");
                alert.showAndWait();
                flag=1;
            }
            if(flag==0){
                try{
                    try (FileOutputStream fileDir = new FileOutputStream(f2)) {
                        fileDir.write(dir.getBytes());              
                        Files.createDirectories(Paths.get(dir));
                    }
                }catch(IOException e){
                    System.out.println(e.getMessage());
                }
            }
        }
        if(flag==0){
            flagMonotorizing=0;
            Daemon t = new Daemon();
            t.start();
            Button_monotorizar.setDisable(true);
            Button_stop.setDisable(false);
        }
        
    }    
    
    @FXML
    private void recoverFromOblivion(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("RecoverFromOblivion.fxml"));

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Recover From Oblivion");
        stage.setScene(scene);
        stage.show();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Se existir o ficheiro com a diretoria escreve no textview e desativa a caixa  
        File f = new File("Directory.txt");
        FileReader directory = null;
        if(f.exists()){
            try{
                directory = new FileReader("Directory.txt");
                char[] buff = new char[1];
                String dir = "";
                while(directory.read(buff)>0){
                    dir += buff[0];
                }
                directory.close();
                TF1.setText(dir);
                TF1.setDisable(true);
            }catch(IOException e){
                System.out.println(e.getMessage()); 
            }
        }
        Button_stop.setDisable(true);
    }    
    
}
