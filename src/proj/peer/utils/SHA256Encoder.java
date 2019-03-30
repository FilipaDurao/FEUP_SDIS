package proj.peer.utils;

import proj.peer.log.NetworkLogger;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

public class SHA256Encoder {

    public static String encode(String text) {

        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return  String.format("%064x", new BigInteger(1, hash));

        } catch (NoSuchAlgorithmException e) {
            NetworkLogger.printLog(Level.SEVERE, "Encoding algorithm not found, using unencrypted data");
            return text;
        }
    }


}
