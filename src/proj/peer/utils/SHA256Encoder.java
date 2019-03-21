package proj.peer.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SHA256Encoder {

    public static String encode(String text) {

        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            byte[] encoded = Base64.getEncoder().encode(hash);
            return new String(encoded, 0, encoded.length);

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Encoding algorithm not found!");
            return text;
        }
    }
}
