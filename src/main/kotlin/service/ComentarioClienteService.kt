package org.iesra.service

import org.iesra.exception.EntidadNoEncontradaException
import org.iesra.exception.ValidacionException
import org.iesra.model.ComentarioCliente
import org.iesra.repository.ComentarioClienteRepository

/**
 * Servicio para la gestión de comentarios de clientes.
 * Permite registrar, listar y eliminar comentarios realizados por los clientes.
 * @property repositorio Repositorio de comentarios de clientes (MongoDB)
 */
class ComentarioClienteService(
    private val repositorio: ComentarioClienteRepository = ComentarioClienteRepository()
) {
    /**
     * Registra un nuevo comentario de cliente.
     * @param nombreCliente Nombre del cliente que realiza el comentario
     * @return El comentario creado
     * @throws ValidacionException si el nombre está vacío
     */
    fun registrarComentario(nombreCliente: String): ComentarioCliente {
        if (nombreCliente.isBlank()) {
            throw ValidacionException("El nombre del cliente no puede estar vacio")
        }
        val comentario = ComentarioCliente(nombreCliente = nombreCliente)
        return repositorio.guardar(comentario)
    }

    /**
     * Obtiene la lista completa de comentarios registrados.
     * @return Lista de todos los comentarios
     */
    fun listarComentarios(): List<ComentarioCliente> {
        return repositorio.buscarTodos()
    }

    /**
     * Elimina un comentario por su ID.
     * @param id ID del comentario
     * @return true si se eliminó correctamente
     * @throws EntidadNoEncontradaException si no existe el comentario
     */
    fun eliminarComentario(id: String): Boolean {
        val comentario = repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Comentario con id $id no encontrada")
        return repositorio.eliminar(comentario.id)
    }
}
