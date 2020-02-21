/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fallintooblivion;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.FileReader;
import java.io.IOException;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Asus
 */
public class SHA256 {
    
    public SHA256(){}
    
    public String getHash_File(FileReader data) throws NoSuchAlgorithmException,IOException 
    {
        char[] buff = new char[1];
 		
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        while(data.read(buff) > 0)
                md.update( (byte)buff[0] );

        byte[] h= new byte[15]; // 16bytes --> 128bits

        h = md.digest();
        String hash = DatatypeConverter.printHexBinary(h);
        
        return hash;
    }
    
    
     public String getHash_String(String data) throws NoSuchAlgorithmException
    {
 		
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());

        byte[] h= new byte[15]; // 16bytes --> 128bits

        h = md.digest();
        String hash = DatatypeConverter.printHexBinary(h);
        
        return hash;
    }
    
    public String getFirst128bits(String hash)
    {
        String[] arr= new String[hash.length()];
        for (int i=0; i< hash.length();i++)
        {
            arr[i]= String.valueOf(hash.charAt(i));
        }
        
        String left_bits="";
        for (int i=0; i< hash.length();i++)
        {
            if(i==32)
                break;

            left_bits=left_bits + arr[i];
        }
        return left_bits.toString();
 
    }
    
    public String getSecond128bits(String hash)
    {
        String[] arr= new String[hash.length()];
        for (int i=0; i< hash.length();i++)
        {
            arr[i]= String.valueOf(hash.charAt(i));
        }
        
        String right_bits="";
        for (int i=32; i< hash.length();i++)
        {
            right_bits=right_bits + arr[i];
        }
        return right_bits.toString();
    }
    
    
    
    
    
    
}