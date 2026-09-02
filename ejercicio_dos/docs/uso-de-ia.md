# Uso de IA en este proyecto

Este documento deja registro de cómo se usó inteligencia artificial durante
el desarrollo de los trabajos integradores, con qué criterio y con qué
verificación.

## Herramienta

Claude Code (Anthropic), modelo Claude Opus 5, ejecutado desde la terminal
sobre este mismo repositorio.

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
