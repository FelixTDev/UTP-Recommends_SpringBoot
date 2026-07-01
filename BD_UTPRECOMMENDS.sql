-- =========================================================
-- UTP+Recommends - Esquema completo MySQL 8
-- Base de datos para backend Spring Boot + JPA/Hibernate
-- =========================================================

DROP DATABASE IF EXISTS utp_recommends;
CREATE DATABASE utp_recommends
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE utp_recommends;

-- =========================================================
-- 1. USUARIOS, CARRERAS Y ESTUDIANTES
-- =========================================================

CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    rol ENUM('ADMIN', 'ESTUDIANTE') NOT NULL,
    estado ENUM('ACTIVO', 'INACTIVO', 'SUSPENDIDO') NOT NULL DEFAULT 'ACTIVO',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_usuario_email UNIQUE (email),
    CONSTRAINT chk_usuario_email_utp CHECK (email REGEXP '^[A-Za-z0-9._%+-]+@utp[.]edu[.]pe$'),
    CONSTRAINT chk_usuario_nombres CHECK (CHAR_LENGTH(TRIM(nombres)) BETWEEN 2 AND 100),
    CONSTRAINT chk_usuario_apellidos CHECK (CHAR_LENGTH(TRIM(apellidos)) BETWEEN 2 AND 100)
) ENGINE=InnoDB;

CREATE TABLE carrera (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    estado ENUM('ACTIVA', 'INACTIVA') NOT NULL DEFAULT 'ACTIVA',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_carrera_nombre UNIQUE (nombre),
    CONSTRAINT chk_carrera_nombre CHECK (CHAR_LENGTH(TRIM(nombre)) BETWEEN 3 AND 150)
) ENGINE=InnoDB;

CREATE TABLE estudiante (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    codigo_estudiante VARCHAR(9) NOT NULL,
    carrera_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_estudiante_usuario UNIQUE (usuario_id),
    CONSTRAINT uk_estudiante_codigo UNIQUE (codigo_estudiante),
    CONSTRAINT chk_estudiante_codigo CHECK (codigo_estudiante REGEXP '^U[0-9]{8}$'),
    CONSTRAINT fk_estudiante_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_estudiante_carrera FOREIGN KEY (carrera_id) REFERENCES carrera(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- =========================================================
-- 2. DOCENTES, CURSOS Y ASIGNACIÓN CURSO-DOCENTE
-- =========================================================

CREATE TABLE docente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(150) NULL,
    estado ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_docente_email UNIQUE (email),
    CONSTRAINT chk_docente_nombres CHECK (CHAR_LENGTH(TRIM(nombres)) BETWEEN 2 AND 100),
    CONSTRAINT chk_docente_apellidos CHECK (CHAR_LENGTH(TRIM(apellidos)) BETWEEN 2 AND 100),
    CONSTRAINT chk_docente_email CHECK (email IS NULL OR email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+[.][A-Za-z]{2,}$')
) ENGINE=InnoDB;

CREATE TABLE curso (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    tipo ENUM('GENERAL', 'CARRERA') NOT NULL,
    carrera_id BIGINT NULL,
    estado ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_curso_nombre_carrera UNIQUE (nombre, carrera_id),
    CONSTRAINT chk_curso_nombre CHECK (CHAR_LENGTH(TRIM(nombre)) BETWEEN 3 AND 150),
    CONSTRAINT chk_curso_tipo_carrera CHECK (
        (tipo = 'GENERAL' AND carrera_id IS NULL)
        OR
        (tipo = 'CARRERA' AND carrera_id IS NOT NULL)
    ),
    CONSTRAINT fk_curso_carrera FOREIGN KEY (carrera_id) REFERENCES carrera(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE curso_docente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    curso_id BIGINT NOT NULL,
    docente_id BIGINT NOT NULL,
    estado ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_curso_docente UNIQUE (curso_id, docente_id),
    CONSTRAINT fk_curso_docente_curso FOREIGN KEY (curso_id) REFERENCES curso(id) ON DELETE RESTRICT,
    CONSTRAINT fk_curso_docente_docente FOREIGN KEY (docente_id) REFERENCES docente(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE INDEX idx_curso_docente_curso_estado ON curso_docente(curso_id, estado);
CREATE INDEX idx_curso_docente_docente_estado ON curso_docente(docente_id, estado);

-- =========================================================
-- 3. CRITERIOS, RESEÑAS Y CALIFICACIONES
-- =========================================================

CREATE TABLE criterio_calificacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255) NULL,
    estado ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_criterio_nombre UNIQUE (nombre),
    CONSTRAINT chk_criterio_nombre CHECK (CHAR_LENGTH(TRIM(nombre)) BETWEEN 3 AND 100)
) ENGINE=InnoDB;

CREATE TABLE resena (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estudiante_id BIGINT NOT NULL,
    curso_docente_id BIGINT NOT NULL,
    comentario TEXT NOT NULL,
    es_anonimo BOOLEAN NOT NULL DEFAULT FALSE,
    estado ENUM('PENDIENTE', 'APROBADA', 'RECHAZADA', 'OCULTA') NOT NULL DEFAULT 'PENDIENTE',
    motivo_rechazo VARCHAR(255) NULL,
    admin_moderador_id BIGINT NULL,
    version INT NOT NULL DEFAULT 1,
    resena_anterior_id BIGINT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_moderacion TIMESTAMP NULL,

    -- MySQL no tiene índices únicos parciales; esta columna simula unicidad solo para reseñas vivas.
    clave_activa VARCHAR(60) GENERATED ALWAYS AS (
        CASE
            WHEN estado IN ('PENDIENTE', 'APROBADA') THEN CONCAT(estudiante_id, '-', curso_docente_id)
            ELSE NULL
        END
    ) STORED,

    CONSTRAINT uk_resena_activa UNIQUE (clave_activa),
    CONSTRAINT chk_resena_comentario CHECK (CHAR_LENGTH(TRIM(comentario)) >= 10),
    CONSTRAINT chk_resena_version CHECK (version >= 1),
    CONSTRAINT chk_resena_motivo_rechazo CHECK (
        (estado = 'RECHAZADA' AND motivo_rechazo IS NOT NULL AND CHAR_LENGTH(TRIM(motivo_rechazo)) >= 5)
        OR
        (estado <> 'RECHAZADA')
    ),
    CONSTRAINT chk_resena_fecha_moderacion CHECK (
        (estado IN ('APROBADA', 'RECHAZADA', 'OCULTA') AND fecha_moderacion IS NOT NULL)
        OR
        (estado = 'PENDIENTE')
    ),
    CONSTRAINT fk_resena_estudiante FOREIGN KEY (estudiante_id) REFERENCES estudiante(id) ON DELETE CASCADE,
    CONSTRAINT fk_resena_curso_docente FOREIGN KEY (curso_docente_id) REFERENCES curso_docente(id) ON DELETE RESTRICT,
    CONSTRAINT fk_resena_admin FOREIGN KEY (admin_moderador_id) REFERENCES usuario(id) ON DELETE SET NULL,
    CONSTRAINT fk_resena_anterior FOREIGN KEY (resena_anterior_id) REFERENCES resena(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE INDEX idx_resena_estado_fecha ON resena(estado, fecha_creacion);
CREATE INDEX idx_resena_estudiante_estado ON resena(estudiante_id, estado);
CREATE INDEX idx_resena_curso_docente_estado ON resena(curso_docente_id, estado);

CREATE TABLE resena_calificacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    resena_id BIGINT NOT NULL,
    criterio_id BIGINT NOT NULL,
    puntaje TINYINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_resena_criterio UNIQUE (resena_id, criterio_id),
    CONSTRAINT chk_calificacion_puntaje CHECK (puntaje BETWEEN 1 AND 5),
    CONSTRAINT fk_calificacion_resena FOREIGN KEY (resena_id) REFERENCES resena(id) ON DELETE CASCADE,
    CONSTRAINT fk_calificacion_criterio FOREIGN KEY (criterio_id) REFERENCES criterio_calificacion(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE INDEX idx_calificacion_criterio ON resena_calificacion(criterio_id);

-- =========================================================
-- 4. SOLICITUDES DE CURSO / DOCENTE INEXISTENTE
-- =========================================================

CREATE TABLE solicitud (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estudiante_id BIGINT NOT NULL,
    tipo ENUM('CURSO_NUEVO', 'DOCENTE_NUEVO', 'AMBOS') NOT NULL,
    nombre_curso_sugerido VARCHAR(150) NULL,
    carrera_sugerida_id BIGINT NULL,
    nombre_docente_sugerido VARCHAR(150) NULL,
    comentario TEXT NOT NULL,
    estado ENUM('PENDIENTE', 'APROBADA', 'RECHAZADA') NOT NULL DEFAULT 'PENDIENTE',
    admin_id BIGINT NULL,
    resena_generada_id BIGINT NULL,
    motivo_rechazo VARCHAR(255) NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_resolucion TIMESTAMP NULL,

    CONSTRAINT chk_solicitud_tipo_campos CHECK (
        (tipo = 'CURSO_NUEVO' AND nombre_curso_sugerido IS NOT NULL AND nombre_docente_sugerido IS NULL)
        OR
        (tipo = 'DOCENTE_NUEVO' AND nombre_curso_sugerido IS NULL AND nombre_docente_sugerido IS NOT NULL)
        OR
        (tipo = 'AMBOS' AND nombre_curso_sugerido IS NOT NULL AND nombre_docente_sugerido IS NOT NULL)
    ),
    CONSTRAINT chk_solicitud_comentario CHECK (CHAR_LENGTH(TRIM(comentario)) >= 10),
    CONSTRAINT chk_solicitud_motivo_rechazo CHECK (
        (estado = 'RECHAZADA' AND motivo_rechazo IS NOT NULL AND CHAR_LENGTH(TRIM(motivo_rechazo)) >= 5)
        OR
        (estado <> 'RECHAZADA')
    ),
    CONSTRAINT chk_solicitud_resolucion CHECK (
        (estado IN ('APROBADA', 'RECHAZADA') AND admin_id IS NOT NULL AND fecha_resolucion IS NOT NULL)
        OR
        (estado = 'PENDIENTE')
    ),
    CONSTRAINT fk_solicitud_estudiante FOREIGN KEY (estudiante_id) REFERENCES estudiante(id) ON DELETE CASCADE,
    CONSTRAINT fk_solicitud_carrera FOREIGN KEY (carrera_sugerida_id) REFERENCES carrera(id) ON DELETE RESTRICT,
    CONSTRAINT fk_solicitud_admin FOREIGN KEY (admin_id) REFERENCES usuario(id) ON DELETE SET NULL,
    CONSTRAINT fk_solicitud_resena FOREIGN KEY (resena_generada_id) REFERENCES resena(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE INDEX idx_solicitud_estado_fecha ON solicitud(estado, fecha_creacion);
CREATE INDEX idx_solicitud_estudiante_estado ON solicitud(estudiante_id, estado);

-- =========================================================
-- 5. DATOS SEMILLA MÍNIMOS
-- Nota: reemplazar el hash por uno generado con BCrypt en backend.
-- Password sugerido para desarrollo: Admin123!
-- =========================================================

INSERT INTO carrera (nombre, estado) VALUES
('Ingeniería de Sistemas e Informática', 'ACTIVA'),
('Ingeniería Industrial', 'ACTIVA'),
('Administración de Empresas', 'ACTIVA'),
('Contabilidad', 'ACTIVA');

INSERT INTO criterio_calificacion (nombre, descripcion, estado) VALUES
('Claridad', 'Evalúa si el docente explica los temas de forma comprensible.', 'ACTIVO'),
('Puntualidad', 'Evalúa el cumplimiento de horarios y tiempos de clase.', 'ACTIVO'),
('Exigencia justa', 'Evalúa si la exigencia es coherente con lo enseñado.', 'ACTIVO'),
('Disponibilidad', 'Evalúa la disposición del docente para resolver dudas.', 'ACTIVO');

INSERT INTO usuario (email, password_hash, nombres, apellidos, rol, estado) VALUES
('admin@utp.edu.pe', '$2a$10$REEMPLAZAR_HASH_BCRYPT_REAL_EN_BACKEND', 'Administrador', 'Sistema', 'ADMIN', 'ACTIVO');

-- Cursos y docentes de ejemplo opcionales
INSERT INTO docente (nombres, apellidos, email, estado) VALUES
('Carlos', 'Ramírez', 'carlos.ramirez@utp.edu.pe', 'ACTIVO'),
('María', 'Torres', 'maria.torres@utp.edu.pe', 'ACTIVO');

INSERT INTO curso (nombre, tipo, carrera_id, estado) VALUES
('Programación Orientada a Objetos', 'CARRERA', 1, 'ACTIVO'),
('Matemática Básica', 'GENERAL', NULL, 'ACTIVO');

INSERT INTO curso_docente (curso_id, docente_id, estado) VALUES
(1, 1, 'ACTIVO'),
(2, 2, 'ACTIVO');
