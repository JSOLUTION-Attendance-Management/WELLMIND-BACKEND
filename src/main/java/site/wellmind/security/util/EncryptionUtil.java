package site.wellmind.security.util;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class EncryptionUtil {
    private static final String AES = "AES";
    private static final String AES_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    @Value("${jwt.secret}")
    private static String secretKey;
    private static final byte[] IV = new byte[16]; // Example: Fixed IV (for testing)

    private static SecretKeySpec secretKeySpec;

    static {
        secretKeySpec = new SecretKeySpec(secretKey.getBytes(), AES);
    }

    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(IV));
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    public static String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] iv = extractIV(decoded);
            byte[] data = extractEncryptedData(decoded);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
            return new String(cipher.doFinal(data));
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }

    private static byte[] generateRandomIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    private static byte[] concatenate(byte[] iv, byte[] encryptedData) {
        ByteBuffer buffer = ByteBuffer.allocate(iv.length + encryptedData.length);
        buffer.put(iv);
        buffer.put(encryptedData);
        return buffer.array();
    }

    private static byte[] extractIV(byte[] combined) {
        return Arrays.copyOfRange(combined, 0, 16);
    }

    private static byte[] extractEncryptedData(byte[] combined) {
        return Arrays.copyOfRange(combined, 16, combined.length);
    }
}
