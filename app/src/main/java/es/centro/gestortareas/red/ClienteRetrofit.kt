package es.centro.gestortareas.red

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Aquí definimos qué dirección exacta vamos a pedirle al servidor de Internet
interface TareasApi {

    // Hace una petición de lectura (GET) a la ruta "todos?_limit=5" para traer 5 tareas de ejemplo.
    @GET("todos?_limit=5")
    suspend fun obtenerTareasEjemplo(): List<TareaRemota>
}

// Este objeto prepara y deja lista la conexión con el servidor para usarla desde cualquier parte
object ClienteRetrofit {

    private const val URL_BASE = "https://jsonplaceholder.typicode.com/"

    // Construye el mensajero una sola vez cuando se necesita por primera vez ("by lazy")
    val api: TareasApi by lazy {
        Retrofit.Builder()
            // Le indicamos a qué página web tiene que conectarse
            .baseUrl(URL_BASE)
            // Le añadimos el traductor Gson para convertir el texto de Internet en objetos TareaRemota
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            // Conecta esa configuración con nuestra orden "obtenerTareasEjemplo"
            .create(TareasApi::class.java)
    }
}