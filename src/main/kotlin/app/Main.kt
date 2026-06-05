package org.iesra.app

import org.iesra.exception.EntidadNoEncontradaException
import org.iesra.model.Cliente
import org.iesra.model.Reserva
import org.iesra.service.ClienteService
import org.iesra.service.ReservaService
import org.iesra.util.ConexionH2
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun main() {
    val clienteService = ClienteService()
    val reservaService = ReservaService()

    try {
        ConexionH2.inicializarTablas()
        println("Base de datos H2 inicializada correctamente")
    } catch (e: Exception) {
        println("Error al inicializar H2: ${e.message}")
        return
    }

    var running = true
    while (running) {
        println("\n===== HOTEL MALIGNO - SISTEMA DE RESERVAS =====")
        println("1. Nueva reserva (check-in)")
        println("2. Gestionar reservas")
        println("3. Salir")
        print("Seleccione una opcion: ")

        when (readlnOrNull()?.trim()) {
            "1" -> nuevaReserva(clienteService, reservaService)
            "2" -> menuReservas(reservaService, clienteService)
            "3" -> running = false
            else -> println("Opcion no valida")
        }
    }
    println("Gracias por usar el sistema. Hasta luego!")
    ConexionH2.cerrarConexion()
}

fun nuevaReserva(clienteService: ClienteService, reservaService: ReservaService) {
    val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    println("\n--- NUEVA RESERVA (CHECK-IN) ---")
    println("Introduzca los datos del cliente:")

    try {
        print("NIF: ")
        val nif = readlnOrNull()?.trim() ?: ""
        var cliente: Cliente

        val existente = clienteService.existeCliente(nif)
        if (existente) {
            cliente = clienteService.buscarCliente(nif)
            println("Cliente encontrado: ${cliente.nombre} (${cliente.email})")
            print("Confirmar datos? (s/n): ")
            val confirmar = readlnOrNull()?.trim()?.lowercase()
            if (confirmar != "s") {
                print("Nombre: ")
                val nombre = readlnOrNull()?.trim() ?: ""
                print("Email: ")
                val email = readlnOrNull()?.trim() ?: ""
                print("Telefono: ")
                val telefono = readlnOrNull()?.trim() ?: ""
                cliente = clienteService.actualizarCliente(nif, nombre, email, telefono)
                println("Cliente actualizado correctamente")
            }
        } else {
            print("Nombre: ")
            val nombre = readlnOrNull()?.trim() ?: ""
            print("Email: ")
            val email = readlnOrNull()?.trim() ?: ""
            print("Telefono: ")
            val telefono = readlnOrNull()?.trim() ?: ""
            cliente = clienteService.obtenerOCrearCliente(nif, nombre, email, telefono)
            println("Nuevo cliente registrado correctamente")
        }

        println("\nDatos de la reserva:")

        print("Numero de habitacion: ")
        val numHab = readlnOrNull()?.trim()?.toIntOrNull()
            ?: throw IllegalArgumentException("Numero de habitacion no valido")

        print("Fecha de entrada (dd/MM/yyyy): ")
        val fechaEnt = LocalDate.parse(readlnOrNull()?.trim(), formatoFecha)

        print("Fecha de salida (dd/MM/yyyy): ")
        val fechaSal = LocalDate.parse(readlnOrNull()?.trim(), formatoFecha)

        print("Para cuantas personas?: ")
        val numPersonas = readlnOrNull()?.trim()?.toIntOrNull()
            ?: throw IllegalArgumentException("Numero de personas no valido")

        var segundoHuesped = ""
        if (numPersonas >= 2) {
            print("Nombre del segundo huesped (opcional, pulse Enter para omitir): ")
            segundoHuesped = readlnOrNull()?.trim() ?: ""
        }

        print("Desea pagar ahora? (s/n): ")
        val pagarAhora = readlnOrNull()?.trim()?.lowercase() == "s"

        val reserva = reservaService.crearReserva(nif, numHab, fechaEnt, fechaSal, pagarAhora, numPersonas, segundoHuesped)
        println("\nReserva creada correctamente con ID: ${reserva.id}")
        println("Cliente: ${cliente.nombre} | Habitacion: $numHab")
        println("Entrada: ${reserva.fechaEntrada} | Salida: ${reserva.fechaSalida}")
        println("Personas: $numPersonas${if (segundoHuesped.isNotBlank()) " ($segundoHuesped)" else ""} | Pagada: ${if (reserva.pagada) "Si" else "No"}")

    } catch (e: DateTimeParseException) {
        println("Error: Formato de fecha incorrecto (use dd/MM/yyyy)")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

fun menuReservas(reservaService: ReservaService, clienteService: ClienteService) {
    val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    var running = true

    while (running) {
        println("\n--- GESTION DE RESERVAS ---")
        println("1. Buscar reserva por cliente")
        println("2. Buscar reservas por fecha")
        println("3. Listar todas las reservas")
        println("4. Modificar fechas de una reserva")
        println("5. Marcar reserva como pagada")
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
            "8" -> running = false
            else -> println("Opcion no valida")
        }
    }
}

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

fun pagarReserva(reservaService: ReservaService, clienteService: ClienteService) {
    try {
        val reserva = seleccionarReserva(reservaService, clienteService) ?: return
        reservaService.marcarComoPagada(reserva.id)
        println("Reserva marcada como pagada correctamente")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

fun cancelarReserva(reservaService: ReservaService, clienteService: ClienteService) {
    try {
        val reserva = seleccionarReserva(reservaService, clienteService) ?: return
        reservaService.cancelarReserva(reserva.id)
        println("Reserva cancelada correctamente")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

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
