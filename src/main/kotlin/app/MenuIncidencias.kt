package org.iesra.app

import org.iesra.service.IncidenciaService

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
