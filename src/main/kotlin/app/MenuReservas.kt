package org.iesra.app

import org.iesra.exception.EntidadNoEncontradaException
import org.iesra.model.Reserva
import org.iesra.service.ClienteService
import org.iesra.service.ReservaService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Menú interactivo para la gestión de reservas.
 * Permite buscar, listar, modificar fechas, pagar, cancelar y eliminar reservas.
 * @param reservaService Servicio de reservas
 * @param clienteService Servicio de clientes
 */
fun menuReservas(reservaService: ReservaService, clienteService: ClienteService) {
    val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    var menu = true

    while (menu) {
        println("\n--- GESTION DE RESERVAS ---")
        println("1. Buscar reservas por cliente")
        println("2. Buscar reservas por fecha")
        println("3. Listar todas las reservas")
        println("4. Modificar fechas")
        println("5. Marcar como pagada")
        println("6. Cancelar reserva")
        println("7. Eliminar reserva")
        println("8. Volver al menu principal")
        print("Seleccione una opcion: ")

        when (readlnOrNull()?.trim()) {
            "1" -> {
                try {
                    print("NIF del cliente: ")
                    val nif = readlnOrNull()?.trim() ?: ""
                    val reservas = reservaService.listarReservasPorCliente(nif)
                    if (reservas.isEmpty()) {
                        println("El cliente no tiene reservas")
                    } else {
                        reservas.forEach { mostrarReserva(it, clienteService) }
                    }
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }
            "2" -> {
                try {
                    print("Introduzca el dia (dd/MM/yyyy): ")
                    val dia = LocalDate.parse(readlnOrNull()?.trim(), formatoFecha)
                    val reservas = reservaService.listarReservas().filter {
                        (it.fechaEntrada == dia || it.fechaSalida == dia) && it.estado != Reserva.ESTADO_CANCELADA
                    }
                    if (reservas.isEmpty()) {
                        println("No hay reservas para el $dia")
                    } else {
                        println("Reservas para el $dia:")
                        reservas.forEach { mostrarReserva(it, clienteService) }
                    }
                } catch (e: DateTimeParseException) {
                    println("Error: Formato de fecha incorrecto (use dd/MM/yyyy)")
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }
            "3" -> {
                val reservas = reservaService.listarReservas()
                if (reservas.isEmpty()) {
                    println("No hay reservas registradas")
                } else {
                    println("Listado completo de reservas:")
                    reservas.forEach { mostrarReserva(it, clienteService) }
                }
            }
            "4" -> modificarFechasReserva(reservaService, clienteService, formatoFecha)
            "5" -> pagarReserva(reservaService, clienteService)
            "6" -> cancelarReserva(reservaService, clienteService)
            "7" -> eliminarReserva(reservaService, clienteService)
            "8" -> menu = false
            else -> println("Opcion no valida")
        }
    }
}

/**
 * Solicita al usuario un NIF de cliente, lista sus reservas y permite seleccionar una.
 * @param reservaService Servicio de reservas
 * @param clienteService Servicio de clientes
 * @return La reserva seleccionada, o null si no se encontró ninguna
 */
fun seleccionarReserva(reservaService: ReservaService, clienteService: ClienteService): Reserva? {
    print("NIF del cliente: ")
    val nif = readlnOrNull()?.trim() ?: return null
    val reservas = reservaService.listarReservasPorCliente(nif)
    if (reservas.isEmpty()) {
        println("El cliente no tiene reservas")
        return null
    }
    println("Reservas de ${clienteService.buscarCliente(nif).nombre}:")
    reservas.forEach { mostrarReserva(it, clienteService) }
    if (reservas.size == 1) return reservas.first()
    print("ID de la reserva: ")
    val id = readlnOrNull()?.trim()?.toIntOrNull() ?: return null
    return reservaService.buscarReserva(id)
}

/**
 * Permite modificar las fechas de entrada y salida de una reserva seleccionada.
 * @param reservaService Servicio de reservas
 * @param clienteService Servicio de clientes
 * @param formatoFecha Formato de fecha (dd/MM/yyyy)
 */
fun modificarFechasReserva(reservaService: ReservaService, clienteService: ClienteService, formatoFecha: DateTimeFormatter) {
    try {
        val reserva = seleccionarReserva(reservaService, clienteService) ?: return
        println("Fechas actuales: ${reserva.fechaEntrada} -> ${reserva.fechaSalida}")
        print("Nueva fecha de entrada (dd/MM/yyyy): ")
        val nuevaEnt = LocalDate.parse(readlnOrNull()?.trim(), formatoFecha)
        print("Nueva fecha de salida (dd/MM/yyyy): ")
        val nuevaSal = LocalDate.parse(readlnOrNull()?.trim(), formatoFecha)
        reservaService.modificarFechas(reserva.id, nuevaEnt, nuevaSal)
        println("Fechas actualizadas correctamente")
    } catch (e: DateTimeParseException) {
        println("Error: Formato de fecha incorrecto")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

/**
 * Marca una reserva seleccionada como pagada.
 * @param reservaService Servicio de reservas
 * @param clienteService Servicio de clientes
 */
fun pagarReserva(reservaService: ReservaService, clienteService: ClienteService) {
    try {
        val reserva = seleccionarReserva(reservaService, clienteService) ?: return
        reservaService.marcarComoPagada(reserva.id)
        println("Reserva marcada como pagada correctamente")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

/**
 * Cancela una reserva seleccionada.
 * @param reservaService Servicio de reservas
 * @param clienteService Servicio de clientes
 */
fun cancelarReserva(reservaService: ReservaService, clienteService: ClienteService) {
    try {
        val reserva = seleccionarReserva(reservaService, clienteService) ?: return
        reservaService.cancelarReserva(reserva.id)
        println("Reserva cancelada correctamente")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

/**
 * Elimina una reserva seleccionada del sistema.
 * @param reservaService Servicio de reservas
 * @param clienteService Servicio de clientes
 */
fun eliminarReserva(reservaService: ReservaService, clienteService: ClienteService) {
    try {
        val reserva = seleccionarReserva(reservaService, clienteService) ?: return
        if (reservaService.eliminarReserva(reserva.id)) {
            println("Reserva eliminada correctamente")
        }
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

/**
 * Muestra por pantalla los datos de una reserva.
 * @param reserva Reserva a mostrar
 * @param clienteService Servicio de clientes para obtener el nombre del cliente
 */
fun mostrarReserva(reserva: Reserva, clienteService: ClienteService) {
    val nombreCliente = try {
        clienteService.buscarCliente(reserva.idCliente).nombre
    } catch (e: Exception) { reserva.idCliente }

    val huespedes = "${reserva.numPersonas} persona${if (reserva.numPersonas > 1) "s" else ""}" +
        if (reserva.segundoHuesped.isNotBlank()) " (${reserva.segundoHuesped})" else ""

    println("ID: ${reserva.id} | Cliente: $nombreCliente (${reserva.idCliente})")
    println("  Hab: ${reserva.numeroHabitacion} | $huespedes | Entrada: ${reserva.fechaEntrada} | Salida: ${reserva.fechaSalida}")
    println("  Estado: ${reserva.estado} | Pagada: ${if (reserva.pagada) "Si" else "No"}")
}
