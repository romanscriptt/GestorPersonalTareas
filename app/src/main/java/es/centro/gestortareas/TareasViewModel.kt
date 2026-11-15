package es.centro.gestortareas.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.centro.gestortareas.datos.AppDataBase
import es.centro.gestortareas.modelo.Tarea
import es.centro.gestortareas.preferencias.PreferenciasTareas
import es.centro.gestortareas.red.ClienteRetrofit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TareasViewModel(application: Application) : AndroidViewModel(application) {

    // Conectamos con la base de datos Room y con las preferencias DataStore
    private val dao = AppDataBase.obtenerInstancia(application).tareaDao()
    private val preferencias = PreferenciasTareas(application)

    // Estado del interruptor "Solo pendientes" guardado en DataStore
    val soloPendientes: StateFlow<Boolean> = preferencias.soloPendientes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Combina en tiempo real la lista de Room con el filtro de DataStore
    val tareasVisible: StateFlow<List<Tarea>> = combine(
        dao.obtenerTodas(),
        soloPendientes
    ) { listaTareas, filtrarPendientes ->
        if (filtrarPendientes) {
            listaTareas.filter { !it.completada }
        } else {
            listaTareas
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estados para mostrar carga o avisos al descargar tareas de Internet
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _mensajeAviso = MutableStateFlow<String?>(null)
    val mensajeAviso: StateFlow<String?> = _mensajeAviso.asStateFlow()

    fun agregarTarea(titulo: String, descripcion: String, prioridad: String) {
        if (titulo.isBlank()) return
        viewModelScope.launch {
            val nueva = Tarea(
                titulo = titulo.trim(),
                descripcion = descripcion.trim(),
                prioridad = prioridad
            )
            dao.insertar(nueva)
        }
    }

    fun alternarCompletada(tarea: Tarea) {
        viewModelScope.launch {
            dao.actualizar(tarea.copy(completada = !tarea.completada))
        }
    }

    fun eliminarTarea(tarea: Tarea) {
        viewModelScope.launch {
            dao.eliminar(tarea)
        }
    }

    fun cambiarFiltroPendientes(activar: Boolean) {
        viewModelScope.launch {
            preferencias.guardarSoloPendientes(activar)
        }
    }

    fun importarTareasDeInternet() {
        viewModelScope.launch {
            _cargando.value = true
            _mensajeAviso.value = null
            try {
                val remotas = ClienteRetrofit.api.obtenerTareasEjemplo()
                val locales = remotas.map { it.aTareaLocal() }
                dao.insertarVarias(locales)
                _mensajeAviso.value = "Se han importado ${locales.size} tareas de ejemplo"
            } catch (e: Exception) {
                _mensajeAviso.value = "Error de conexión: comprueba Internet en el emulador"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun limpiarAviso() {
        _mensajeAviso.value = null
    }
}