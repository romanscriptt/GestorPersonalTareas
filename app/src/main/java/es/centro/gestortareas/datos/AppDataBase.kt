package es.centro.gestortareas.datos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import es.centro.gestortareas.modelo.Tarea

@Database(entities = [Tarea::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

    // Conecta esta base de datos con el encargado de dar las órdenes (TareaDao)
    abstract fun tareaDao(): TareaDao

    // Este bloque se asegura de que solo exista UNA única libreta abierta en toda la app
    companion object {

        // Guarda en memoria la libreta una vez abierta para reutilizarla siempre.
        // "@Volatile" hace que toda la app vea al instante si ya está abierta o no
        @Volatile
        private var INSTANCIA: AppDataBase? = null

        fun obtenerInstancia(contexto: Context): AppDataBase {
            // Si ya estaba abierta de antes, la devuelve directamente.
            // Si es la primera vez que se abre, la fabrica en este momento
            return INSTANCIA ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    contexto.applicationContext,
                    // Le dice qué molde de base de datos tiene que fabricar
                    AppDataBase::class.java,
                    // Nombre real que tendrá el archivo guardado dentro de la memoria del teléfono
                    "tareas_db"
                ).build()

                // La deja apuntada como "ya abierta" para las próximas veces
                INSTANCIA = instancia
                instancia
            }
        }
    }
}