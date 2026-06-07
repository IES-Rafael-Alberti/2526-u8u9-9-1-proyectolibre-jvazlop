package org.iesra.app

import org.iesra.exception.EntidadNoEncontradaException
import org.iesra.model.Cliente
import org.iesra.model.Reserva
import org.iesra.service.ClienteService
import org.iesra.service.ComentarioClienteService
import org.iesra.service.IncidenciaService
import org.iesra.service.ReservaService
import org.iesra.repository.FicheroRepository
import org.iesra.util.ConexionH2
import org.iesra.validator.Validador
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Punto de entrada principal de la aplicación.
 * Inicializa los servicios, la base de datos H2 y ejecuta el bucle del menú principal.
 */
fun main() {
    val clienteService = ClienteService()
    val reservaService = ReservaService()
    val comentarioService = ComentarioClienteService()
    val incidenciaService = IncidenciaService()
    val ficheroRepo = FicheroRepository()

    try {
        ConexionH2.inicializarTablas()
        println("Base de datos H2 inicializada correctamente")
    } catch (e: Exception) {
        println("Error al inicializar H2: ${e.message}")
        return
    }

    var menu = true
    while (menu) {
        println("\n===== GESTOR HOTEL - SISTEMA DE RESERVAS =====")
        println("1. Nuevo check-in")
        println("2. Gestion de reservas")
        println("3. Gestion de clientes")
        println("4. Gestion de incidencias")
        println("5. Importacion / Exportacion de datos")
        println("6. Check-out")
        println("7. Salir")
        print("Seleccione una opcion: ")

        when (readlnOrNull()?.trim()) {
            "1" -> nuevaReserva(clienteService, reservaService)
            "2" -> menuReservas(reservaService, clienteService)
            "3" -> menuClientes(clienteService, comentarioService)
            "4" -> menuIncidencias(incidenciaService)
            "5" -> menuImportarExportar(clienteService, reservaService, incidenciaService, ficheroRepo)
            "6" -> checkout(clienteService, reservaService, incidenciaService)
            "7" -> menu = false
            else -> println("Opcion no valida")
        }
    }
}

/**
 * Menú interactivo para la gestión de incidencias.
 * Permite reportar, buscar, listar, filtrar por habitación, resolver y eliminar incidencias.
 * @param service Servicio de incidencias
 */
fun menuIncidencias(service: IncidenciaService) {
    var menu = true
    while (menu) {
        println("\n--- GESTION DE INCIDENCIAS ---")
        println("1. Reportar nueva incidencia")
        println("2. Buscar incidencia por ID")
        println("3. Listar todas las incidencias")
        println("4. Filtrar por habitacion")
        println("5. Incidencias pendientes")
        println("6. Resolver incidencia")
        println("7. Eliminar incidencia")
        println("8. Volver al menu principal")
        print("Seleccione una opcion: ")

        when (readlnOrNull()?.trim()) {
            "1" -> {
                try {
                    print("Numero de habitacion: ")
                    val numHab = readlnOrNull()?.trim()?.toIntOrNull()
                        ?: throw IllegalArgumentException("Numero de habitacion no valido")
                    print("Descripcion de la incidencia: ")
                    val descripcion = readlnOrNull()?.trim() ?: ""

                    val incidencia = service.reportarIncidencia(numHab, descripcion)
                    println("Incidencia reportada correctamente. ID: ${incidencia.id}")
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }
            "2" -> {
                try {
                    val incidencias = service.listarIncidencias()
                    if (incidencias.isEmpty()) {
                        println("No hay incidencias registradas")
                    } else {
                        println("Incidencias actuales:")
                        incidencias.forEach { i ->
                            println("  ${i.id} | Hab: ${i.numeroHabitacion} | ${i.descripcion} | Resuelta: ${if (i.resuelta) "Si" else "No"}")
                        }
                        print("ID de la incidencia a buscar: ")
                        val id = readlnOrNull()?.trim() ?: ""
                        val incidencia = service.buscarIncidencia(id)
                        println("Habitacion: ${incidencia.numeroHabitacion}")
                        println("Descripcion: ${incidencia.descripcion}")
                        println("Fecha: ${incidencia.fecha}")
                        println("Resuelta: ${if (incidencia.resuelta) "Si" else "No"}")
                    }
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }
            "3" -> {
                val incidencias = service.listarIncidencias()
                if (incidencias.isEmpty()) {
                    println("No hay incidencias registradas")
                } else {
                    incidencias.forEach { i ->
                        println("  ${i.id} | Hab: ${i.numeroHabitacion} | ${i.descripcion} | Resuelta: ${if (i.resuelta) "Si" else "No"}")
                    }
                }
            }
            "4" -> {
                try {
                    print("Numero de habitacion: ")
                    val numHab = readlnOrNull()?.trim()?.toIntOrNull()
                        ?: throw IllegalArgumentException("Numero de habitacion no valido")
                    val incidencias = service.listarIncidenciasPorHabitacion(numHab)
                    if (incidencias.isEmpty()) {
                        println("La habitacion no tiene incidencias")
                    } else {
                        incidencias.forEach { i ->
                            println("  ${i.id} | ${i.descripcion} | Resuelta: ${if (i.resuelta) "Si" else "No"}")
                        }
                    }
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }
            "5" -> {
                val incidencias = service.listarIncidenciasNoResueltas()
                if (incidencias.isEmpty()) {
                    println("No hay incidencias pendientes")
                } else {
                    incidencias.forEach { i ->
                        println("  ${i.id} | Hab: ${i.numeroHabitacion} | ${i.descripcion}")
                    }
                }
            }
            "6" -> {
                try {
                    val incidencias = service.listarIncidenciasNoResueltas()
                    if (incidencias.isEmpty()) {
                        println("No hay incidencias pendientes por resolver")
                    } else {
                        println("Incidencias pendientes:")
                        incidencias.forEach { i ->
                            println("  ${i.id} | Hab: ${i.numeroHabitacion} | ${i.descripcion}")
                        }
                        print("ID de la incidencia a resolver: ")
                        val id = readlnOrNull()?.trim() ?: ""
                        service.marcarComoResuelta(id)
                        println("Incidencia marcada como resuelta")
                    }
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }
            "7" -> {
                try {
                    val incidencias = service.listarIncidencias()
                    if (incidencias.isEmpty()) {
                        println("No hay incidencias registradas")
                    } else {
                        println("Incidencias actuales:")
                        incidencias.forEach { i ->
                            println("  ${i.id} | Hab: ${i.numeroHabitacion} | ${i.descripcion} | Resuelta: ${if (i.resuelta) "Si" else "No"}")
                        }
                        print("ID de la incidencia a eliminar: ")
                        val id = readlnOrNull()?.trim() ?: ""
                        if (service.eliminarIncidencia(id)) {
                            println("Incidencia eliminada correctamente")
                        }
                    }
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }
            "8" -> menu = false
            else -> println("Opcion no valida")
        }
    }
}

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

/**
 * Menú interactivo para la gestión de clientes.
 * Permite registrar, buscar, listar, actualizar, eliminar clientes y gestionar comentarios.
 * @param service Servicio de clientes
 * @param comentarioService Servicio de comentarios de clientes
 */
fun menuClientes(service: ClienteService, comentarioService: ComentarioClienteService) {
    var menu = true
    while (menu) {
        println("\n--- GESTION DE CLIENTES ---")
        println("1. Registrar nuevo cliente")
        println("2. Buscar cliente por NIF")
        println("3. Listar todos los clientes")
        println("4. Actualizar datos")
        println("5. Eliminar cliente")
        println("6. Anadir comentario")
        println("7. Ver comentarios")
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
            "8" -> menu = false
            else -> println("Opcion no valida")
        }
    }
}

/**
 * Menú interactivo para la importación y exportación de datos.
 * Permite importar reservas desde JSON y exportar reservas, incidencias y clientes.
 * @param clienteService Servicio de clientes
 * @param reservaService Servicio de reservas
 * @param incidenciaService Servicio de incidencias
 * @param ficheroRepo Repositorio de ficheros para importación/exportación
 */
fun menuImportarExportar(
    clienteService: ClienteService,
    reservaService: ReservaService,
    incidenciaService: IncidenciaService,
    ficheroRepo: FicheroRepository
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
                    val datos = ficheroRepo.importarDatosPrueba(ruta)
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
                    ficheroRepo.generarInformeReservas(reservas, "./reservas.txt")
                    println("Informe de reservas generado en ./reservas.txt")
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }
            "3" -> {
                try {
                    val incidencias = incidenciaService.listarIncidencias()
                    ficheroRepo.exportarIncidenciasATxt(incidencias, "./incidencias.txt")
                    println("Informe de incidencias generado en ./incidencias.txt")
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }
            "4" -> {
                try {
                    val clientes = clienteService.listarClientes()
                    ficheroRepo.exportarClientesAJson(clientes, "./clientes.json")
                    println("Clientes exportados a ./clientes.json")
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }
            "5" -> menu = false
            else -> println("Opcion no valida")
        }
    }
}

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
