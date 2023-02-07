package rs.cs.restaurantnea.general.IOData;

import rs.cs.restaurantnea.general.errorMethods;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class cryptMethods {
    public static String hashing(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256"); // Creates a message disgest with an instance of SHA-256
            byte[] digest = messageDigest.digest(str.getBytes(StandardCharsets.UTF_8)); // Uses the message digest to convert the plaintext, 'str', into a hashed byte array
            return bytesToHex(digest); // Due to byte arrays producing the same values but different outputs, the hashed byte array but converted into a string in the form of hex
        } catch (Exception e) {
            errorMethods.defaultErrors(e); // Outputs errors
            return null;
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length); // Creates a StringBuilder capable of converting the byte array.
        // The capacity of the StringBuilder is twice that of the hash length because each byte equates to 2 hex values
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]); // Converts the byte to its corresponding hex values
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static SecretKey key; // Allows for key to used outside of this class
    private static Cipher encryptionCipher;
    public static IvParameterSpec IV;
    public static byte[] iv;

    public static void initKeys() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES"); // Inits the key generator using the AES algortihm
            keyGenerator.init(256); // Specifies that keys will have a size of 256, the most secure value
            key = keyGenerator.generateKey(); // Generates a new key
        } catch (Exception e) {
            errorMethods.defaultErrors(e);
        }
    }
    public static void generateIV() {
        iv = new byte[16]; // This initialisation vector is public due to it being stored for decryption, storing it as a byte array makes it easier to convert to either a string or IvParameterSpec
        new SecureRandom().nextBytes(iv);
        IV = new IvParameterSpec(iv); // Creates an IV to be used in encryption of data that hasn't already been encrypted
    }
    public static String encrypt(String plaintext, String hashedEmail, boolean mode, IvParameterSpec initVector) {
        try {
            SecretKey secretKey;
            if (mode) { // Creates a new key entry in the keystore for data that has not already been encrypted
                setKeyValInKeyStore(key, hashedEmail);
                secretKey = key;
            } else { // Loads the key for data that needs to be re-encrypted, like a password change
                loadKeyStore();
                secretKey = loadKeyEntry(hashedEmail);
            }
            byte[] byteText = plaintext.getBytes(); // Converts plaintext to a byte array to allow for encryption
            encryptionCipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // Creates an encryption cipher with a set algorithm
            encryptionCipher.init(Cipher.ENCRYPT_MODE, secretKey, initVector); // Uses the same key and IV to encrypt batches of related data
            byte[] encryptedBytes = encryptionCipher.doFinal(byteText); // Encrypts data
            return Base64.getEncoder().encodeToString(encryptedBytes); // Returns encrypted data as a string
        } catch (Exception e) {
            errorMethods.defaultErrors(e);
            return null;
        }
    }

    public static String decrypt(String encryptedString, String hashedEmail, byte[] iv) {
        try {
            loadKeyStore();
            SecretKey secretKey = loadKeyEntry(hashedEmail); // Loads key with alias that corresponds to the current users userID
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedString); // Converts encrypted string to a byte array
            Cipher decryptionCipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // Creates decryption cipher with a set algorithm
            IvParameterSpec initVector = new IvParameterSpec(iv); // Uses the iv that was stored as a string in the DB to decrypt related data
            decryptionCipher.init(Cipher.DECRYPT_MODE, secretKey, initVector); // Inits the decryption cipher
            byte[] decryptedBytes = decryptionCipher.doFinal(encryptedBytes); // Decrypts the data
            return new String(decryptedBytes); // Returns the decrypted text as a string
        } catch (Exception e) {
            errorMethods.defaultErrors(e);
            return null;
        }
    }

    public static KeyStore keyStore;
    public static char[] passwordArray = "restaurantNEA".toCharArray(); // Default keystore password
    private static String keyStoreLoc = System.getProperty("user.dir")  + "\\src\\main\\resources\\rs\\cs\\restaurantnea\\data\\restaurantNEA.jks";

    public static void initKeyStore() {
        try {
            KeyStore newKeyStore = KeyStore.getInstance(KeyStore.getDefaultType()); // Creates a default keystore with a version of jks
            newKeyStore.load(null, passwordArray); // Loads a brand new keystore with the password created above
            try (FileOutputStream fileOutputStream = new FileOutputStream(keyStoreLoc)) {
                newKeyStore.store(fileOutputStream, passwordArray); // Stores the new keystore in the position stated above
            }
            loadKeyStore(); // The keystore created is loaded
        } catch (Exception e) {
            errorMethods.defaultErrors(e);
        }
    }
    public static void loadKeyStore() {
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType()); // Inits the keystore with the default type (jks)
            try (FileInputStream fis = new FileInputStream(keyStoreLoc)) {
                keyStore.load(fis, passwordArray); // Loads the keystore using the password and from the position, both set above
            } catch (Exception e) {
                initKeyStore();
                errorMethods.defaultErrors(e);
            }
        } catch (Exception e) {
            initKeyStore(); // This will only run an error if the key store doesn't already exist
            errorMethods.defaultErrors(e);
        }
    }
    public static void setKeyValInKeyStore(SecretKey sKey, String hashedEmail) {
        try {
            loadKeyStore(); // Loads the keystore where the key entry will be saved
            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(sKey); // Creates a new secret key entry in the keystore
            KeyStore.ProtectionParameter passwordEntry = new KeyStore.PasswordProtection(passwordArray); // Adds the default password to the entry
            keyStore.setEntry(hashedEmail, secretKeyEntry, passwordEntry); // This sets up the entry in the keystore
            keyStore.store(new java.io.FileOutputStream(keyStoreLoc), passwordArray); // This stores the value in the keystore, otherwise the entry will only be temporarily stored
        } catch (Exception e) {
            errorMethods.defaultErrors(e);
        }
    }
    public static SecretKey loadKeyEntry(String hashedEmail) {
        try {
            loadKeyStore(); // Loads the keystore
            Key key = keyStore.getKey(hashedEmail,passwordArray); // Gets the stored key (as a Key) from the keystore from the provided alias
            byte[] keyData = key.getEncoded(); // As the key is currently a Key and not a Secretkey, an intermediary is needed to change key types
            SecretKey secretKey = new SecretKeySpec(keyData,0, keyData.length,"AES"); // Creates the new AES SecretKey
            return secretKey;
        } catch (Exception e) {
            errorMethods.defaultErrors(e);
            return null;
        }
    }
    public static void deleteKeyEntry(String alias) {
        try {
            loadKeyStore(); // Loads the keystore
            keyStore.deleteEntry(alias); // Uses the keystore to delete the entry
            keyStore.store(new java.io.FileOutputStream(keyStoreLoc), passwordArray); // Stores the keystore with the updated entries
        } catch (Exception e) {
            errorMethods.defaultErrors(e);
        }
    }
}
