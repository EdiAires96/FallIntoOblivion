package fallintooblivion;




import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.bind.DatatypeConverter;


public class AES_CBC{

	private static String plainText;
	private static byte[] cipherText;
	private static String symetricKey;
        private static byte[] iv; 

	//contrutor utizado na cifragem
	public AES_CBC(String plainText,String symetricKey,byte[] iv){
		this.plainText = plainText;
		this.symetricKey = symetricKey;
                this.iv=iv;
	}
        
        //construtor utilizado na decifragem
        public AES_CBC(byte[] cipherText, String symetricKey,byte[] iv){
            this.cipherText=cipherText;
            this.symetricKey = symetricKey;
            this.iv=iv;
            plainText= new String();
        }
        
        public static void setCipherText(byte[] cipherText) {
            AES_CBC.cipherText = cipherText;
        }
        
	public static byte[] encrypt() throws Exception{
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                SecretKey key = new SecretKeySpec(DatatypeConverter.parseHexBinary(symetricKey), "AES");
		cipher.init(Cipher.ENCRYPT_MODE,key,new IvParameterSpec(iv));
		cipherText=cipher.doFinal(plainText.getBytes("UTF-8"));
                return cipherText;
	}	

	public static byte[] decrypt() throws Exception{
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                SecretKey key = new SecretKeySpec(DatatypeConverter.parseHexBinary(symetricKey), "AES");
		cipher.init(Cipher.DECRYPT_MODE,key,new IvParameterSpec(iv));
		return cipher.doFinal(cipherText);
	}
}
