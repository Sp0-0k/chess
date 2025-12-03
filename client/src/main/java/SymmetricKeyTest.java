import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.io.*;

public class SymmetricKeyTest {
    public static void main(String[] args) throws Exception {
        SecretKey key = createAesKey();
        IvParameterSpec initVector = createAesInitVector();

        var secretMessage = "toomanysecrets";

        var plainTextIn = new ByteArrayInputStream(secretMessage.getBytes());
        var cipherBytes = runAes(Cipher.ENCRYPT_MODE, plainTextIn, key, initVector);

        var cipherTextIn = new ByteArrayInputStream(cipherBytes);
        var plainTextBytes = runAes(Cipher.DECRYPT_MODE, cipherTextIn, key, initVector);

        System.out.printf("%s == %s%n", secretMessage, new String(plainTextBytes));
    }

    static SecretKey createAesKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }

    static IvParameterSpec createAesInitVector() {
        var ivBytes = new byte[16];
        new SecureRandom().nextBytes(ivBytes);
        return new IvParameterSpec(ivBytes);
    }

    static byte[] runAes(int cipherMode, InputStream in, SecretKey key, IvParameterSpec initVector) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(cipherMode, key, initVector);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] inputBytes = new byte[64];
        int bytesRead;
        while ((bytesRead = in.read(inputBytes)) != -1) {
            out.write(cipher.update(inputBytes, 0, bytesRead));
        }

        out.write(cipher.doFinal());
        return out.toByteArray();
    }
}

