package org.iesra.service

import org.iesra.exception.EntidadNoEncontradaException
import org.iesra.model.Cliente
import org.iesra.repository.ClienteDao
import org.iesra.repository.Repositorio
import org.iesra.validator.Validador

/**
 * Servicio para la gestión de clientes.
 * Contiene la lógica de negocio y validaciones antes de delegar en el repositorio.
 * @property repositorio Repositorio de clientes (H2)
 */
class ClienteService(private val repositorio: Repositorio<Cliente, String> = ClienteDao()) {

    /**
     * Registra un nuevo cliente tras validar sus datos.
     * @param id NIF del cliente (validado con módulo 23)
     * @param nombre Nombre completo
     * @param email Email (validado con regex)
     * @param telefono Teléfono (validado con regex)
     * @return El cliente creado
     * @throws ValidacionException si algún dato no es válido
     */
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

    /**
     * Busca un cliente por su NIF.
     * @param id NIF del cliente
     * @return El cliente encontrado
     * @throws EntidadNoEncontradaException si no existe el cliente
     */
    fun buscarCliente(id: String): Cliente {
        return repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Cliente con NIF $id no encontrado")
    }

    /**
     * Obtiene la lista completa de clientes registrados.
     * @return Lista de todos los clientes
     */
    fun listarClientes(): List<Cliente> {
        return repositorio.buscarTodos()
    }

    /**
     * Actualiza los datos de un cliente existente.
     * @param id NIF del cliente
     * @param nombre Nuevo nombre
     * @param email Nuevo email
     * @param telefono Nuevo teléfono
     * @return El cliente actualizado
     * @throws EntidadNoEncontradaException si no existe el cliente
     */
    fun actualizarCliente(id: String, nombre: String, email: String, telefono: String): Cliente {
        Validador.comprobarTextoNoVacio(nombre, "nombre")
        Validador.comprobarEmail(email)
        Validador.comprobarTelefono(telefono)

        val clienteExistente = repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Cliente con NIF $id no encontrado")

        val clienteActualizado = clienteExistente.copy(nombre = nombre, email = email, telefono = telefono)
        return repositorio.actualizar(clienteActualizado)
    }

    /**
     * Elimina un cliente por su NIF.
     * @param id NIF del cliente
     * @return true si se eliminó correctamente
     * @throws EntidadNoEncontradaException si no existe el cliente
     */
    fun eliminarCliente(id: String): Boolean {
        val cliente = repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Cliente con NIF $id no encontrado")
        return repositorio.eliminar(cliente.id)
    }

    /**
     * Comprueba si existe un cliente con el NIF indicado.
     * @param id NIF del cliente
     * @return true si el cliente existe, false en caso contrario
     */
    fun existeCliente(id: String): Boolean {
        return repositorio.buscarPorId(id) != null
    }

    /**
     * Obtiene un cliente existente o lo crea si no existe.
     * @param id NIF del cliente
     * @param nombre Nombre completo
     * @param email Email
     * @param telefono Teléfono
     * @return El cliente existente o el recién creado
     */
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
