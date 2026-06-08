package org.iesra.app

import org.iesra.exception.EntidadNoEncontradaException
import org.iesra.model.Reserva
import org.iesra.service.ClienteService
import org.iesra.service.IncidenciaService
import org.iesra.service.ReservaService
import java.time.LocalDate

/**
 * Proceso interactivo de check-out.
 * Finaliza una reserva activa, permite reportar incidencias y realizar el pago pendiente.
 * @param clienteService Servicio de clientes
 * @param reservaService Servicio de reservas
 * @param incidenciaService Servicio de incidencias
 */
fun checkout(clienteService: ClienteService, reservaService: ReservaService, incidenciaService: IncidenciaService) {
    try {
        println("\n--- CHECKOUT ---")
        val hoy = LocalDate.now()
        val activas = reservaService.listarReservas().filter {
            it.estado != Reserva.ESTADO_CANCELADA && it.estado != Reserva.ESTADO_FINALIZADA
            && !it.fechaEntrada.isAfter(hoy)
        }
        if (activas.isEmpty()) {
            println("No hay reservas activas para hoy")
            return
        }
        println("Reservas activas:")
        activas.forEach { r ->
            val nombre = try {
                clienteService.buscarCliente(r.idCliente).nombre
            } catch (e: Exception) { r.idCliente }
            println("  ID: ${r.id} | Cliente: $nombre | Hab: ${r.numeroHabitacion} | Ent: ${r.fechaEntrada} | Sal: ${r.fechaSalida} | Pagada: ${if (r.pagada) "Si" else "No"}")
        }
        print("\nID de la reserva a finalizar: ")
        val id = readlnOrNull()?.trim()?.toIntOrNull()
            ?: throw IllegalArgumentException("ID no valido")
        val reserva = reservaService.buscarReserva(id)

        println("\nReserva seleccionada:")
        mostrarReserva(reserva, clienteService)

        print("\nReportar incidencia durante la estancia? (s/n): ")
        if (readlnOrNull()?.trim()?.lowercase() == "s") {
            print("Descripcion de la incidencia: ")
            val descripcion = readlnOrNull()?.trim() ?: ""
            val incidencia = incidenciaService.reportarIncidencia(reserva.numeroHabitacion, descripcion)
            println("Incidencia reportada correctamente. ID: ${incidencia.id}")
        }

        if (!reserva.pagada) {
            print("La reserva no esta pagada. Desea pagar ahora? (s/n): ")
            if (readlnOrNull()?.trim()?.lowercase() == "s") {
                reservaService.marcarComoPagada(reserva.id)
                println("Reserva pagada correctamente")
            }
        }

        reservaService.finalizarReserva(reserva.id)
        val nombreCliente = try {
            clienteService.buscarCliente(reserva.idCliente).nombre
        } catch (e: Exception) { reserva.idCliente }
        println("Checkout completado correctamente. ¡Hasta pronto, $nombreCliente!")
    } catch (e: EntidadNoEncontradaException) {
        println(e.message)
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}
