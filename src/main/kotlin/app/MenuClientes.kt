package org.iesra.app

import org.iesra.exception.EntidadNoEncontradaException
import org.iesra.service.ClienteService
import org.iesra.service.ComentarioClienteService

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
