package es.centro.gestortareas.datos

// Traemos las órdenes de la base de datos (leer, guardar, cambiar y borrar)
import androidx.room.Dao
import androidx.room.Delete //Borrar
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import es.centro.gestortareas.modelo.Tarea
// Herramienta para que la lista de tareas se actualice en directo en la pantalla
import kotlinx.coroutines.flow.Flow

// Esta etiqueta es donde dice que aqui dentro están las órdenes para manejar las tareas
@Dao
interface TareaDao {
    // Aqui coge toda las filas de la tabla tarea y las ordena con el id mas alto hasta la mas antigua
    @Query("SELECT * FROM tareas ORDER BY id DESC")
    //Lo que hago con Flow es qu elo devuelvo,funciona asi como una conexion directa si cambia algo la pantalla se actuliza
    fun obtenerTodas(): Flow<List<Tarea>>

    // Se guarda la tarea, si hubiera el mismo numero, la reemeplazo si que de errror
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // Suspend hace que este trabajo se haga por detras de la pantalla sin congelarse
    suspend fun insertar(tarea: Tarea)

    // Es lo mismo qu ela funcion de arriba, peor guarda todas de golpe, ya que os servira para cuando lo descarguemos las tareas en internet
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVarias(tareas: List<Tarea>)

    // Busca una traea que ya etsaba guardada y la actualiza
    @Update
    suspend fun actualizar(tarea: Tarea)

    // Borra para siempre la memoria del telefono la tarea que nosotros le indicamos
    @Delete
    suspend fun eliminar(tarea: Tarea)
}