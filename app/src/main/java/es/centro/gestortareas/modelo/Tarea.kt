package es.centro.gestortareas.modelo

// Importamis asi las herramientas de la base de datos para poder guardar esta ficha en el teléfono
import androidx.room.Entity
import androidx.room.PrimaryKey

// Esta etiqueta la utilizo para que el diga a la base de datos del móvil que cree una tabla llamada "tareas" en si como sis creo una fila en el excel
@Entity(tableName = "tareas")
data class Tarea(

    // Al poner autogenerate = true, el móvil inventa el número solo a la hora que creo la tarea
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // El nombre principal de la tarea como tipo "Examen IMPORTANTE"
    val titulo: String,

    // Un texto extra para apuntar detalles más largos, tambien lo he hecho por si queda vacio no hay nada.
    val descripcion: String = "",

    // Sirve para marcar si es muy urgente como ejemplo si es alta, media, baja ....
    val prioridad: String = "Media",

    // Es como una casilla de verificación, si es true esta hecha y no es false
    val completada: Boolean = false
)