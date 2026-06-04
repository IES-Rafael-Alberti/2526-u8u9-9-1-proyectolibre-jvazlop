package org.iesra.service

import org.iesra.exception.EntidadNoEncontradaException
import org.iesra.model.Cliente
import org.iesra.repository.ClienteDao
import org.iesra.repository.Repositorio
import org.iesra.validator.Validador

class ClienteService(private val repositorio: Repositorio<Cliente, String> = ClienteDao()) {

    fun registrarCliente(id: String, nombre: String, email: String, telefono: String): Cliente {
        Validador.comprobarNif(id)
        Validador.comprobarTextoNoVacio(nombre, "nombre")
        Validador.comprobarEmail(email)
        Validador.comprobarTelefono(telefono)

        val existente = repositorio.buscarPorId(id)
        if (existente != null) {
            throw IllegalArgumentException("Ya existe un cliente con el NIF $id")
        }

        val cliente = Cliente(id, nombre, email, telefono)
        return repositorio.guardar(cliente)
    }

    fun buscarCliente(id: String): Cliente {
        return repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Cliente con NIF $id no encontrado")
    }

    fun listarClientes(): List<Cliente> {
        return repositorio.buscarTodos()
    }

    fun actualizarCliente(id: String, nombre: String, email: String, telefono: String): Cliente {
        Validador.comprobarTextoNoVacio(nombre, "nombre")
        Validador.comprobarEmail(email)
        Validador.comprobarTelefono(telefono)

        val clienteExistente = repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Cliente con NIF $id no encontrado")

        val clienteActualizado = clienteExistente.copy(nombre = nombre, email = email, telefono = telefono)
        return repositorio.actualizar(clienteActualizado)
    }

    fun eliminarCliente(id: String): Boolean {
        val cliente = repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Cliente con NIF $id no encontrado")
        return repositorio.eliminar(cliente.id)
    }

    fun existeCliente(id: String): Boolean {
        return repositorio.buscarPorId(id) != null
    }

    fun obtenerOCrearCliente(id: String, nombre: String, email: String, telefono: String): Cliente {
        val existente = repositorio.buscarPorId(id)
        if (existente != null) {
            return existente
        }
        Validador.comprobarNif(id)
        Validador.comprobarTextoNoVacio(nombre, "nombre")
        Validador.comprobarEmail(email)
        Validador.comprobarTelefono(telefono)
        val cliente = Cliente(id, nombre, email, telefono)
        return repositorio.guardar(cliente)
    }
}
