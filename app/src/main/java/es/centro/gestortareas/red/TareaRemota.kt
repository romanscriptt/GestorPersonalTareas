package es.centro.gestortareas.red

// Traemos la etiqueta que sirve para traducir los nombres que vienen en inglés desde Internet
import com.google.gson.annotations.SerializedName
import es.centro.gestortareas.modelo.Tarea

// Este molde recoge los datos tal y como llegan desde Internet (en formato JSON)
data class TareaRemota(

    val id: Int,

    // En Internet este campo se llama "title" (en inglés); con @SerializedName lo traducimos a "titulo"
    @SerializedName("title")
    val titulo: String,

    @SerializedName("completed")
    val completada: Boolean
) {
    // Esta función convierte la tarea descargada de Internet en una "Tarea" lista para nuestra base de datos
    fun aTareaLocal(): Tarea {
        return Tarea(
            titulo = titulo.replaceFirstChar { it.uppercase() },
            // Le añadimos una descripción automática para saber que viene de ejemplo
            descripcion = "Tarea de ejemplo importada desde Internet",
            prioridad = "Media",
            // Mantenemos si venía marcada como completada o pendiente
            completada = completada
        )
    }
}