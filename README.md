# ms-mensajeria — Colegio Bernardo O'Higgins

Microservicio de mensajería interna del sistema **Libro de Clases Digital** (DSY1106 Fullstack III).  
Permite a los usuarios del colegio (docentes, apoderados, administrativos) enviarse mensajes privados cifrados dentro de la plataforma.

---

## Responsabilidades

- Envío de mensajes privados entre usuarios del sistema
- Cifrado del cuerpo del mensaje en reposo (AES-256 vía `CifradoConverter`)
- Control de acceso: cada usuario solo puede leer su propia bandeja, enviados y conversaciones
- Marcado de mensajes como leídos (solo el destinatario)
- Conteo de mensajes no leídos en bandeja de entrada

---

## Seguridad y control de acceso

Todos los endpoints requieren el header `X-User-Id` inyectado por el BFF tras validar el JWT. El servicio usa este ID para verificar que el usuario autenticado solo acceda a sus propios datos:

| Operación | Restricción |
|---|---|
| Ver conversación | El usuario autenticado debe ser `usuario1` o `usuario2` |
| Ver bandeja de entrada | Solo el propio receptor (`idUsuario` == `X-User-Id`) |
| Ver mensajes enviados | Solo el propio emisor (`idUsuario` == `X-User-Id`) |
| Marcar como leído | Solo el destinatario del mensaje |
| Contar no leídos | Solo el propio usuario |

Las violaciones lanzan `AccesoDenegadoException` → HTTP 403.

---

## Cifrado en reposo

El campo `cuerpoMensaje` se cifra automáticamente al persistir y se descifra al leer mediante un `@Convert` JPA con `CifradoConverter` (AES-256, clave en Base64 configurada en `application.properties`). El cifrado es transparente para el resto de la aplicación.

---

## Endpoints REST (Puerto 8086)

Base path: `/mensajes`

| Método | Ruta | Header requerido | Descripción |
|---|---|---|---|
| `POST` | `/mensajes` | `X-User-Id` | Envía un mensaje al receptor indicado en el body |
| `GET` | `/mensajes/conversacion?usuario1={id}&usuario2={id}` | `X-User-Id` | Lista todos los mensajes entre dos usuarios (orden cronológico) |
| `GET` | `/mensajes/bandeja/{idUsuario}` | `X-User-Id` | Lista los mensajes recibidos por el usuario (orden desc por fecha) |
| `GET` | `/mensajes/enviados/{idUsuario}` | `X-User-Id` | Lista los mensajes enviados por el usuario (orden desc por fecha) |
| `GET` | `/mensajes/bandeja/{idUsuario}/no-leidos/count` | `X-User-Id` | Retorna el conteo de mensajes no leídos del usuario |
| `PUT` | `/mensajes/{id}/marcar-leido` | `X-User-Id` | Marca un mensaje como leído (solo el destinatario) |

### Body `POST /mensajes`

```json
{
  "idUsuarioReceptor": 10,
  "asuntoMensaje": "Reunión de apoderados",
  "cuerpoMensaje": "Estimado apoderado, lo citamos para el viernes a las 18:00 hrs."
}
```

> `asuntoMensaje` es opcional. `cuerpoMensaje` e `idUsuarioReceptor` son obligatorios.

---

## Modelo de datos (tabla `MENSAJE` en `ms_mensajeria`)

| Campo | Tipo | Descripción |
|---|---|---|
| `ID_MENSAJE` | `Long` (PK) | Identificador único (secuencia Oracle `seq_mensaje`) |
| `ASUNTO_MENSAJE` | `String` (200) | Asunto del mensaje (opcional) |
| `CUERPO_MENSAJE` | `LOB` | Cuerpo cifrado con AES-256 (`CifradoConverter`) |
| `FECHA_ENVIO_MENSAJE` | `LocalDateTime` | Fecha y hora de envío (zona: `America/Santiago`) |
| `ESTADO_MENSAJE` | `String` (20) | `NO_LEIDO` (por defecto) o `LEIDO` |
| `ID_USUARIO_EMISOR` | `Long` | ID del usuario que envía el mensaje |
| `ID_USUARIO_RECEPTOR` | `Long` | ID del usuario destinatario |

---

## Configuración

```properties
# application.properties
spring.application.name=mensajeriaService
server.port=8086

spring.datasource.url=jdbc:oracle:thin:@proyectolibroasistencia_high?TNS_ADMIN=<ruta_wallet>
spring.datasource.username=ms_mensajeria
spring.datasource.driver-class-name=oracle.jdbc.driver.OracleDriver

# Pool reducido (tier gratuito compartido entre microservicios)
spring.datasource.hikari.maximum-pool-size=3
spring.datasource.hikari.minimum-idle=1

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect

# Clave AES-256 en Base64 para cifrar el cuerpo de los mensajes
mensajeria.encryption.key=<clave-base64-256-bits>

# Seguridad deshabilitada (la protección la ejerce el BFF)
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
```

---

## Ejecución

```bash
# Desde la carpeta mensajeriaService/
./mvnw spring-boot:run
```

> Requiere conectividad con Oracle Autonomous Database y el Wallet configurado.

---

## Tests unitarios

```bash
./mvnw test -Dtest="MensajeServiceImplTest" -Dsurefire.failIfNoSpecifiedTests=false
```

| Clase | Tests | Casos cubiertos |
|---|---|---|
| `MensajeServiceImplTest` | 13 | Enviar (sin receptor → excepción, cuerpo vacío → excepción, válido), listar conversación (participante válido, acceso denegado), bandeja de entrada (propia, ajena → 403), enviados (propios, ajenos → 403), marcar leído (receptor correcto, otro usuario → 403), contar no leídos (propio, ajeno → 403) |

Los tests usan `@ExtendWith(MockitoExtension.class)` — no requieren base de datos ni Spring context.

---

## Patrones de diseño implementados

| Patrón | Implementación |
|---|---|
| **Repository** | `MensajeRepository` — abstrae el acceso a datos |
| **Service Layer** | `MensajeService` (interfaz) + `MensajeServiceImpl` — separa lógica de negocio del controlador |
| **DTO** | `MensajeDTO` — recibe solo los campos de entrada necesarios (el emisor viene del header `X-User-Id`) |
| **Converter** | `CifradoConverter` (`@Convert` JPA) — cifra/descifra el cuerpo del mensaje de forma transparente |
| **Exception Handler** | `GlobalExceptionHandler` — captura `AccesoDenegadoException` y retorna HTTP 403 con mensaje descriptivo |

---

## Stack tecnológico

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje |
| Spring Boot | 3.2.12 | Framework base |
| Spring Data JPA | 3.x | Acceso a base de datos |
| Oracle Autonomous DB | — | Persistencia (esquema `ms_mensajeria`) |
| JPA `AttributeConverter` | — | Cifrado AES-256 del cuerpo del mensaje |
| JUnit 5 + Mockito | (via spring-boot-starter-test) | Tests unitarios |
