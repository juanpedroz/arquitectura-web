# Integrador 2 — Arquitectura Web

Esta configuración aplica **solo al Integrador 2**. El Integrador 1 vive en
`../ejercicio_uno/` y está entregado: no modificarlo.

## Estado

**La consigna todavía no está definida.** No asumir requisitos ni elegir
tecnologías hasta tenerla. Cuando llegue, se registra acá el stack acordado.

## Estructura

| Ruta | Contenido |
|---|---|
| `.claude/skills/` | Procedimientos propios, invocables con `/` |
| `docs/uso-de-ia.md` | Bitácora del uso de IA |
| `README.md` | Consigna, stack y cómo ejecutar |

## Contexto del Integrador 1

Resuelto con Java + Maven, JDBC puro sobre MySQL 5.7 levantado con Docker,
patrón DAO con Abstract Factory, DTOs para las consultas y carga de datos
desde archivos CSV.

Sirve como referencia de estilo, pero este integrador no está obligado a
repetir esas decisiones.

## Idioma

Código, documentación y mensajes de commit en español.

## Reglas de trabajo

1. Antes de implementar algo grande, proponer un plan corto y esperar el OK.
2. No inventar requisitos. Si algo de la consigna es ambiguo, preguntarlo
   en vez de resolverlo por cuenta propia.
3. No tocar `../ejercicio_uno/`: está entregado.
4. El repositorio es grupal. Avisar antes de hacer push.
5. Las decisiones de diseño que se tomen se registran en `docs/uso-de-ia.md`.
