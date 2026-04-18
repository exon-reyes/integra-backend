package integra.acceso.service.user;

public class CredencialesEmailTemplate {

    public static String generar(String nombreCompleto,
                                 String usuario,
                                 String contrasena,
                                 int anio) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
              <meta charset="UTF-8" />
              <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
              <title>Tus credenciales de acceso</title>
            </head>
            <body style="margin:0; padding:0; background-color:#f0f4f8;
                         font-family: Arial, Helvetica, sans-serif;">

              <table width="100%%" cellpadding="0" cellspacing="0" border="0"
                     style="background-color:#f0f4f8; padding: 40px 20px;">
                <tr>
                  <td align="center">
                    <table width="560" cellpadding="0" cellspacing="0" border="0"
                           style="background-color:#ffffff; border-radius:12px;
                                  overflow:hidden; border:1px solid #dce4f0;">

                      <!-- CABECERA -->
                      <tr>
                        <td align="center"
                            style="background-color:#1a3a5c; padding:32px 40px;">
                          <div style="width:52px; height:52px; border-radius:50%%;
                                      background:rgba(255,255,255,0.15);
                                      display:inline-block; text-align:center;
                                      line-height:52px; margin-bottom:14px;">
                            <span style="font-size:22px;">&#128274;</span>
                          </div>
                          <h1 style="color:#ffffff; font-size:22px; font-weight:600;
                                     margin:0; letter-spacing:-0.3px;">
                            Tus credenciales de acceso
                          </h1>
                        </td>
                      </tr>

                      <!-- CUERPO -->
                      <tr>
                        <td style="padding:32px 40px;">

                          <p style="color:#1a1a1a; font-size:16px; margin:0 0 18px;">
                            ¡Hola, <strong>%s</strong>!
                          </p>

                          <p style="color:#555555; font-size:14px; line-height:1.7;
                                    margin:0 0 24px;">
                            Te informamos que se han generado tus credenciales de
                            acceso a la plataforma Integra para la gestión de vacaciones/permisos. Por el momento ya las tienes
                            disponibles &mdash; guárdalas en un lugar seguro.
                          </p>

                          <!-- CAJA DE CREDENCIALES -->
                          <table width="100%%" cellpadding="0" cellspacing="0" border="0"
                                 style="background-color:#f4f7fb; border-radius:10px;
                                        border:1px solid #dce4f0; margin-bottom:24px;">
                            <tr>
                              <td style="padding:20px 24px;">
                                <p style="font-size:11px; color:#888888;
                                          text-transform:uppercase; letter-spacing:1px;
                                          margin:0 0 4px;">Usuario</p>
                                <p style="font-size:16px; font-weight:700;
                                          color:#1a3a5c; margin:0;
                                          font-family:Courier New, monospace;">%s</p>
                              </td>
                            </tr>
                            <tr>
                              <td style="border-top:1px solid #dce4f0; padding:20px 24px;">
                                <p style="font-size:11px; color:#888888;
                                          text-transform:uppercase; letter-spacing:1px;
                                          margin:0 0 4px;">Contraseña</p>
                                <p style="font-size:16px; font-weight:700;
                                          color:#1a3a5c; margin:0;
                                          font-family:Courier New, monospace;">%s</p>
                              </td>
                            </tr>
                          </table>

                          <!-- AVISO -->
                          <table width="100%%" cellpadding="0" cellspacing="0" border="0"
                                 style="background-color:#fffbec;
                                        border-left:3px solid #f0a500;
                                        border-radius:0 8px 8px 0;
                                        margin-bottom:24px;">
                            <tr>
                              <td style="padding:14px 16px; font-size:13px;
                                         color:#7a5200; line-height:1.6;">
                                <strong>Próximo paso:</strong> Proximamente recibirás
                                un correo con el enlace para acceder a la plataforma.
                                ¡Mantente al pendiente de tu bandeja de entrada!
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>

                      <!-- PIE DE PÁGINA -->
                      <tr>
                        <td align="center"
                            style="background-color:#f4f7fb;
                                   border-top:1px solid #dce4f0;
                                   padding:16px 40px;">
                          <p style="color:#aaaaaa; font-size:11px; margin:0;">
                            v2.1 &copy; %d Comialex &nbsp;&middot;&nbsp;
                            Integra Gestión Vacacional
                          </p>
                        </td>
                      </tr>

                    </table>
                  </td>
                </tr>
              </table>

            </body>
            </html>
            """.formatted(nombreCompleto, usuario, contrasena, anio);
    }
}
