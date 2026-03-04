package integra.acceso.service.account;

public interface PasswordResetNotifier {
    void enviarLink(String email, String user, String resetLink);
}