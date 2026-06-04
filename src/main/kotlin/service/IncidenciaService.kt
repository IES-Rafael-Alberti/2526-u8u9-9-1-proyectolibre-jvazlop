package org.iesra.service

import org.iesra.exception.EntidadNoEncontradaException
import org.iesra.exception.ValidacionException
import org.iesra.model.Incidencia
import org.iesra.repository.IncidenciaRepository
import org.iesra.repository.Repositorio

class IncidenciaService(
    private val repositorio: Repositorio<Incidencia, String> = IncidenciaRepository()
) {
    fun reportarIncidencia(numeroHabitacion: Int, descripcion: String): Incidencia {
        if (descripcion.isBlank()) {
            throw ValidacionException("La descripcion de la incidencia no puede estar vacia")
        }
        val incidencia = Incidencia(numeroHabitacion = numeroHabitacion, descripcion = descripcion)
        return repositorio.guardar(incidencia)
    }

    fun buscarIncidencia(id: String): Incidencia {
        return repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Incidencia con id $id no encontrada")
    }

    fun listarIncidencias(): List<Incidencia> {
        return repositorio.buscarTodos()
    }

    fun listarIncidenciasPorHabitacion(numeroHabitacion: Int): List<Incidencia> {
        val repo = repositorio as? IncidenciaRepository
            ?: return repositorio.buscarTodos().filter { it.numeroHabitacion == numeroHabitacion }
        return repo.buscarPorHabitacion(numeroHabitacion)
    }

    fun listarIncidenciasNoResueltas(): List<Incidencia> {
        val repo = repositorio as? IncidenciaRepository
            ?: return repositorio.buscarTodos().filter { !it.resuelta }
        return repo.buscarNoResueltas()
    }

    fun marcarComoResuelta(id: String): Incidencia {
        val incidencia = repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Incidencia con id $id no encontrada")
        val actualizada = incidencia.copy(resuelta = true)
        return repositorio.actualizar(actualizada)
    }

    fun eliminarIncidencia(id: String): Boolean {
        val incidencia = repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Incidencia con id $id no encontrada")
        return repositorio.eliminar(incidencia.id)
    }
}
