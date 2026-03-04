package integra.acceso.service.account;

import integra.acceso.util.TokenGenerator;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class UuidTokenGenerator implements TokenGenerator {

    private static final String PEPPER = "INTEGRA_INTERNAL_PEPPER_2026";

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generate() {
        byte[] random = new byte[32]; // 256 bits
        secureRandom.nextBytes(random);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(random);
    }

    @Override
    public String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(PEPPER.getBytes(StandardCharsets.UTF_8));
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
