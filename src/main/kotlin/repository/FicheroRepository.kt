package org.iesra.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.iesra.exception.FicheroException
import org.iesra.model.Cliente
import org.iesra.model.Incidencia
import org.iesra.model.Reserva
import java.io.File
import java.time.LocalDate

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

class FicheroRepository {

    private val gson = Gson()

    fun exportarClientesAJson(clientes: List<Cliente>, ruta: String) {
        try {
            val json = gson.toJson(clientes)
            File(ruta).writeText(json)
        } catch (e: Exception) {
            throw FicheroException("Error al exportar clientes a $ruta", e)
        }
    }

    fun importarClientesDeJson(ruta: String): List<Cliente> {
        try {
            val json = File(ruta).readText()
            val tipo = object : TypeToken<List<Cliente>>() {}.type
            return gson.fromJson(json, tipo)
        } catch (e: Exception) {
            throw FicheroException("Error al importar clientes de $ruta", e)
        }
    }

    fun exportarReservasAJson(reservas: List<Reserva>, ruta: String) {
        try {
            val json = gson.toJson(reservas)
            File(ruta).writeText(json)
        } catch (e: Exception) {
            throw FicheroException("Error al exportar reservas a $ruta", e)
        }
    }

    fun exportarIncidenciasAJson(incidencias: List<Incidencia>, ruta: String) {
        try {
            val json = gson.toJson(incidencias)
            File(ruta).writeText(json)
        } catch (e: Exception) {
            throw FicheroException("Error al exportar incidencias a $ruta", e)
        }
    }

    fun importarDatosPrueba(ruta: String): DatosImportacion {
        try {
            val json = File(ruta).readText()
            return gson.fromJson(json, DatosImportacion::class.java)
        } catch (e: Exception) {
            throw FicheroException("Error al importar datos de prueba de $ruta", e)
        }
    }

    fun generarInformeReservas(reservas: List<Reserva>, ruta: String) {
        try {
            val lineas = mutableListOf("INFORME DE RESERVAS - HOTEL MALIGNO")
            lineas.add("=" .repeat(50))
            lineas.add("Total de reservas: ${reservas.size}")
            lineas.add("")
            for (reserva in reservas) {
                lineas.add("ID: ${reserva.id} | Cliente: ${reserva.idCliente} | Habitacion: ${reserva.numeroHabitacion}")
                lineas.add("  Entrada: ${reserva.fechaEntrada} | Salida: ${reserva.fechaSalida} | Estado: ${reserva.estado}")
                lineas.add("")
            }
            File(ruta).writeText(lineas.joinToString("\n"))
        } catch (e: Exception) {
            throw FicheroException("Error al generar informe en $ruta", e)
        }
    }
}
