# Integrador 2 — Registro de Estudiantes (JPA/Hibernate)

## Estado

Implementado sobre el plan y la especificación de `docs/`.

## Consigna

Para un registro de estudiantes (nombres, apellido, edad, género, documento,
ciudad, libreta universitaria, carreras en las que está inscripto, antigüedad
en cada carrera y si se graduó o no):

1. Diseñar el **diagrama de objetos** y el **DER**.
2. Implementar consultas para: **a)** dar de alta un estudiante,
   **b)** matricular un estudiante en una carrera, **c)** recuperar todos los
   estudiantes con un criterio de ordenamiento simple, **d)** recuperar un
   estudiante por su número de libreta universitaria, **e)** recuperar todos
   los estudiantes por género, **f)** recuperar las carreras con estudiantes
   inscriptos ordenadas por cantidad de inscriptos, **g)** recuperar los
   estudiantes de una determinada carrera filtrados por ciudad.
3. Generar un **reporte** de las carreras con inscriptos y egresados por año,
   ordenadas alfabéticamente y con años cronológicos.

Las consultas se resuelven mayormente en **JPQL**, no en código Java.

## Stack

- **Java 25** + Maven (proyecto nuevo `estudiantesJPA/`)
- **Jakarta Persistence 3.1 + Hibernate 6.4.x** sobre **MySQL 8**
- **Lombok** (entidades) y **commons-csv** (carga de datos)
- Esquema recreado en cada corrida (`hibernate.hbm2ddl.auto = create-drop`);
  la base se crea sola si no existe

## Modelo

Tres entidades: `Estudiante`, `Carrera` e `Inscripcion` (entidad asociativa
de la relación N:M, con año de inscripción, año de graduación y antigüedad).
`dni` y `lu` son únicos. Estado "graduado" derivado de `esGraduado()`.

## Requisitos previos

- JDK 25 y Maven instalados.
- MySQL 8 escuchando en `localhost:3306` (usuario `root`, sin password).
  Opción Docker: `docker compose up -d`.
  Si ya tenés un MySQL en el puerto 3306 (p. ej. el contenedor `estudiantes-db`
  ya levantado), usá ese y no levantes el compose.

## Cómo ejecutar

```
cd estudiantesJPA
docker compose up -d      # solo si no hay otro MySQL 8 en localhost:3306
mvn exec:java
```

El `Main` es secuencial: carga los CSVs de `src/main/resources/datos/` (con
normalización de género y rechazo con log de filas inválidas) y ejecuta cada
punto de la consigna en orden, mostrando la salida por consola en columnas.

## Documentación

- Plan y decisiones: `docs/plan-integrador-estudiantes.md`
- Especificación de implementación: `docs/documentacion-to.spec.md`
- Diagramas (mermaid + imágenes): `docs/`
- Bitácora de uso de IA: `docs/uso-de-ia.md`

## Uso de IA

Ver [`docs/uso-de-ia.md`](docs/uso-de-ia.md).

### Nota sobre los datos de origen

Los tres CSVs (`carreras.csv`, `estudiantes.csv`, `estudianteCarrera.csv`)
se copian tal cual a `src/main/resources/datos/` y **no se modifican**. La
carga rechaza con log las filas inválidas. Resultado esperado: 15 carreras,
104 estudiantes y **101 inscripciones** cargadas, **8 rechazadas** (4 con
graduación anterior a la inscripción, 1 con año truncado, 2 con DNI inexistente
en el padrón y 1 par duplicado).