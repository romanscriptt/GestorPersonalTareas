# Gestor Personal de Tareas

Aplicación móvil para Android desarrollada con **Kotlin** y **Jetpack Compose** que permite organizar tareas diarias, asignar prioridades, filtrar pendientes y guardar toda la información directamente en la memoria del teléfono.

---

## Estado actual del proyecto (hasta la base de datos)

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

## Herramientas utilizadas

* **Lenguaje:** Kotlin (`2.2.10`)
* **Diseño de pantallas:** Jetpack Compose + Material Design 3
* **Base de datos en el móvil:** Room (`2.8.4`) con generador automático KSP
* **Ajustes rápidos (preparado):** DataStore Preferences (`1.1.7`)
* **Descarga de datos (preparado):** Retrofit (`3.0.0`) + Gson

---

## Estructura actual de carpetas

```text
es.centro.gestortareas/
├── modelo/
│   └── Tarea.kt          
├── datos/
│   ├── TareaDao.kt       
│   └── AppDatabase.kt    
├── preferencias/        
└── red/                 
