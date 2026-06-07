package org.iesra.repository

import com.google.gson.Gson
import org.iesra.exception.FicheroException
import org.iesra.model.Cliente
import org.iesra.model.Incidencia
import org.iesra.model.Reserva
import java.io.File
import java.time.LocalDate

/**
 * Datos de importación provenientes de un archivo JSON.
 *
 * @property clientes Lista de clientes importados.
 * @property reservas Lista de reservas importadas en formato JSON.
 */
data class DatosImportacion(
    val clientes: List<Cliente>,
    val reservas: List<ReservaJson>
)

/**
 * Representa una reserva en el formato utilizado durante la importación desde JSON.
 *
 * @property idCliente Identificador del cliente asociado a la reserva.
 * @property numeroHabitacion Número de la habitación reservada.
 * @property fechaEntrada Fecha de entrada en formato texto.
 * @property fechaSalida Fecha de salida en formato texto.
 */
data class ReservaJson(
    val idCliente: String,
    val numeroHabitacion: Int,
    val fechaEntrada: String,
    val fechaSalida: String
)

/**
 * Repositorio para operaciones de exportación e importación de datos mediante archivos.
 * Permite exportar e importar datos en formato JSON y generar informes de texto.
 */
class FicheroRepository {

    private val gson = Gson()

    /**
     * Exporta una lista de clientes a un archivo JSON.
     *
     * @param clientes Lista de clientes a exportar.
     * @param ruta Ruta del archivo de destino.
     * @throws FicheroException si ocurre un error durante la exportación.
     */
    fun exportarClientesAJson(clientes: List<Cliente>, ruta: String) {
        try {
            val json = gson.toJson(clientes)
            File(ruta).writeText(json)
        } catch (e: Exception) {
            throw FicheroException("Error al exportar clientes a $ruta", e)
        }
    }

    /**
     * Genera un informe de texto con los datos de las reservas.
     *
     * @param reservas Lista de reservas a incluir en el informe.
     * @param ruta Ruta del archivo de destino.
     * @throws FicheroException si ocurre un error al generar el informe.
     */
    fun generarInformeReservas(reservas: List<Reserva>, ruta: String) {
        try {
            val lineas = mutableListOf("INFORME DE RESERVAS - GESTOR HOTEL")
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

    /**
     * Importa datos de prueba desde un archivo JSON.
     *
     * @param ruta Ruta del archivo JSON a importar.
     * @return Los datos importados en un objeto [DatosImportacion].
     * @throws FicheroException si ocurre un error durante la importación.
     */
    fun importarDatosPrueba(ruta: String): DatosImportacion {
        try {
            val json = File(ruta).readText()
            return gson.fromJson(json, DatosImportacion::class.java)
        } catch (e: Exception) {
            throw FicheroException("Error al importar datos de prueba de $ruta", e)
        }
    }

    /**
     * Exporta una lista de incidencias a un archivo de texto.
     *
     * @param incidencias Lista de incidencias a exportar.
     * @param ruta Ruta del archivo de destino.
     * @throws FicheroException si ocurre un error durante la exportación.
     */
    fun exportarIncidenciasATxt(incidencias: List<Incidencia>, ruta: String) {
        try {
            val lineas = mutableListOf("INFORME DE INCIDENCIAS - GESTOR HOTEL")
            lineas.add("=" .repeat(50))
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
