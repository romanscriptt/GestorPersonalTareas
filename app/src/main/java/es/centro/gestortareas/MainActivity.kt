package es.centro.gestortareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import es.centro.gestortareas.modelo.Tarea
import es.centro.gestortareas.viewmodel.TareasViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TareasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                PantallaPrincipalTareas(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipalTareas(viewModel: TareasViewModel) {
    val tareas by viewModel.tareasVisible.collectAsState()
    val soloPendientes by viewModel.soloPendientes.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val mensajeAviso by viewModel.mensajeAviso.collectAsState()

    var mostrarDialogoNueva by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Muestra un aviso abajo cuando se descargan tareas de Internet o hay error
    LaunchedEffect(mensajeAviso) {
        mensajeAviso?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            viewModel.limpiarAviso()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Gestor Personal de Tareas", fontWeight = FontWeight.Bold) },
                actions = {
                    // Botón para descargar las 5 tareas desde Retrofit
                    IconButton(
                        onClick = { viewModel.importarTareasDeInternet() },
                        enabled = !cargando
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Importar tareas de ejemplo"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogoNueva = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva tarea")
            }
        }
    ) { paddingInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .padding(horizontal = 16.dp)
        ) {
            // Interruptor conectado a DataStore para filtrar pendientes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Mostrar solo pendientes",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = soloPendientes,
                    onCheckedChange = { viewModel.cambiarFiltroPendientes(it) }
                )
            }

            if (cargando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (tareas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay tareas.\nPulsa + para crear una o ↻ arriba para importar ejemplos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(tareas, key = { it.id }) { tarea ->
                        TarjetaTarea(
                            tarea = tarea,
                            onCambiarEstado = { viewModel.alternarCompletada(tarea) },
                            onEliminar = { viewModel.eliminarTarea(tarea) }
                        )
                    }
                }
            }
        }

        if (mostrarDialogoNueva) {
            DialogoNuevaTarea(
                onCerrar = { mostrarDialogoNueva = false },
                onGuardar = { titulo, descripcion, prioridad ->
                    viewModel.agregarTarea(titulo, descripcion, prioridad)
                    mostrarDialogoNueva = false
                }
            )
        }
    }
}

@Composable
fun TarjetaTarea(
    tarea: Tarea,
    onCambiarEstado: () -> Unit,
    onEliminar: () -> Unit
) {
    val colorPrioridad = when (tarea.prioridad) {
        "Alta" -> Color(0xFFD32F2F)
        "Baja" -> Color(0xFF388E3C)
        else -> Color(0xFFF57C00)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = tarea.completada,
                onCheckedChange = { onCambiarEstado() }
            )

            Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (tarea.completada) TextDecoration.LineThrough else TextDecoration.None
                )

                if (tarea.descripcion.isNotBlank()) {
                    Text(
                        text = tarea.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Prioridad: ${tarea.prioridad}",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorPrioridad,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = onEliminar) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar tarea",
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun DialogoNuevaTarea(
    onCerrar: () -> Unit,
    onGuardar: (String, String, String) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var prioridad by remember { mutableStateOf("Media") }
    val opcionesPrioridad = listOf("Alta", "Media", "Baja")

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Nueva Tarea") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Prioridad:", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    opcionesPrioridad.forEach { opcion ->
                        FilterChip(
                            selected = prioridad == opcion,
                            onClick = { prioridad = opcion },
                            label = { Text(opcion) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onGuardar(titulo, descripcion, prioridad) },
                enabled = titulo.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) {
                Text("Cancelar")
            }
        }
    )
}