package org.iesra.app

import org.iesra.service.ClienteService
import org.iesra.service.IncidenciaService
import org.iesra.service.ReservaService
import org.iesra.repository.ExportadorFicheros
import org.iesra.repository.ImportadorFicheros
import java.time.LocalDate

/**
 * Menú interactivo para la importación y exportación de datos.
 * Permite importar reservas desde JSON y exportar reservas, incidencias y clientes.
 * @param clienteService Servicio de clientes
 * @param reservaService Servicio de reservas
 * @param incidenciaService Servicio de incidencias
 * @param exportadorFicheros Exportador de ficheros
 * @param importadorFicheros Importador de ficheros
 */
fun menuImportarExportar(
    clienteService: ClienteService,
    reservaService: ReservaService,
    incidenciaService: IncidenciaService,
    exportadorFicheros: ExportadorFicheros,
    importadorFicheros: ImportadorFicheros
) {
    var menu = true
    while (menu) {
        println("\n--- IMPORTACION / EXPORTACION ---")
        println("1. Importar reservas (JSON)")
        println("2. Exportar reservas (TXT)")
        println("3. Exportar incidencias (TXT)")
        println("4. Exportar clientes (JSON)")
        println("5. Volver al menu principal")
        print("Seleccione una opcion: ")

        when (readlnOrNull()?.trim()) {
            "1" -> {
                try {
                    print("Nombre del fichero (ej: datos_prueba.json): ")
                    val nombreFic = readlnOrNull()?.trim() ?: ""
                    val ruta = if (nombreFic.startsWith("./")) nombreFic else "./$nombreFic"
                    val datos = importadorFicheros.importarDatosPrueba(ruta)
                    var clientesNuevos = 0
                    var clientesSaltados = 0

                    for (c in datos.clientes) {
                        if (clienteService.existeCliente(c.id)) {
                            clientesSaltados++
                        } else {
                            try {
                                clienteService.registrarCliente(c.id, c.nombre, c.email, c.telefono)
                                clientesNuevos++
                            } catch (e: Exception) {
                                println("  Error al importar cliente ${c.id}: ${e.message}")
                            }
                        }
                    }

                    var reservasCreadas = 0
                    for (r in datos.reservas) {
                        try {
                            val fechaEnt = LocalDate.parse(r.fechaEntrada)
                            val fechaSal = LocalDate.parse(r.fechaSalida)
                            reservaService.crearReserva(r.idCliente, r.numeroHabitacion, fechaEnt, fechaSal)
                            reservasCreadas++
                        } catch (e: Exception) {
                            println("  Error al crear reserva: ${e.message}")
                        }
                    }

                    println("Importados $clientesNuevos clientes nuevos ($clientesSaltados ya existian)")
                    println("Creadas $reservasCreadas reservas")
                } catch (e: Exception) {
                    println("Error al importar: ${e.message}")
                }
            }
            "2" -> {
                try {
                    val reservas = reservaService.listarReservas()
                    exportadorFicheros.generarInformeReservas(reservas, "./exportaciones/reservas.txt")
                    println("Informe de reservas generado en ./exportaciones/reservas.txt")
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }
            "3" -> {
                try {
                    val incidencias = incidenciaService.listarIncidencias()
                    exportadorFicheros.exportarIncidenciasATxt(incidencias, "./exportaciones/incidencias.txt")
                    println("Informe de incidencias generado en ./exportaciones/incidencias.txt")
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }
            "4" -> {
                try {
                    val clientes = clienteService.listarClientes()
                    exportadorFicheros.exportarClientesAJson(clientes, "./exportaciones/clientes.json")
                    println("Clientes exportados a ./exportaciones/clientes.json")
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }
            "5" -> menu = false
            else -> println("Opcion no valida")
        }
    }
}
