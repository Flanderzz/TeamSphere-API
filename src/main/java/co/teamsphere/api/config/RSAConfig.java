package co.teamsphere.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RSAConfig {

    @Bean
    public PrivateKey privateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("private.pem");

        if(inputStream == null){
            throw new IllegalStateException("Private Key Could Not Be Found!");
        }

        var key = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGINPRIVATEKEY-----", "")
                .replace("-----ENDPRIVATEKEY-----", "")
                .replaceAll("\\s", "");

        if(key.isEmpty())
            throw new IllegalStateException("Key was not generated!");

        byte[] keyBytes = Base64.getDecoder().decode(key);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    @Bean
    public PublicKey publicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("public.pem");

        if(inputStream == null){
            throw new IllegalStateException("Public Key Could Not Be Found!");
        }

        var key = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("-----BEGINPUBLICKEY-----", "")
                .replace("-----ENDPUBLICKEY-----", "")
                .replaceAll("\\s", "");

        if(key.isEmpty())
            throw new IllegalStateException("Key was not generated!");

        byte[] keyBytes = Base64.getDecoder().decode(key);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

}
