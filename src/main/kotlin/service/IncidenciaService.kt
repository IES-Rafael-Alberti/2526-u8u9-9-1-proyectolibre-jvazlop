package org.iesra.service

import org.iesra.exception.EntidadNoEncontradaException
import org.iesra.exception.ValidacionException
import org.iesra.model.Incidencia
import org.iesra.repository.IncidenciaRepository
import org.iesra.repository.Repositorio

/**
 * Servicio para la gestión de incidencias.
 * Permite reportar, buscar, listar, resolver y eliminar incidencias de habitaciones.
 * @property repositorio Repositorio de incidencias (MongoDB)
 */
class IncidenciaService(
    private val repositorio: Repositorio<Incidencia, String> = IncidenciaRepository()
) {
    /**
     * Reporta una nueva incidencia para una habitación.
     * @param numeroHabitacion Número de habitación
     * @param descripcion Descripción de la incidencia
     * @return La incidencia creada
     * @throws ValidacionException si la descripción está vacía
     */
    fun reportarIncidencia(numeroHabitacion: Int, descripcion: String): Incidencia {
        if (descripcion.isBlank()) {
            throw ValidacionException("La descripcion de la incidencia no puede estar vacia")
        }
        val incidencia = Incidencia(numeroHabitacion = numeroHabitacion, descripcion = descripcion)
        return repositorio.guardar(incidencia)
    }

    /**
     * Busca una incidencia por su ID.
     * @param id ID de la incidencia
     * @return La incidencia encontrada
     * @throws EntidadNoEncontradaException si no existe la incidencia
     */
    fun buscarIncidencia(id: String): Incidencia {
        return repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Incidencia con id $id no encontrada")
    }

    /**
     * Obtiene la lista completa de incidencias registradas.
     * @return Lista de todas las incidencias
     */
    fun listarIncidencias(): List<Incidencia> {
        return repositorio.buscarTodos()
    }

    /**
     * Obtiene las incidencias de una habitación específica.
     * @param numeroHabitacion Número de habitación
     * @return Lista de incidencias de la habitación
     */
    fun listarIncidenciasPorHabitacion(numeroHabitacion: Int): List<Incidencia> {
        val repo = repositorio as? IncidenciaRepository
            ?: return repositorio.buscarTodos().filter { it.numeroHabitacion == numeroHabitacion }
        return repo.buscarPorHabitacion(numeroHabitacion)
    }

    /**
     * Obtiene las incidencias que aún no han sido resueltas.
     * @return Lista de incidencias pendientes
     */
    fun listarIncidenciasNoResueltas(): List<Incidencia> {
        val repo = repositorio as? IncidenciaRepository
            ?: return repositorio.buscarTodos().filter { !it.resuelta }
        return repo.buscarNoResueltas()
    }

    /**
     * Marca una incidencia como resuelta.
     * @param id ID de la incidencia
     * @return La incidencia actualizada
     * @throws EntidadNoEncontradaException si no existe la incidencia
     */
    fun marcarComoResuelta(id: String): Incidencia {
        val incidencia = repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Incidencia con id $id no encontrada")
        val actualizada = incidencia.copy(resuelta = true)
        return repositorio.actualizar(actualizada)
    }

    /**
     * Elimina una incidencia del sistema.
     * @param id ID de la incidencia
     * @return true si se eliminó correctamente
     * @throws EntidadNoEncontradaException si no existe la incidencia
     */
    fun eliminarIncidencia(id: String): Boolean {
        val incidencia = repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Incidencia con id $id no encontrada")
        return repositorio.eliminar(incidencia.id)
    }
}
