package amt.du.clientwebsocket.cryptoProcessor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CyrptoProcessor {
	private static Logger lg = Logger.getLogger(CyrptoProcessor.class);
	
	@Value("${cbwsi.request.privatekey}")
	private static String cbPrivateKey;
	

	
	public static String generateRSAPrivateKeyKey() throws NoSuchAlgorithmException, NoSuchProviderException{
		final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        final KeyPair keyPair = keyGen.generateKeyPair();
        String publicKey = DatatypeConverter.printBase64Binary(keyPair.getPublic().getEncoded());
        String privateKey =  DatatypeConverter.printBase64Binary(keyPair.getPrivate().getEncoded());
		return privateKey;
	}
	
	public static String AESDecrypt(String CipherText,String keyStr)
	
	{  
	
		String plainText=null;
	  try{
	    byte[] decodedKey = DatatypeConverter.parseBase64Binary(keyStr);
	    SecretKey skey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
	    final Cipher cipher = Cipher.getInstance("AES");
	    cipher.init(Cipher.DECRYPT_MODE, skey);
	   

		byte[] buf = new byte[128];
		int bufl;
		//byte[] text = Base64.decodeBase64(CipherText);
		byte[] text =Base64.getDecoder().decode(CipherText);
		ByteArrayInputStream bis = new ByteArrayInputStream(text);
		ByteArrayOutputStream cos = new ByteArrayOutputStream();
		
	
		
		while ((bufl = bis.read(buf)) != -1) {			
			cos.write(cipher.doFinal(copyBytes(buf, bufl)));				
		
		}	
		
		cos.flush();
		byte[] decrypted = cos.toByteArray();
		bis.close();
		cos.close();
		lg.info("decrypted dd"+new String(decrypted));
		lg.info("UTF FOrmat"+new String(decrypted, "UTF-8"));
		plainText = new String(decrypted, "UTF-8");
	  }catch(Exception e){
		  e.printStackTrace();
	  }
		return plainText;
	}

	public static String rsaDecrypt(String message){
		
		//String privatekeystring="MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC/5sQXxpKaL5XR5nqhWoyDZACwFRgseXfAG9V0BiGEw1Q2WYLxoDpnGdGa/xabh64JpPOqP4uMrfmoL1ZiqT/SPxNHfP95iXmidnFbfOFzRekY1dPIGlGAKnfoluQ4Ua7qDJz5uCRtw6e4tyCxQV0wfr6G2nsvpfEFDPOOxqfli+vfjuUXzqqsosP5WX34souRp1h9Oy3II2xw766gLI/ztWU/uQPir05jBU584xeFRuZIRvCwjArXe0iFDCAmXcl48Llo0POnvQ4R9foMACEyl5MGmixE0EqwCd+v+gUO/S0P2WoSDMtvHFQhLL9bsaQBSfCpSeehJfLQm7M6DdzHAgMBAAECggEATkomPBDuJGs/cyfVZdicNMpqoWupthNEMKLqXXBW4mQSCi5QhqU4znExG0vwfDlJXnPmWNGd7CHaFaxiUNP9zpefFO5BB+N3NTmfG5UCIVYnN9OcmClskJ5C1yggKX3hn2jP7e0bJ5DH70r+0nH5Q9yhfEDMOu83OjlDcD9JphLPGl62vep1dWBmzhj9m38gIpB1/1atFbk9mIT+/XiX7jO96OrxOgQjLyxpWfwxoz9lM4AcNdAs2cvYPfUVpM/kVdBlRiZa6yxUdaSvM6HminXkLqm4v8yIVblH7lUb04onb+7yL6c1l8jov/1HUYmyujsAa0IMkL80RC5TbLwtyQKBgQDvwFFwVZhAyCkRwz0Com5wc2yKfCwuPly5cYAKECpYI828M7X+K8/xjYU8Ou2rigt6+G1C06OYdfyoUDFP2EUFosUThVJThY+G/m58WEezfK1MSW3yLPczX5E4PD2/XQ+96Dkuv/Ghdc2PcK1AMwJWKiacGxKrNdUMbM5TpuoVFQKBgQDM6EEJNYTxL2sS4f/Cp5PM0ZtcUzj9LdEzHDZN6oq4qVTXEQBtGlTcrEtgX8xF7Va9wGEbbW9jDUwXBXnL7i5uZPYWBdo1Er2wRGDUZfj178fgr52nrkO7urxLHe+Nlel1/YWmrTcyS1vEBSrQkK9+NnKX6VCjtqlydIOai4gZawKBgQC3537Qu2eZsbAHJwsCnVQ3v+IvbEZ7hmyb9xsvHO1ORNCIn+XnPPY6JMNhyxYKck6SGkO9chhYV5DiDvl0dMGlLBEeAzN+S8In270mdpUxcd6A2QaUMLfCWXlMeLEovBraImrSmHuhzXpLoDPVPgt5ysVpEz9Dw3xUFpZCBpQibQKBgQDE9AT3JhMnhG2F0+bzIgIFn/+WCYRAjpaDav2jfOpjvpcGtZfURrgE+DLnao09NXjnoI92PoUPD0vw4NmGM+U7RR3oYy2vT9k2ITN1VKKtOhHEHOkLsIv4WO/9v8NvMwYY3Ftnrxzb4eVNj63slPWp+gnBjIhvI/bRRb7tgGqMCwKBgFrYI3xo+ayKafjEswZYDL9KGBoPKstfIpRy1/7RsodcNRZxJQ7KXlO+Wy6yNWezw5/42NAtxYCZJwvWerpVgTWJqsxdUJ8r/flSnRGilRs65ny11c4GlnLJGze+023jrlKGgDrighvNK0QjNgKDUyjwYsaICjHhZ61KO9WcduDz";
		//byte[] privateKeyBytes = Base64.decodeBase64(privatekeystring);
		byte[] privateKeyBytes =Base64.getDecoder().decode(cbPrivateKey);
		byte[] dectyptedText = null;
		try {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA/ECB/NOPADDING");
	    EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
	    PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
		String stringForDecryption="";
		
		   
		      // get an RSA cipher object and print the provider
		      final Cipher cipher = Cipher.getInstance("RSA");

		      // decrypt the text using the private key
		      cipher.init(Cipher.DECRYPT_MODE, privateKey);
		     // dectyptedText = cipher.doFinal(Base64.decodeBase64(stringForDecryption));
		      dectyptedText = cipher.doFinal(Base64.getDecoder().decode(stringForDecryption));
		    } catch (Exception ex) {
		      ex.printStackTrace();
		    }

		    return new String(dectyptedText);
	}
	
	
	

	private static byte[] copyBytes(byte[] arr, int length) {
		
		try {
			byte[] newArr = null;

			if (arr.length == length) {
				newArr = arr;
			} else {
				newArr = new byte[length];
				for (int i = 0; i < length; i++) {
					newArr[i] = (byte) arr[i];
				}
			}
			return newArr;
		} catch (Exception e) {
			return null;
		}
	}
	

	
	public static String generateHash(String KEY, String VALUE, String SHA_TYPE){
		
		try {
			SecretKeySpec signingKey = new SecretKeySpec(
					KEY.getBytes("Cp1256"), SHA_TYPE);
			Mac mac = Mac.getInstance(SHA_TYPE);
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(VALUE.getBytes("Cp1256"));
			byte[] hexArray = { (byte) '0', (byte) '1', (byte) '2', (byte) '3',
					(byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8',
					(byte) '9', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd',
					(byte) 'e', (byte) 'f' };
			byte[] hexChars = new byte[rawHmac.length * 2];
			for (int j = 0; j < rawHmac.length; j++) {
				int v = rawHmac[j] & 0xFF;
				hexChars[j * 2] = hexArray[v >>> 4];
				hexChars[j * 2 + 1] = hexArray[v & 0x0F];
			}

			lg.info(new String(hexChars));
			return new String(hexChars);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	
	}
	
	
	public void validateMessage(String message){
		
	}
	
	public void decryptMessage(String message){
		
	}
	
	public static String generateAesKey() throws NoSuchAlgorithmException, NoSuchProviderException{
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128);
        SecretKey key = generator.generateKey();
        String aesKey= DatatypeConverter.printBase64Binary(key.getEncoded());
        return aesKey;
 }
 public static String aesEncrypt(String data, String secKey) { 

     String encryptedVal = null;

     try {
        byte[] decodedKey = DatatypeConverter.parseBase64Binary(secKey);
              // rebuild key using SecretKeySpec
              SecretKey skey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
         final Cipher c = Cipher.getInstance("AES");
         c.init(Cipher.ENCRYPT_MODE, skey);
         final byte[] encValue = c.doFinal(data.getBytes());
         encryptedVal = DatatypeConverter.printBase64Binary(encValue);
     } catch(Exception ex) {
         lg.info("The Exception is=" + ex);
     }

     return encryptedVal;
 }
 public static String rsaEncrypt(String message,String keystring){
        //String publickeystring="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv+bEF8aSmi+V0eZ6oVqMg2QAsBUYLHl3wBvVdAYhhMNUNlmC8aA6ZxnRmv8Wm4euCaTzqj+LjK35qC9WYqk/0j8TR3z/eYl5onZxW3zhc0XpGNXTyBpRgCp36JbkOFGu6gyc+bgkbcOnuLcgsUFdMH6+htp7L6XxBQzzjsan5Yvr347lF86qrKLD+Vl9+LKLkadYfTstyCNscO+uoCyP87VlP7kD4q9OYwVOfOMXhUbmSEbwsIwK13tIhQwgJl3JePC5aNDzp70OEfX6DAAhMpeTBposRNBKsAnfr/oFDv0tD9lqEgzLbxxUISy/W7GkAUnwqUnnoSXy0JuzOg3cxwIDAQAB";
        byte[] cipherText = null;
        try{
        byte[] publicKeyBytes = org.apache.commons.codec.binary.Base64.decodeBase64(keystring);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubkey = keyFactory.generatePublic(spec);
 
       // get an RSA cipher object and print the provider
       final Cipher cipher = Cipher.getInstance("RSA");
       // encrypt the plain text using the public key
       cipher.init(Cipher.ENCRYPT_MODE, pubkey);
       cipherText = cipher.doFinal(message.getBytes());
        } catch (Exception e) {
              e.printStackTrace();
            } 
       return org.apache.commons.codec.binary.Base64.encodeBase64String(cipherText); 
  
 }
 
 public static String sign(String plainText, String privatekeystring) throws Exception {
        
        byte[] privateKeyBytes = org.apache.commons.codec.binary.Base64.decodeBase64(privatekeystring);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
     EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
     PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        
        
        
     Signature privateSignature = Signature.getInstance("SHA256withRSA");
     privateSignature.initSign(privateKey);
     privateSignature.update(plainText.getBytes("UTF-8"));

     byte[] signature = privateSignature.sign();
     
     return DatatypeConverter.printBase64Binary(signature);
}
 public static void verifysign(String plainText,String signedData, String publicKeyString) throws Exception {
        
        byte[] privateKeyBytes = org.apache.commons.codec.binary.Base64.decodeBase64(publicKeyString);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
     EncodedKeySpec privateKeySpec = new X509EncodedKeySpec(privateKeyBytes);
     PublicKey  publicKey = keyFactory.generatePublic(privateKeySpec);
        
     Signature publicSignature = Signature.getInstance("SHA256withRSA");
     publicSignature.initVerify(publicKey);
     publicSignature.update(plainText.getBytes("UTF-8"));
     byte[] signatureBytes = org.apache.commons.codec.binary.Base64.decodeBase64(signedData);
     publicSignature.verify(signatureBytes);
    

}


	
}
