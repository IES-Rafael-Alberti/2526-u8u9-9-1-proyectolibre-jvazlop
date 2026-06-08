package org.iesra.repository

import com.google.gson.Gson
import org.iesra.exception.FicheroException
import org.iesra.model.Cliente
import java.io.File

data class DatosImportacion(
    val clientes: List<Cliente>,
    val reservas: List<ReservaJson>
)

data class ReservaJson(
    val idCliente: String,
    val numeroHabitacion: Int,
    val fechaEntrada: String,
    val fechaSalida: String
)

class ImportadorFicheros {

    private val gson = Gson()

    fun importarDatosPrueba(ruta: String): DatosImportacion {
        try {
            val json = File(ruta).readText()
            return gson.fromJson(json, DatosImportacion::class.java)
        } catch (e: Exception) {
            throw FicheroException("Error al importar datos de prueba de $ruta", e)
        }
    }
}
