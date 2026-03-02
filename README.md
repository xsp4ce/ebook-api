<h1 align="center">📚 LiterAlura</h1>

<p align="center">
  Catálogo de libros interactivo por consola desarrollado en Java con Spring Boot, que consulta la API Gutendex y persiste los datos en PostgreSQL.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-blue?logo=java" alt="Java 17"/>
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.3-6DB33F?logo=spring-boot" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/PostgreSQL-16+-336791?logo=postgresql" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Jackson-2.16.1-orange" alt="Jackson"/>
  <img src="https://img.shields.io/badge/Estado-Completado-brightgreen" alt="Estado"/>
</p>

---

## Índice

- [Descripción](#descripción)
- [Estado del proyecto](#estado-del-proyecto)
- [Características](#características)
- [Acceso al proyecto](#acceso-al-proyecto)
- [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Autor](#autor)

---

## Descripción

**LiterAlura** es una aplicación de consola que permite construir un catálogo personalizado de libros consultando la API pública [Gutendex](https://gutendex.com), que expone metadatos de más de 70.000 libros del proyecto Gutenberg.

El usuario puede buscar libros por título, guardarlos en una base de datos PostgreSQL y luego realizar distintas consultas sobre el catálogo acumulado: listar por idioma, buscar autores, ver estadísticas de descargas y más.

Proyecto desarrollado como parte del programa **Oracle Next Education (ONE)** de Alura Latam.

---

## Estado del proyecto

✅ **Completado** — todas las funcionalidades obligatorias y extras implementadas.

---

## Características

### Funcionalidades principales
- ✅ Buscar libro por título (consulta Gutendex y guarda en BD)
- ✅ Listar todos los libros registrados
- ✅ Listar todos los autores registrados
- ✅ Listar autores vivos en un año determinado
- ✅ Listar libros por idioma con cantidad total

### Funcionalidades extra
- ✅ Estadísticas de descargas (total, promedio, máximo, mínimo) con `DoubleSummaryStatistics`
- ✅ Top 10 libros más descargados
- ✅ Buscar autor por nombre en la base de datos
- ✅ Listar autores por rango de años de nacimiento

---

## Acceso al proyecto

### Requisitos

- Java 17+
- Maven 3.x
- PostgreSQL 16+

### Configuración de la base de datos

1. Crea la base de datos en PostgreSQL:
   ```sql
   CREATE DATABASE literalura;
   ```

2. Configura tus credenciales en `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/literalura
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_password
   ```

### Instalación y ejecución

1. Clona el repositorio:
   ```bash
   git clone https://github.com/xsp4ce/ebook-api.git
   cd ebook-api
   ```

2. Compila y ejecuta:
   ```bash
   ./mvnw spring-boot:run
   ```

### Ejemplo de uso

```
========== LiterAlura ==========
1 - Buscar libro por título
2 - Listar todos los libros
3 - Listar autores
4 - Listar autores vivos en un año
5 - Listar libros por idioma
6 - Estadísticas de descargas
7 - Top 10 libros más descargados
8 - Buscar autor por nombre
9 - Listar autores por rango de años
0 - Salir
=================================

Elige una opción: 1
Ingresa el título del libro a buscar: frankenstein

Libro guardado exitosamente:
----- LIBRO -----
Título    : Frankenstein
Autor     : Shelley, Mary Wollstonecraft
Idioma    : en
Descargas : 78905
-----------------

Elige una opción: 6

===== Estadísticas de descargas =====
Total de libros  : 10
Total descargas  : 450,320
Promedio         : 45,032.00
Máximo           : 78,905
Mínimo           : 1,200
=====================================
```

---

## Tecnologías utilizadas

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 17 | Lenguaje principal |
| Spring Boot | 4.0.3 | Framework principal |
| Spring Data JPA | (gestionado) | Persistencia y repositorios |
| PostgreSQL | 16+ | Base de datos relacional |
| Jackson Databind | 2.16.1 | Parseo de respuestas JSON |
| Java HttpClient | (built-in) | Peticiones HTTP a Gutendex |
| Gutendex API | — | Fuente de metadatos de libros |
| Maven | 3.x | Gestión de dependencias y build |

---

## Autor

| [<img src="https://avatars.githubusercontent.com/xsp4ce" width=80><br><sub>xsp4ce</sub>](https://github.com/xsp4ce) |
|:---:|
