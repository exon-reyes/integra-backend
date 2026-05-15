# Manual de Configuración Cloudflared (Windows + Caddy + Backend)

## 1. Arquitectura

Internet → Cloudflare → cloudflared → Caddy → Backend

## 2. Estructura

C:`\cloudflared`{=tex}\
- cloudflared.exe - config.yml - tunnel.json - cloudflared.log

## 3. Crear túnel

cloudflared tunnel login\
cloudflared tunnel create mi-tunel

## 4. Configuración

``` yaml
tunnel: TU_ID
credentials-file: C:\cloudflared\TU_ID.json

protocol: http2

logfile: C:\cloudflared\cloudflared.log
loglevel: info

ingress:
  - hostname: tudominio.com
    service: http://localhost:4201
  - service: http_status:404
```

## 5. DNS

cloudflared tunnel route dns mi-tunel tudominio.com

## 6. Caddy

http://0.0.0.0:4201 { reverse_proxy localhost:8081 }

## 7. Servicio Windows

sc config cloudflared binPath=
"\"C:`\cloudflared`{=tex}`\cloudflared`{=tex}.exe\" --config
C:`\cloudflared`{=tex}`\config`{=tex}.yml tunnel run TU_ID"

net stop cloudflared taskkill /F /IM cloudflared.exe net start
cloudflared

## 8. Validaciones

sc query cloudflared\
tasklist \| findstr cloudflared\
cloudflared tunnel info TU_ID\
curl http://localhost:4201

## 9. Logs

type C:`\cloudflared`{=tex}`\cloudflared`{=tex}.log

## 10. Problemas comunes

-   Error 1033 → túnel sin conexión\
-   Funciona en CMD pero no como servicio → rutas/permisos\
-   Backend inaccesible → revisar Caddy

## 11. Buenas prácticas

-   Usar rutas absolutas\
-   No usar carpeta de usuario\
-   Logs activos\
-   Separar responsabilidades

## 12. Seguridad

-   Proteger archivo JSON\
-   No exponer backend directo\
-   Usar Cloudflare como entrada única
