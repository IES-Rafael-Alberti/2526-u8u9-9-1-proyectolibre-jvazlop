package org.iesra.app

import org.iesra.service.ClienteService
import org.iesra.service.ComentarioClienteService
import org.iesra.service.IncidenciaService
import org.iesra.service.ReservaService
import org.iesra.repository.ExportadorFicheros
import org.iesra.repository.ImportadorFicheros
import org.iesra.util.ConexionH2

/**
 * Punto de entrada principal de la aplicación.
 * Inicializa los servicios, la base de datos H2 y ejecuta el bucle del menú principal.
 */
fun main() {
    val clienteService = ClienteService()
    val reservaService = ReservaService()
    val comentarioService = ComentarioClienteService()
    val incidenciaService = IncidenciaService()
    val exportadorFicheros = ExportadorFicheros()
    val importadorFicheros = ImportadorFicheros()

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
            "5" -> menuImportarExportar(clienteService, reservaService, incidenciaService, exportadorFicheros, importadorFicheros)
            "6" -> checkout(clienteService, reservaService, incidenciaService)
            "7" -> menu = false
            else -> println("Opcion no valida")
        }
    }
}
