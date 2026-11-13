// Este es el archivo general de configuración para todo mi proyecto
plugins {
    // Prepara las herramientas básicas para construir una aplicación de Android
    alias(libs.plugins.android.application) apply false

    // Prepara las herramientas para dibujar las pantallas de la app
    alias(libs.plugins.kotlin.compose) apply false

    // Deja preparado el ayudante automático que fabricará nuestra base de datos
    // (pone apply false porque aquí solo lo dejamos listo
    alias(libs.plugins.ksp) apply false
}