# Uso de IA en este proyecto

Este documento deja registro de cómo se usó inteligencia artificial durante
el desarrollo de los trabajos integradores, con qué criterio y con qué
verificación.

## Herramienta

Claude Code (Anthropic), modelo Claude Opus 5, ejecutado desde la terminal
sobre este mismo repositorio.

La sesión de implementación del 2026-09-09 se hizo con **OpenCode** (modelo
`big-pickle`) desde la terminal sobre el mismo repositorio.

## Cómo está configurado

La configuración está versionada en el repositorio, no en las máquinas
personales. Cualquiera del grupo que clone el repo trabaja con la misma.

- **`CLAUDE.md`** (raíz) — convenciones del proyecto que el agente lee al
  iniciar cada sesión: estructura, idioma, reglas de trabajo y qué no tocar.
- **`.claude/skills/`** — procedimientos propios, invocables escribiendo `/`
  seguido del nombre:

| Skill | Para qué sirve |
|---|---|
| `/grilling` | Interroga en rondas sobre una decisión o un plan, mostrando una respuesta recomendada en cada pregunta, hasta que no queden supuestos sin resolver |
| `/to-spec` | Toma lo conversado y lo convierte en una especificación escrita: problema, solución, historias de usuario, decisiones de implementación y de testing |
| `/prototype` | Construye un prototipo descartable para responder una única duda de diseño, sea de lógica (HTML autocontenido con botones) o de interfaz (variantes comparables) |
| `/java-jpa-hibernate` | Referencia de persistencia con JPA/Hibernate: mapeo de entidades, prevención del problema N+1, transacciones y caché |

### Procedencia de las skills

`grilling`, `to-spec` y `prototype` son de autoría de Matt Pocock,
incorporadas sin modificaciones. `java-jpa-hibernate` proviene de un
paquete de skills generado automáticamente; se conserva por su contenido
técnico, con la salvedad de que sus archivos de apoyo
(`references/`, `assets/`, `scripts/`) son material genérico de relleno y
no aportan nada al proyecto.

## Criterio de uso

La IA se usa para explorar alternativas, redactar documentación, revisar
código y detectar errores.

Las decisiones de diseño, la comprensión de lo que se entrega y la
verificación de que el código efectivamente funciona son responsabilidad
nuestra. No se incorpora código que no podamos explicar.

## Rastro en el historial

Los commits hechos con asistencia de IA quedan firmados con una línea
`Co-Authored-By: Claude`, visible en el historial de Git.

## Bitácora

| Fecha | Qué se hizo | Cómo se verificó |
|---|---|---|
| 2026-09-02 | Configuración inicial del agente: `CLAUDE.md`, `.claude/skills/` con cuatro skills y esta bitácora. Se creó la carpeta `ejercicio_dos/` a la espera de la consigna | Revisión manual de los archivos creados |
| 2026-09-02 | Llegó la consigna del Integrador 2 (registro de estudiantes, JPA). Se usó el skill `/grilling` para entrevistar en rondas hasta cerrar el árbol de decisiones de diseño. Se generó `docs/plan-integrador-estudiantes.md` con el modelo, los diagramas (mermaid `.mmd`), las consultas JPQL y la estrategia de carga de CSV. No se implementó código todavía | El plan quedó como documento; los diagramas `.mmd` se generaron con mermaid y quedan pendientes de exportar a imagen para la entrega |
| 2026-09-09 | Se implementó el proyecto `estudiantesJPA/` según `docs/plan-integrador-estudiantes.md` y `docs/documentacion-to.spec.md`: entidades `Estudiante`, `Carrera`, `Inscripcion`; repositorios (interfaz + impl singleton); DTOs como `record`; `Main` secuencial con los siete puntos y el reporte; `docker-compose.yml` con MySQL 8. Decisiones de implementación: **commons-csv** por continuidad con el Integrador 1 (la spec lo prefería); **reporte construido con dos consultas JPQL** (inscriptos por año de inscripción y egresados por año de graduación) combinadas en Java, respetando la forma del DTO y el orden (carrera ASC, año ASC) que fija la spec; `show_sql=false` para salida limpia; **Lombok configurado con `annotationProcessorPaths` + `proc=full`** en el pom porque el procesamiento implícito no corre con JDK 25 (el Integrador 1 tiene el mismo problema). **Los CSVs no se modificaron** (decisión del grupo): las inscripciones con DNI `6397408` se rechazan en la carga, por lo que el resultado es **101 inscripciones cargadas y 8 rechazadas** (la spec asumía corregir el tipeo y esperaba 103/6). Sin tests automatizados (decisión del grupo); la verificación fue compilar y ejecutar el `Main` contra MySQL 8, contrastando la salida con los conteos y ordenamientos esperados | `mvn compile` en 25 sin errores y `mvn exec:java` contra el contenedor `estudiantes-db` (MySQL 8.4, root sin password) con la salida esperada: 15/0 carreras, 104/0 estudiantes, 101/8 inscripciones; alta y matriculación con rechazos de duplicados; TUDAI+Rauch → 4; reporte alfabético y cronológico |
