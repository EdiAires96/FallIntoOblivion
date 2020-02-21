/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fallintooblivion;

import java.io.File;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Asus
 */
public class Hmac{
    
    public Hmac(){}
    
    public String calcHMAC(File input, String key) throws NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException, IOException{
    
        // key-> segundos 128bits do SHA
        // transformação da variavel key para uma varivel dp tipo SecretKey com o modo SHA256 
        SecretKey hkey = new SecretKeySpec(key.getBytes(),0,key.getBytes().length,"SHA256");
        
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(hkey);
        
        FileInputStream fin = new FileInputStream(input);
        byte[] b = new byte[1024];
        
        int ler = fin.read(b);
        while( ler > 0)
        {
            hmac.update(b,0,ler);
            ler= fin.read(b);
        }
        
        
        byte[] macbytes = hmac.doFinal();
        
        
        String sHmac = DatatypeConverter.printHexBinary(macbytes);
        
        fin.close();
        
        return sHmac;
    
    
    }
}
