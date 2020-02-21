/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fallintooblivion;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Vector;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import javax.xml.bind.DatatypeConverter;
/**
 *
 * @author jferr
 */
public class Daemon extends Thread {
    public File f = null;
    public String[] paths;
    
    public Daemon(){
        setDaemon(false);
    }
    
    @Override
    public void run(){       
        System.out.println("Thread");
      
        try{ 
            //Pasta que está a ser monotorizada
            FileReader dir = new FileReader("Directory.txt");
            String s="";
            char[] buff = new char[1];
            while(dir.read(buff)>0){
                s += buff[0];
            }
            
            while(true) {
                try{
                    Thread.sleep(10000);
                }catch(InterruptedException x){
                    System.out.println(x.getMessage());
                }
                
                //Flag acionada para a thread
                
                int flag = FallIntoOblivionController.getFlagMonotorizing();
                if(flag==1){
                    break;
                }
                        
                //coloca o nome dos ficheiros que estão na diretoria no vetor
                File f = new File(s);
                Vector<String> v=new Vector<String>();
                paths = f.list();
                for (String path : paths) {

                    v.add(path);
                    
                    //para remover os que já foram cifrados
                    for (int i = 0; i < v.size(); i++) {
                        String ext = FilenameUtils.getExtension(v.elementAt(i));
                        if(ext.equals("aes")){
                            v.remove(i);
                        }
                    }
                    
                    
                }
                
                System.out.println("Benfica!");
                monotorizing(v);
            }
            
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        System.out.println("Exit thread");
    }
    
        public static void monotorizing(Vector<String> files) throws SQLException, NoSuchAlgorithmException, FileNotFoundException, IOException, Exception{

        for(int i =0; i<files.size();i++){
            String ext = FilenameUtils.getExtension(files.elementAt(0));

            if(!ext.equals("aes")){
                
                // gerar iv
                SecureRandom random = new SecureRandom();
                byte[] iv = new byte[16];
                random.nextBytes(iv);
                
                //gerar pin e salt
                random = new SecureRandom();
                byte[] keyAux = new byte[16];
                random.nextBytes(keyAux);
                
                Long l = ByteBuffer.wrap(keyAux).getLong();
                //se o for negativo
                if(l<0){
                    l=l*(-1);
                }
                
                String sAux = String.valueOf(l);
                
                //Partir String
                String[] sAuxSplit = new String[sAux.length()];
                for(int j=0; j<sAux.length();j++){
                    sAuxSplit[j]=String.valueOf(sAux.charAt(j));
                }
                
                String pin = "";
                String salt = "";
                for(int j=0;j<16;j++){
                    if(j<4){
                        pin += sAuxSplit[j];
                    }
                    else{
                        salt += sAuxSplit[j];
                    }
                }
                
                //Mostra o Pin salt iv 
                System.out.println("O pin do ficheiro "+files.elementAt(0)+" é:"+pin+". É favor guarda-lo para reaver o ficheiro. iv=" +iv);
                System.out.println("salt="+salt);
                
                //Concatenação pin e salt 
                String pinMaisSalt = pin + salt;
                
                //SHA256 do pin mais salt, chave simétrica
                SHA256 sha = new SHA256();
                String hash = sha.getHash_String(pinMaisSalt);
                String chave = sha.getFirst128bits(hash);
                
                //abrir pop-up com o pin
                PinController.setPin(pin);
                PinController.setFile(files.elementAt(0));
                new Thread(() -> {
                    Platform.runLater(() -> {
                        try {
                            FXMLLoader loader = new FXMLLoader();
                            Parent root = loader.load(Daemon.class.getResource("pin.fxml"));
                            
                            Scene scene = new Scene(root);
                            Stage stage = new Stage();
                            stage.setTitle("PIN");
                            stage.setScene(scene);
                            stage.show();
                            
                        } catch (IOException ex) {
                            Logger.getLogger(Daemon.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    });
                }).start();

                //Cifrar o ficheiro com aes128
                FileReader directory = new FileReader("Directory.txt");
                char[] buff2 = new char[1];
                String dir = "";
                while(directory.read(buff2)>0){
                    dir += buff2[0];
                }
                directory.close();
                
                //obter o conteudo do ficheiro
                String name = files.elementAt(0);
                FileReader file = new FileReader(dir+"\\"+name);
                String plainText="";
                char[] buff = new char[1];
                while(file.read(buff)>0){
                    plainText += buff[0];
                }
                file.close();
                
                //cifrar conteudo
                AES_CBC aes = new AES_CBC(plainText,chave,iv);
                byte[] cipherText = aes.encrypt();
               
                // apagar ficheiro .txt
                File file1 = new File(dir+"\\"+name);
                int index = file1.getName().indexOf(".");
                String fname = file1.getName().substring(0, index);
                file1.delete();
                
                //escrever cifra no ficheiro .aes
                FileOutputStream fout = new FileOutputStream(dir+"\\"+fname+".aes");
                fout.write(cipherText);
                fout.close();
                
                //Calcular o hmac
                File cripto = new File(dir+"\\"+fname+".aes");
                Hmac hmac = new Hmac();
                String sHmac = hmac.calcHMAC(cripto,sha.getSecond128bits(hash));
                Hmac hmac2 = new Hmac();
                System.out.println(sHmac);

                
                
                //Guardar nome do ficheiro, extensão, salt e iv e o hmac na BD
                try{
                    //Criar bd se não existir
                    File fBD = new File("oblivion.db");
                    SQLiteBD bd = new SQLiteBD();
                    if(!fBD.exists()){
                        
                        bd.createBD();
                    }
                 
                    Statement stmt=bd.returnStmt();
                    
                    //passar o iv para hexadecimal
                    String sIv = DatatypeConverter.printHexBinary(iv);
                    
                    String insert = "INSERT INTO File VALUES('"+fname+"','."+ext+"',"+salt+","+"'"+sIv+"'"+",'"+sHmac+"');";
                    stmt.executeUpdate(insert);
                    
                    bd.closeBD();
                    
                }catch(Exception e){
                    System.out.println(e.getClass().getName() +": "+e.getMessage());
                    System.exit(0);
                }
                
                //apagar nome do ficheiro no vetor    
                files.removeElementAt(0);

               
            }
        }
    }
}
