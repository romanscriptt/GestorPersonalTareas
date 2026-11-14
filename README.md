# Gestor Personal de Tareas

Aplicación móvil para **Android** desarrollada con **Kotlin** y **Jetpack Compose** que permite organizar tareas diarias, asignarles una prioridad, marcarlas como completadas, filtrar las pendientes y conservar toda la información en la memoria del teléfono, incluso después de cerrar la aplicación.

Proyecto de la práctica del módulo **Programación Multimedia y Dispositivos Móviles (PMDM)**.

---
 
## Índice
 
1. [Descripción](#1-descripción)
2. [Estado del proyecto](#2-estado-del-proyecto)
3. [Tecnologías y bibliotecas](#3-tecnologías-y-bibliotecas)
4. [Arquitectura y estructura de carpetas](#4-arquitectura-y-estructura-de-carpetas)
5. [Documentación técnica](#5-documentación-técnica)
   - [5.1 Configuración del proyecto](#51-configuración-del-proyecto)
   - [5.2 Modelo de datos: `Tarea`](#52-modelo-de-datos-tarea)
   - [5.3 Acceso a datos: `TareaDao`](#53-acceso-a-datos-tareadao)

---

## 1. Descripción
 
El **Gestor Personal de Tareas** está pensado para registrar tareas personales, académicas o profesionales. Funcionalidades previstas:
 
- Crear, consultar, editar y eliminar tareas.
- Asignar una **prioridad** (baja, media o alta) y marcar cada tarea como **completada**.
- Mostrar **solo las pendientes** mediante un interruptor cuya elección se recuerda entre ejecuciones.
- Guardar las tareas de forma **permanente** en una base de datos local.
- Consultar tareas de ejemplo de un servicio web (JSONPlaceholder).
- Compartir una tarea con otras aplicaciones instaladas en el dispositivo.

---

## 2. Estado del proyecto

El proyecto ya tiene lista toda la configuración inicial, los recursos visuales y el motor completo para guardar y leer tareas en el móvil:

- [x] **Configuración del proyecto (`Gradle`):** Instaladas las herramientas de pantalla (Jetpack Compose y Material 3), iconos extra, base de datos en el teléfono (Room + KSP), memoria de ajustes (DataStore) y conexión a Internet (Retrofit).
- [x] **Permisos y recursos (`AndroidManifest.xml` y `res`):** Configurado el permiso de Internet, los textos de la app en español (`strings.xml`) y el icono vectorial (`ic_tareas.xml`).
- [x] **Ficha de datos (`modelo/Tarea.kt`):** Define cómo es cada tarea (número identificador, título, descripción, prioridad y si está completada o pendiente) y crea la tabla `tareas`.
- [x] **Encargado de órdenes (`datos/TareaDao.kt`):** Contiene las funciones para leer la lista de tareas en directo (`Flow`), guardar una o varias tareas de golpe, modificarlas y borrarlas.
- [ ] **Base de datos local (`datos/AppDatabase.kt`):** Crea y abre la libreta principal (`tareas_db`) dentro de la memoria del teléfono asegurando que solo haya una conexión abierta a la vez.
- [ ] **Memoria de ajustes (`preferencias/`):** Pendiente de crear.
- [ ] **Conexión a Internet (`red/`):** Pendiente de crear.
- [ ] **Coordinador (`TareasViewModel.kt`) y pantalla principal (`MainActivity.kt`):** Pendiente de crear.

---

## 3. Tecnologías y bibliotecas
 
| Tecnología | Versión | Para qué se usa |
|---|---|---|
| **Kotlin** | 2.2.10 | Lenguaje de programación |
| **Jetpack Compose** + **Material 3** | BOM `2026.02.01` | Construcción de la interfaz |
| **Room** (`runtime`, `ktx`, `compiler`) | 2.8.4 | Base de datos local (SQLite) |
| **KSP** | 2.2.10-2.0.2 | Genera el código interno de Room al compilar |
| **DataStore Preferences** | 1.1.7 | Guardar la preferencia del filtro |
| **Retrofit** + **converter-gson** | 3.0.0 | Peticiones HTTP y conversión de JSON |
| **Lifecycle Runtime Compose** | 2.11.0 | Observar flujos de datos desde la interfaz respetando el ciclo de vida |
| **Material Icons Extended** | (según BOM) | Iconos de las acciones (editar, borrar, etc.) |
 
Versión mínima de Android: **API 26** (Android 8.0).

---

## 4. Arquitectura y estructura de carpetas
El código se separa por responsabilidades: cada carpeta se ocupa de una sola cosa.
<img width="1654" height="271" alt="image" src="https://github.com/user-attachments/assets/d926ede1-485b-4b60-a088-663d573ddfc7" />

```text
es.centro.gestortareas/
├── modelo/
│   └── Tarea.kt            # Ficha de una tarea 
├── datos/
│   ├── TareaDao.kt         # leer, insertar, actualizar y eliminar
│   └── AppDataBase.kt      # Construye y abre la base de datos "tareas_db"              
└── MainActivity.kt        
```
---
 
## 5. Documentación técnica
 
### 5.1 Configuración del proyecto
 
Las versiones se centralizan en el catálogo `gradle/libs.versions.toml`:

```toml
[versions]
kotlin    = "2.2.10"
ksp       = "2.2.10-2.0.2"   # debe ser compatible con la versión de Kotlin
room      = "2.8.4"
datastore = "1.1.7"
retrofit  = "3.0.0"
```
y se usan en `app/build.gradle.kts`:

```kotlin
dependencies {
    // Base de datos local (Room)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)          // genera el código de Room al compilar
 
    // Preferencias del usuario
    implementation(libs.androidx.datastore.preferences)
 
    // Comunicaciones
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
 
    // Observación de datos desde la interfaz
    implementation(libs.androidx.lifecycle.runtime.compose)
}
```
**Permisos** (`AndroidManifest.xml`): únicamente `INTERNET`, necesario para consultar el servicio web. Es un permiso normal que no requiere diálogo al usuario. No se solicitan permisos innecesarios (SMS, Bluetooth, contactos o ubicación).
 
```xml
<uses-permission android:name="android.permission.INTERNET" />
```
 
---

## 5.2 Modelo de datos: `Tarea`
 
`modelo/Tarea.kt` define cómo es una tarea y, al estar anotada con `@Entity`, Room crea a partir de ella la tabla `tareas`.

```kotlin
@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,                      // lo asigna la base de datos al insertar
    val titulo: String,                   // nombre principal de la tarea
    val descripcion: String = "",         // texto opcional con más detalles
    val prioridad: String = "Media",      // "Baja", "Media" o "Alta"
    val completada: Boolean = false       // false = pendiente, true = terminada
)

```
| Campo | Tipo | Valor inicial | Descripción |
|---|---|---|---|
| `id` | `Int` | `0` | Identificador único. Con `0` se indica «aún sin guardar»; la base genera el número real |
| `titulo` | `String` | — (obligatorio) | Nombre de la tarea |
| `descripcion` | `String` | `""` | Detalles opcionales |
| `prioridad` | `String` | `"Media"` | Urgencia de la tarea |
| `completada` | `Boolean` | `false` | Estado de la tarea |

**Esquema de la tabla resultante en SQLite:**

| Columna | Tipo SQLite | Restricciones |
|---|---|---|
| `id` | `INTEGER` | Clave primaria, autoincremental |
| `titulo` | `TEXT` | `NOT NULL` |
| `descripcion` | `TEXT` | `NOT NULL` |
| `prioridad` | `TEXT` | `NOT NULL` |
| `completada` | `INTEGER` | `NOT NULL` (0 = pendiente, 1 = completada) |

Al ser una `data class`, para modificar una tarea se crea una **copia** que conserva el `id`, de modo que la base actualiza la misma fila:
 
```kotlin
val terminada = tarea.copy(completada = true)
```
