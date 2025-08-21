package utilities;

import java.security.Key;
import java.util.Base64;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class PasswordEncryption {

    private static final String ALGO = "AES";
    private static final byte[] keyValue = new byte[] { 
        'm', 'Y', 'p', 'U', 'b', 'l', 'I', 'c', 'k', 'E', 'y', 'n', 'A', 'e', 'E', 'M' 
    };

    public static String encrypt(String data) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedVal = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedVal);
    }

    public static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedVal = cipher.doFinal(decodedValue);
        return new String(decryptedVal);
    }

    private static Key generateKey() {
        return new SecretKeySpec(keyValue, ALGO);
    }

    public static void main(String[] args) {
        try {
            ConfigReader configReader = new ConfigReader();
            Properties prop = configReader.init_prop();

            String password = prop.getProperty("password_personal");
            String encrypted = encrypt(password);
            System.out.println("Plain Text : " + password + " | Encrypted : " + encrypted);

            String decrypted = decrypt(encrypted);
            System.out.println("Decrypted : " + decrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Constructor if needed (can be removed if not used)
    public PasswordEncryption() {
        // No WebDriver used
    }
}
