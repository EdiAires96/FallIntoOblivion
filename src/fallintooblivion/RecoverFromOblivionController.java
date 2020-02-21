package fallintooblivion;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.FileUtils;

/**
 * FXML Controller class
 *
 * @author jferr
 */
public class RecoverFromOblivionController implements Initializable {
    @FXML
    private TextField TFNFile;
    
    @FXML
    private TextField TFPIN;
    
    @FXML
    private TextField TFDIR;
    
    @FXML
    private Button Bverify;
    
    @FXML
    private Button Brecover;
    
    private int count=3;
    
    private String sDir;
    
    private String nameWithoutExtension;
    
    @FXML
    private void help(ActionEvent event){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.getDialogPane().setMinWidth(750);
            alert.setTitle("Fall Into Oblivion");
            alert.setHeaderText("Ajuda");
            alert.setContentText("\"Nome do ficheiro\"\nIntrodução do nome do ficheiro a reaver. Após o utilizador clicar no botão "
                    + "\"Verificar\", como o próprio nome denota, irá ser feita uma verificação ao nome do ficheiro introduzido. "
                    + "Caso o nome inserido for incorreto, é apresentada uma mensagem de erro; caso contrário prossegue-se à próxima "
                    + "etapa de introdução de novos dados para a recuperação do ficheiro.\n\n"
                    + "\"Directoria de Destino\"\nIntrodução da directoria do destino em que o utilizador pretenda recuperar o ficheiro,"
                    + " ou seja, escolha da pasta em que o ficheiro decifrado/ original irá ser colocado.\nEx.: C:\\Users\\Desktop\\Ficheiros_Recuperados\n\n"
                    + "Se o PIN colocado for correcto, ou seja for o correspondido ao ficheiro a reaver,\nEntão após clicar no botão \"Recuperar\" a recuperação "
                    + "do ficheiro foi realizada com sucesso.\nSe o utilizador exceder as 3 tentativas de inserção do PIN, o ficheiro em questão irá ser removido permanentemente. ");

            alert.showAndWait();
    }
    
    @FXML
    public void verificar(ActionEvent event) throws IOException{
        
        //Vai buscar a diretoria
        File f = new File("Directory.txt");
        FileReader directory = new FileReader("Directory.txt");
        char[] buff = new char[1];
        String dir = "";
        while(directory.read(buff)>0){
            dir += buff[0];
        }
        directory.close();
        sDir=dir;
        
        // nome dado na textview
        String nfile = TFNFile.getText();
        
        //vê se o utilizador introduziu a extensão se introduziu ignora
        int index = nfile.indexOf('.');
        String aux=null;
        if(index!=-1){
            aux= nfile.substring(0, index);
            nameWithoutExtension = aux;
        }
        else{
            aux = TFNFile.getText();
            nameWithoutExtension = aux;
        }
        System.out.println(nameWithoutExtension);
        
        //verificar se o ficheiro existe senao abre caixa de aviso
        File file = new File(dir+"\\"+aux+".aes");
        if(file.exists() && !nfile.equals("")){
            TFDIR.setDisable(false);
            TFPIN.setDisable(false);
            Brecover.setDisable(false);
            TFNFile.setDisable(true);
            Bverify.setDisable(true);
        }
        else{
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Recover From Oblivion");
            alert.setHeaderText("Ficheiro não encontrado");
            alert.setContentText("O Ficheiro não foi encontrado. Certifique-se do nome do ficheiro e tente novamente.\n\n Exemplo: exemplo");

            alert.showAndWait();
        }
    }
    
    public void recuperar(ActionEvent event) throws NoSuchAlgorithmException, FileNotFoundException, IOException, InvalidKeyException, Exception{
        int flag = 0;
        int index = TFDIR.getText().indexOf('\\');
        String caminho = TFDIR.getText().substring(0, index);
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
            //Vai buscar o salt e o hmac á bd
            long lSalt=0;
            String sHmac=null;
            String sIv = null;
            String sExt = null;
            try{
                File fBD = new File("oblivion.db");
                SQLiteBD bd = new SQLiteBD();
                if(!fBD.exists()){

                    bd.createBD();
                }

                Statement stmt=bd.returnStmt();

                String qSaltHmac = "SELECT salt,hmac,iv,ext FROM File WHERE name='"+nameWithoutExtension+"';";
                ResultSet res = stmt.executeQuery(qSaltHmac);
                if(res.next()){
                    lSalt = res.getLong(1);
                    sHmac = res.getString(2);
                    sIv = res.getString(3);
                    sExt = res.getString(4);
                }
                bd.closeBD();
            }catch(Exception e){
                System.out.println(e.getClass().getName() +": "+e.getMessage());
                System.exit(0);
            }

            //concatenar pin com o salt
            String aux = TFPIN.getText();
            String pinMaisSalt = aux + lSalt;
            System.out.println(pinMaisSalt);

            //calcula sha
            SHA256 sha256 = new SHA256();
            String sha = sha256.getHash_String(pinMaisSalt);

            //faz o hmac do criptograma e verifica se é igual ao guardado na BD
            File fcriptograma = new File(sDir+"\\"+nameWithoutExtension+".aes");

            Hmac hmac = new Hmac();
            String res_hmac=hmac.calcHMAC(fcriptograma, sha256.getSecond128bits(sha));
            System.out.println(res_hmac);

            //Se não é igua mostra aviso senão desbloqueia os seguintes textviews
            if(!sHmac.equals(res_hmac)){
                count--;
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Recover From Oblivion");
                alert.setHeaderText("Pin Inválido");
                alert.setContentText("O pin que inseriu não é o pin do ficheiro. Tem " +count+" tentativas.");
                alert.showAndWait();

                // se o utilizador introduzir 3x mal o pin o ficheiro é eliminado. è mostrado um aviso ao utilizador
                if(count==0){
                    fcriptograma.delete();
                    
                    //apagar da bd
                    try{
                        File fBD = new File("oblivion.db");
                        SQLiteBD bd = new SQLiteBD();
                        if(!fBD.exists()){

                            bd.createBD();
                        }

                        Statement stmt=bd.returnStmt();

                        String del = "DELETE FROM File WHERE name='"+nameWithoutExtension+"';";
                        int line = stmt.executeUpdate(del);
                        System.out.println(line);
                        bd.closeBD();

                    }catch(Exception e){
                        System.out.println(e.getClass().getName() +": "+e.getMessage());
                        System.exit(0);
                    }
                    
                    Alert alert2 = new Alert(AlertType.INFORMATION);
                    alert2.setTitle("Recover From Oblivion");
                    alert2.setHeaderText("Tentativas Excedidas");
                    alert2.setContentText("Excedeu as 3 tentativas a que tinha direito.\nO Ficheiro foi eliminado.");
                    alert2.showAndWait();
                    Stage stage = (Stage) Brecover.getScene().getWindow();
                    stage.close();
                }
            }

            //Se forem iguais decifra o ficheiro e coloca o na diretoria desejada pelo utilizador
            else{

                //abre e lê o criptograma
                FileReader cripto = new FileReader(fcriptograma);
                char[] buff = new char[1];
                String criptograma = null;
                while(cripto.read(buff)>0){
                    criptograma += buff[0];
                }
                cripto.close();
                
                //apagar criptograma
                fcriptograma.delete();


                // passa o iv para bytes
                byte[] iv = DatatypeConverter.parseHexBinary(sIv);

                //decifra
                AES_CBC aes = new AES_CBC(criptograma,sha256.getFirst128bits(sha),iv);
                String textoLimpo = new String(aes.decrypt(),"UTF-8");

                // escreve no ficheiro 
                FileUtils.writeStringToFile(new File(TFDIR.getText()+"\\"+nameWithoutExtension+sExt), textoLimpo);

                //remover da bd
                try{
                    File fBD = new File("oblivion.db");
                    SQLiteBD bd = new SQLiteBD();
                    if(!fBD.exists()){

                        bd.createBD();
                    }

                    Statement stmt=bd.returnStmt();

                    String del = "DELETE FROM File WHERE name='"+nameWithoutExtension+"';";
                    int line = stmt.executeUpdate(del);
                    System.out.println(line);
                    bd.closeBD();

                }catch(Exception e){
                    System.out.println(e.getClass().getName() +": "+e.getMessage());
                    System.exit(0);
                }

                //mensagem de sucesso
                Alert alert2 = new Alert(AlertType.INFORMATION);
                alert2.setTitle("Recover From Oblivion");
                alert2.setHeaderText("Sucesso");
                alert2.setContentText("O ficheiro foi recuperado, na diretoria que inseriu.");
                alert2.showAndWait();        
                Stage stage = (Stage) Brecover.getScene().getWindow();
                stage.close();
            }
        }
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // não precisa
    }    
    
}
