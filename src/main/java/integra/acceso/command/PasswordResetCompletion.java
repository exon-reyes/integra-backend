package integra.acceso.command;

public record PasswordResetCompletion(String token, String newPassword) {
}