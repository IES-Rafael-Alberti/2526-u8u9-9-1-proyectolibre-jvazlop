package org.iesra.app

import org.iesra.exception.EntidadNoEncontradaException
import org.iesra.model.Cliente
import org.iesra.model.Reserva
import org.iesra.service.ClienteService
import org.iesra.service.ComentarioClienteService
import org.iesra.service.ReservaService
import org.iesra.util.ConexionH2
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun main() {
    val clienteService = ClienteService()
    val reservaService = ReservaService()
    val comentarioService = ComentarioClienteService()

    try {
        ConexionH2.inicializarTablas()
        println("Base de datos H2 inicializada correctamente")
    } catch (e: Exception) {
        println("Error al inicializar H2: ${e.message}")
        return
    }

    var menu = true
    while (menu) {
        println("\n===== HOTEL MALIGNO - SISTEMA DE RESERVAS =====")
        println("1. Nueva reserva (check-in)")
        println("2. Gestionar reservas")
        println("3. Gestionar clientes")
        println("4. Salir")
        print("Seleccione una opcion: ")

        when (readlnOrNull()?.trim()) {
            "1" -> nuevaReserva(clienteService, reservaService)
            "2" -> menuReservas(reservaService, clienteService)
            "3" -> menuClientes(clienteService, comentarioService)
            "4" -> menu = false
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

fun menuClientes(service: ClienteService, comentarioService: ComentarioClienteService) {
    var running = true
    while (running) {
        println("\n--- GESTION DE CLIENTES ---")
        println("1. Registrar nuevo cliente")
        println("2. Buscar cliente por NIF")
        println("3. Listar todos los clientes")
        println("4. Actualizar datos de cliente")
        println("5. Eliminar cliente")
        println("6. Registrar comentario de cliente")
        println("7. Listar comentarios de clientes")
        println("8. Volver al menu principal")
        print("Seleccione una opcion: ")

        when (readlnOrNull()?.trim()) {
            "1" -> {
                try {
                    print("NIF: ")
                    val nif = readlnOrNull()?.trim() ?: ""
                    print("Nombre: ")
                    val nombre = readlnOrNull()?.trim() ?: ""
                    print("Email: ")
                    val email = readlnOrNull()?.trim() ?: ""
                    print("Telefono: ")
                    val telefono = readlnOrNull()?.trim() ?: ""

                    val cliente = service.registrarCliente(nif, nombre, email, telefono)
                    println("Cliente registrado correctamente: ${cliente.nombre}")
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }
            "2" -> {
                try {
                    print("NIF del cliente: ")
                    val nif = readlnOrNull()?.trim() ?: ""
                    val cliente = service.buscarCliente(nif)
                    println("NIF: ${cliente.id}")
                    println("Nombre: ${cliente.nombre}")
                    println("Email: ${cliente.email}")
                    println("Telefono: ${cliente.telefono}")
                } catch (e: EntidadNoEncontradaException) {
                    println(e.message)
                }
            }
            "3" -> {
                val clientes = service.listarClientes()
                if (clientes.isEmpty()) {
                    println("No hay clientes registrados")
                } else {
                    println("Lista de clientes:")
                    clientes.forEach { c ->
                        println("  ${c.id} - ${c.nombre} (${c.email})")
                    }
                }
            }
            "4" -> {
                try {
                    print("NIF del cliente a actualizar: ")
                    val nif = readlnOrNull()?.trim() ?: ""
                    print("Nuevo nombre: ")
                    val nombre = readlnOrNull()?.trim() ?: ""
                    print("Nuevo email: ")
                    val email = readlnOrNull()?.trim() ?: ""
                    print("Nuevo telefono: ")
                    val telefono = readlnOrNull()?.trim() ?: ""

                    service.actualizarCliente(nif, nombre, email, telefono)
                    println("Cliente actualizado correctamente")
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }
            "5" -> {
                try {
                    print("NIF del cliente a eliminar: ")
                    val nif = readlnOrNull()?.trim() ?: ""
                    if (service.eliminarCliente(nif)) {
                        println("Cliente eliminado correctamente")
                    }
                } catch (e: EntidadNoEncontradaException) {
                    println(e.message)
                }
            }
            "6" -> {
                try {
                    print("Comentario del cliente: ")
                    val nombre = readlnOrNull()?.trim() ?: ""
                    val comentario = comentarioService.registrarComentario(nombre)
                    println("Comentario registrado correctamente. ID: ${comentario.id}")
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }
            "7" -> {
                val visitas = comentarioService.listarComentarios()
                if (visitas.isEmpty()) {
                    println("No hay visitas registradas")
                } else {
                    println("Comentarios de clientes:")
                    visitas.forEach { v ->
                        println("  ${v.id} | ${v.nombreCliente} | ${v.fecha}")
                    }
                }
            }
            "8" -> running = false
            else -> println("Opcion no valida")
        }
    }
}
