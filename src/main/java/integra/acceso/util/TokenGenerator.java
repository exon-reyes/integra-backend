package integra.acceso.util;

public interface TokenGenerator {
    String generate();


    String hash(String token);
}