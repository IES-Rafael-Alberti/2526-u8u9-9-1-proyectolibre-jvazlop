package org.iesra.repository

import com.google.gson.Gson
import org.iesra.exception.FicheroException
import org.iesra.model.Cliente
import org.iesra.model.Incidencia
import org.iesra.model.Reserva
import java.io.File

class ExportadorFicheros {

    private val gson = Gson()

    private fun asegurarDirectorio(ruta: String) {
        val archivo = File(ruta)
        archivo.parentFile?.mkdirs()
    }

    fun exportarClientesAJson(clientes: List<Cliente>, ruta: String) {
        try {
            asegurarDirectorio(ruta)
            val json = gson.toJson(clientes)
            File(ruta).writeText(json)
        } catch (e: Exception) {
            throw FicheroException("Error al exportar clientes a $ruta", e)
        }
    }

    fun generarInformeReservas(reservas: List<Reserva>, ruta: String) {
        try {
            asegurarDirectorio(ruta)
            val lineas = mutableListOf("INFORME DE RESERVAS - GESTOR HOTEL")
            lineas.add("=".repeat(50))
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

    fun exportarIncidenciasATxt(incidencias: List<Incidencia>, ruta: String) {
        try {
            asegurarDirectorio(ruta)
            val lineas = mutableListOf("INFORME DE INCIDENCIAS - GESTOR HOTEL")
            lineas.add("=".repeat(50))
            lineas.add("Total de incidencias: ${incidencias.size}")
            lineas.add("")
            for (i in incidencias) {
                lineas.add("Habitacion: ${i.numeroHabitacion} | Resuelta: ${if (i.resuelta) "Si" else "No"}")
                lineas.add("  Descripcion: ${i.descripcion}")
                lineas.add("  Fecha: ${i.fecha}")
                lineas.add("")
            }
            File(ruta).writeText(lineas.joinToString("\n"))
        } catch (e: Exception) {
            throw FicheroException("Error al exportar incidencias a $ruta", e)
        }
    }
}
