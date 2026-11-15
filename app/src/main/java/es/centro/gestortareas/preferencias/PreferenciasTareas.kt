package es.centro.gestortareas.preferencias

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Al ponerlo fuera de la clase nos aseguramos de que solo exista una única copia abierta en toda la app
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ajustes_tareas")

// Esta clase se encarga de leer y guardar si el usuario quiere ver solo las tareas pendientes
class PreferenciasTareas(private val contexto: Context) {

    // (es como ponerle nombre a un cajón: "solo_pendientes")
    companion object {
        private val CLAVE_SOLO_PENDIENTES = booleanPreferencesKey("solo_pendientes")
    }

    // Si es la primera vez que se abre la app y aún no hay nada guardado, devuelve "false" (mostrar todas)
    val soloPendientes: Flow<Boolean> = contexto.dataStore.data.map { preferencias ->
        preferencias[CLAVE_SOLO_PENDIENTES] ?: false
    }

    // Guarda el nuevo estado cada vez que tocas el interruptor en pantalla.
    suspend fun guardarSoloPendientes(activar: Boolean) {
        contexto.dataStore.edit { preferencias ->
            preferencias[CLAVE_SOLO_PENDIENTES] = activar
        }
    }
}