package org.iesra.service

import org.iesra.exception.EntidadNoEncontradaException
import org.iesra.exception.ValidacionException
import org.iesra.model.ComentarioCliente
import org.iesra.repository.ComentarioClienteRepository

class ComentarioClienteService(
    private val repositorio: ComentarioClienteRepository = ComentarioClienteRepository()
) {
    fun registrarComentario(nombreCliente: String): ComentarioCliente {
        if (nombreCliente.isBlank()) {
            throw ValidacionException("El nombre del cliente no puede estar vacio")
        }
        val comentario = ComentarioCliente(nombreCliente = nombreCliente)
        return repositorio.guardar(comentario)
    }

    fun listarComentarios(): List<ComentarioCliente> {
        return repositorio.buscarTodos()
    }

    fun eliminarComentario(id: String): Boolean {
        val comentario = repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Comentario con id $id no encontrada")
        return repositorio.eliminar(comentario.id)
    }
}
