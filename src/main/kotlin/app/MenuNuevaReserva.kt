package org.iesra.app

import org.iesra.model.Cliente
import org.iesra.service.ClienteService
import org.iesra.service.ReservaService
import org.iesra.validator.Validador
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Proceso interactivo de check-in.
 * Solicita los datos del cliente y de la reserva, y la registra en el sistema.
 * @param clienteService Servicio de clientes
 * @param reservaService Servicio de reservas
 */
fun nuevaReserva(clienteService: ClienteService, reservaService: ReservaService) {
    val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    println("\n--- NUEVA RESERVA (CHECK-IN) ---")
    println("Introduzca los datos del cliente:")

    try {
        print("NIF: ")
        val nif = readlnOrNull()?.trim() ?: ""
        Validador.comprobarNif(nif)
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
