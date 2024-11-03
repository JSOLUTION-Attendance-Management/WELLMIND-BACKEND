package site.wellmind.security.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.ByteBuffer;
import java.util.Base64;

public class EncryptionUtil {
    private static final String AES="AES";
    private static final SecretKey secretKey=createSecretKey();

    //암호화 키 생성
    private static SecretKey createSecretKey(){
        try{
            KeyGenerator keyGenerator=KeyGenerator.getInstance(AES);
            keyGenerator.init(128); //AES-128

            return keyGenerator.generateKey();
        } catch (Exception e){
            throw new RuntimeException("Error creating Secret Key",e);
        }
    }

    // 다양한 타입을 앟호화하는 메서드
    public static <T> String encrypt(T data){
        return encryptBytes(convertToBytes(data));
    }

    // 암호화
    public static <T> String encryptBytes(byte[] dataBytes){
        try{
            Cipher cipher=Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE,secretKey);
            byte[] encryptedData=cipher.doFinal(dataBytes);
            return Base64.getEncoder().encodeToString(encryptedData);
        }catch (Exception e){
            throw new RuntimeException("Error encrypting data",e);
        }
    }

    // 복호화
    public static String decrypt(String encryptedData){
        try{
            Cipher cipher=Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE,secretKey);
            byte[] decodedData=Base64.getDecoder().decode(encryptedData);
            byte[] originalData=cipher.doFinal(decodedData);
            return new String(originalData);
        }catch (Exception e){
            throw new RuntimeException("Error decrypting data", e);
        }
    }

    //복호화 메서드
    public static String decryptToString(String encryptedData){
        return new String(decryptedToBytes(encryptedData));
    }

    // 다양한 타입을 바이트 배열로 변환
    private static <T> byte[] convertToBytes(T data){
        if (data instanceof String) {
            return ((String) data).getBytes();
        } else if (data instanceof Integer) {
            return ByteBuffer.allocate(Integer.BYTES).putInt((Integer) data).array();
        } else if (data instanceof Long) {
            return ByteBuffer.allocate(Long.BYTES).putLong((Long) data).array();
        } else if (data instanceof Double) {
            return ByteBuffer.allocate(Double.BYTES).putDouble((Double) data).array();
        } else {
            throw new IllegalArgumentException("Unsupported data type for encryption");
        }
    }

    // 바이트 배열 복호화
    private static byte[] decryptedToBytes(String encryptedData){
        try{
            Cipher cipher=Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE,secretKey);
            byte[] decodedData=Base64.getDecoder().decode(encryptedData);

            return cipher.doFinal(decodedData);
        }catch (Exception e){
            throw new RuntimeException("Error decrypting data",e);
        }
    }

}
