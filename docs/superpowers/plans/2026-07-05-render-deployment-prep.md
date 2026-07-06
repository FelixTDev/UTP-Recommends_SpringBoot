# Render Deployment Prep Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Dejar el backend listo para desplegar en Render con MySQL en Railway y frontend externo con CORS configurable.

**Architecture:** La configuración seguirá centralizada en Spring Boot, resolviendo producción mediante variables de entorno. El despliegue se empacará con Docker multi-stage y se describirá en un Blueprint `render.yaml`.

**Tech Stack:** Spring Boot 3.3, Java 21, Maven, Docker, Render Blueprint YAML

## Global Constraints

- Mantener compatibilidad con desarrollo local en `localhost:8081`.
- No hardcodear dominios productivos en código Java.
- Usar variables de entorno para secretos y URLs externas.
- Mantener `ddl-auto=validate`.

---

### Task 1: Configuracion runtime y CORS

**Files:**
- Modify: `C:\WebProyecto\src\main\resources\application.yml`
- Modify: `C:\WebProyecto\src\main\java\com\utp\recommends\config\CorsConfig.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\config\CorsProperties.java`
- Test: `C:\WebProyecto\src\test\java\com\utp\recommends\config\CorsConfigTest.java`

### Task 2: Empaquetado de despliegue

**Files:**
- Create: `C:\WebProyecto\Dockerfile`
- Create: `C:\WebProyecto\.dockerignore`
- Create: `C:\WebProyecto\render.yaml`
- Modify: `C:\WebProyecto\.env.example`

### Task 3: Verificacion

**Files:**
- Verify: `C:\WebProyecto\src\test\java\com\utp\recommends\config\CorsConfigTest.java`
- Verify: `C:\WebProyecto\pom.xml`
