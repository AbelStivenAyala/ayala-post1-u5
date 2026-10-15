Post-contenido Unidad 5 - Integración en Aplicaciones Web

Este repositorio tiene las dos partes del post-contenido de Patrones de Diseño de Software. Es un solo proyecto Spring Boot, reservas-labs-api, para reservar laboratorios de cómputo de la universidad. La parte 1 es una API REST en capas (Entity, Repository, Service y Controller) con base de datos H2. La parte 2 le agrega una vista con Thymeleaf que usa el mismo Service de la API.

DESCRIPCION

Parte 1. LaboratorioRepository y ReservaRepository extienden JpaRepository. ReservaRepository tiene además una consulta JPQL propia que busca las reservas activas que se cruzan con un rango de horas. ReservaService tiene las reglas de negocio: no permitir cruces de horario, respetar el horario de atención, la duración de la reserva y no cancelar reservas que ya empezaron. ReservaController y LaboratorioController exponen /api/reservas y /api/laboratorios.

Parte 2. ReservaWebController atiende las rutas /reservas con plantillas Thymeleaf y recibe el mismo ReservaService que usa la API. ReservaWebExceptionHandler maneja las mismas excepciones pero en vez de devolver JSON redirige al formulario con un mensaje.

Paquetes dentro de com.universidad.reservaslabs: model, repository, service, exception, controller (REST) y web (MVC). Las plantillas lista.html y nueva.html están en src/main/resources/templates/reservas.

COMO EJECUTAR

Se necesita Java 17 o superior. El proyecto trae el Maven Wrapper, así que no hace falta instalar Maven. Desde la carpeta del proyecto en PowerShell:

.\mvnw.cmd clean package

.\mvnw.cmd spring-boot:run

Cuando aparece la línea Started ReservasLabsApiApplication ya se puede usar. La API queda en http://localhost:8080/api/reservas, la vista en http://localhost:8080/reservas y la consola de H2 en http://localhost:8080/h2-console (JDBC URL jdbc:h2:mem:reservas_labs_db, usuario sa y sin contraseña).

La base de datos es en memoria, entonces cada vez que se reinicia la aplicación se borran los datos y hay que volver a crear el laboratorio. Usé Spring Boot 4.1.1 porque Spring Initializr ya no ofrece la 3.2.x que pedía la guía.

ENDPOINTS REST

GET /api/laboratorios: lista los laboratorios (200)
GET /api/laboratorios/{id}: un laboratorio (200 o 404)
POST /api/laboratorios: crea un laboratorio (201 o 400)
GET /api/reservas: lista las reservas (200)
GET /api/reservas/{id}: una reserva (200 o 404)
GET /api/reservas/laboratorio/{id}: reservas de un laboratorio (200)
POST /api/reservas: crea una reserva (201, 400, 404 o 409)
DELETE /api/reservas/{id}: cancela una reserva (204, 404 o 409)

RUTAS DE LA VISTA

GET /reservas: lista de reservas
GET /reservas/nueva: formulario
POST /reservas: crea la reserva
POST /reservas/{id}/cancelar: cancela la reserva

DECISIONES DE DISEÑO

Punto de decisión 1: dónde validar el cruce de horarios

La consulta que busca los cruces está en el Repository (buscarSolapamientos) y la decisión de rechazar la reserva está en ReservaService. El Repository responde una pregunta de datos: qué reservas activas se cruzan con este rango. El Service responde una pregunta de negocio: se permite crear esta reserva o no. La otra opción era traer todas las reservas del laboratorio a memoria y comparar los rangos en Java, pero eso no funciona bien cuando la tabla crece, porque cada reserva nueva cargaría todo el historial. Si el Controller llamara directo a buscarSolapamientos tendría que decidir qué hacer con el resultado, es decir, qué excepción lanzar y qué código HTTP devolver, y esa decisión se repetiría en cada controlador que cree reservas, incluido el de la vista.

Punto de decisión 2: reglas que usan el Repository y reglas que no

El horario de atención (07:00 a 21:00) y la duración (entre 30 minutos y 3 horas) solo dependen de los datos de la reserva que se está creando. Por eso validarHorarioYDuracion está en el Service con Java normal y no consulta la base de datos. El criterio que usé es este: si la regla necesita comparar con datos de otras filas (como las demás reservas), se apoya en el Repository; si solo depende del objeto que se está validando, no tiene sentido hacer una consulta. Esas reglas lanzan ReservaInvalidaException, que responde 400. ReservaConflictException responde 409 y la dejé para los conflictos con lo que ya existe, como el cruce de horario o cancelar una reserva que ya empezó.

Punto de decisión 3: cómo comparten el Service el controlador REST y el MVC

ReservaController y ReservaWebController reciben por constructor la misma clase ReservaService, que Spring maneja como un solo bean. Ninguno de los dos repite las validaciones. Si hubiera copiado la lógica en el controlador de la vista, o creado un ReservaWebService parecido, cada cambio en una regla habría que hacerlo en dos sitios, que es justo lo que la capa Service busca evitar.

Punto de decisión 4: manejo de errores en REST y en MVC

Hay dos manejadores de excepciones. GlobalRestExceptionHandler usa @RestControllerAdvice limitado a los @RestController con annotations = RestController.class. ReservaWebExceptionHandler usa @ControllerAdvice limitado a ReservaWebController con assignableTypes. Un @RestControllerAdvice siempre responde JSON y la página necesita redirigir con un mensaje, así que no sirve uno solo. También se podía hacer un único manejador que mirara el header Accept, pero habría que meter una condición por cada excepción. Con dos manejadores cada uno se encarga de su tipo de controlador y los dos usan las mismas excepciones de dominio, por eso el mensaje es el mismo en la API y en la página.

Decisión adicional: LaboratorioController sin Service

LaboratorioController usa el Repository directamente porque el catálogo de laboratorios es un CRUD sin reglas propias. Un LaboratorioService que solo llamara al Repository sería un Service anémico, así que solo creé Service donde hay reglas de negocio.

PRUEBAS REALIZADAS

Las capturas están en la carpeta capturas del repositorio, separadas en api-rest y web.

En la API probé desde PowerShell. Crear un laboratorio y una reserva válida respondió 201. Intentar reservar el mismo laboratorio en un horario que se cruza respondió 409 con el mensaje "El laboratorio Lab. Cómputo 3 ya tiene una reserva en ese horario". Una reserva de 22:00 a 23:00 respondió 400 con el mensaje del horario de atención, y una de 4 horas respondió 400 con el mensaje de la duración. En la consola de H2 se ve la tabla RESERVAS con la reserva guardada y sin la que fue rechazada.

En la vista probé la lista de reservas, el formulario con el combo de laboratorios, la creación de una reserva, el cancelar y el intento de reservar en un horario ocupado, que vuelve al formulario con el mensaje de error en rojo. También comprobé que una reserva cancelada deja libre el horario, porque la consulta de cruces ignora las canceladas.

HERRAMIENTAS

Java 17, Spring Boot 4.1.1, Spring Data JPA, H2, Thymeleaf, Lombok, Maven, PowerShell, Git, GitHub y VS Code.

CONCLUSIONES

Lo que más me ayudó fue separar la consulta de cruces, que va en el Repository, de la decisión de rechazar la reserva, que va en el Service. Lo más difícil fue decidir dónde poner las reglas de horario y duración, hasta que usé el criterio de si necesitaban datos de otras filas o no. Reusar el mismo ReservaService en la API y en la vista evitó repetir reglas y el mismo mensaje de error salió en JSON y en la página. También vi que no todas las entidades necesitan su propio Service, como pasa con los laboratorios.