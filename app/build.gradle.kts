// Aquí encendemos los motores principales que va a usar nuestra aplicación
plugins {
    // Motor básico para construir una aplicación de Android
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

// Ficha técnica de la aplicación
android {
    namespace = "es.centro.gestortareas"

    // Versión más moderna de Android
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "es.centro.gestortareas"
        minSdk = 26
        // Está pensada y probada para funcionar hasta Android 37
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Limpia y optimiza el código para que la app pese menos y vaya más rápida
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }

    // Versión del lenguaje Java que usa por debajo para entenderse con Android
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // Activamos el sistema moderno para dibujar botones, listas y textos en pantalla
    buildFeatures {
        compose = true
    }
}

// Lista de piezas y herramientas extra que le enchufamos a nuestra aplicación
dependencies {
    // Controla que todas las piezas de diseño usen versiones compatibles entre sí
    implementation(platform(libs.androidx.compose.bom))
    // Permite meter nuestras pantallas modernas dentro de la pantalla principal del móvil
    implementation(libs.androidx.activity.compose)
    // Nos da botones, tarjetas, interruptores y ventanas con el diseño oficial de Google
    implementation(libs.androidx.compose.material3)
    // Herramientas base para colocar textos, filas y columnas en pantalla
    implementation(libs.androidx.compose.ui)
    // Herramientas para pintar colores, formas y gráficos
    implementation(libs.androidx.compose.ui.graphics)
    // Permite ver una vista previa de cómo queda la pantalla sin abrir el emulador
    implementation(libs.androidx.compose.ui.tooling.preview)
    // Atajos cómodos para escribir menos código en Android
    implementation(libs.androidx.core.ktx)
    // Ayuda a la app a saber cuándo se abre, se minimiza o se cierra la pantalla
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // --- CONEXIÓN ENTRE PANTALLA Y DATOS ---
    // Hace que la lista en pantalla se refresque sola al instante cuando guardas, editas o borras una tarea
    implementation(libs.androidx.lifecycle.runtime.compose)

    // --- BASE DE DATOS EN EL MÓVIL  ---
    // Instala el motor para guardar las tareas en la memoria del teléfono y que no se pierdan
    implementation(libs.androidx.room.runtime)
    // Permite guardar y leer tareas en segundo plano sin que la pantalla se quede congelada
    implementation(libs.androidx.room.ktx)
    // Ordena al ayudante automático que escriba el código interno de la base de datos
    ksp(libs.androidx.room.compiler)

    // --- MEMORIA DE AJUSTES  ---
    // Recuerda si dejaste activado el interruptor de "Mostrar solo pendientes" al cerrar la app
    implementation(libs.androidx.datastore.preferences)

    // --- CONEXIÓN A INTERNET  ---
    // Herramienta para conectarse a Internet y traer las tareas de ejemplo
    implementation(libs.retrofit)
    // Traductor que convierte lo que llega de Internet en tareas que entiende nuestra app
    implementation(libs.retrofit.converter.gson)

    // --- ICONOS EXTRA ---
    // Añade iconos listos para usar en los botones (papelera para borrar, lápiz para editar, etc.)
    implementation(libs.androidx.compose.material.icons.extended)

    // --- HERRAMIENTAS DE PRUEBAS (VIENEN POR DEFECTO) ---
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}