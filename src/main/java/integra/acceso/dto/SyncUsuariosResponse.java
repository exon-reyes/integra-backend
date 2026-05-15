package integra.acceso.dto;

public record SyncUsuariosResponse(
        int totalProcesados,
        int creados,
        int omitidos,
        String archivoGenerado
) {}
