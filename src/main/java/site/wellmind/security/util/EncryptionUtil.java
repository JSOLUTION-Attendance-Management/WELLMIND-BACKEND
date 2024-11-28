package site.wellmind.security.util;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.exception.GlobalException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
@Component
public class EncryptionUtil {
    private final String secretKey;
    private final String initVector;
    private SecretKeySpec secretKeySpec;
    private IvParameterSpec ivParameterSpec;

    public EncryptionUtil(@Value("${encryption.secret-key}") String secretKey,
                          @Value("${encryption.init-vector}") String initVector) {
        this.secretKey = secretKey;
        this.initVector = initVector;
        initialize();
    }
    private void initialize() {
        this.secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
        this.ivParameterSpec = new IvParameterSpec(initVector.getBytes());
    }
    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new GlobalException(ExceptionStatus.INTERNAL_SERVER_ERROR,"Error encrypting data");
        }
    }

    public String decrypt(String encryptedData) {
        if(encryptedData==null){
            return "";
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            return new String(cipher.doFinal(decoded));
        } catch (Exception e) {
            throw new GlobalException(ExceptionStatus.INTERNAL_SERVER_ERROR,"Error decrypting data");
        }
    }
}
