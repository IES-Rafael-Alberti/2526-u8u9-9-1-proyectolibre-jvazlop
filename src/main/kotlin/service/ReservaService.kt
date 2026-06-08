package org.iesra.service

import org.iesra.exception.EntidadNoEncontradaException
import org.iesra.exception.ValidacionException
import org.iesra.model.Reserva
import org.iesra.repository.Repositorio
import org.iesra.repository.ReservaDao
import java.time.LocalDate

/**
 * Servicio para la gestión de reservas.
 * Contiene la lógica de negocio para crear, modificar, cancelar y consultar reservas.
 * @property repositorio Repositorio de reservas (H2)
 */
class ReservaService(
    private val repositorio: Repositorio<Reserva, Int> = ReservaDao()
) {

    /**
     * Crea una nueva reserva tras validar las fechas y la disponibilidad de la habitación.
     * @param idCliente NIF del cliente
     * @param numeroHabitacion Número de habitación
     * @param fechaEntrada Fecha de entrada
     * @param fechaSalida Fecha de salida
     * @param pagadaAhora Indica si se paga en el momento de la reserva
     * @param numPersonas Número de personas
     * @param segundoHuesped Nombre del segundo huésped (opcional)
     * @return La reserva creada
     * @throws ValidacionException si las fechas no son válidas o la habitación está ocupada
     */
    fun crearReserva(idCliente: String, numeroHabitacion: Int, fechaEntrada: LocalDate, fechaSalida: LocalDate, pagadaAhora: Boolean = false, numPersonas: Int = 1, segundoHuesped: String = ""): Reserva {
        if (fechaEntrada.isBefore(LocalDate.now())) {
            throw ValidacionException("La fecha de entrada no puede ser anterior a hoy")
        }
        if (fechaSalida.isBefore(fechaEntrada) || fechaSalida.isEqual(fechaEntrada)) {
            throw ValidacionException("La fecha de salida debe ser posterior a la de entrada")
        }

        val ocupadas = repositorio.buscarTodos().any {
            it.numeroHabitacion == numeroHabitacion
            && it.estado != Reserva.ESTADO_CANCELADA
            && it.estado != Reserva.ESTADO_FINALIZADA
            && it.fechaEntrada < fechaSalida
            && it.fechaSalida > fechaEntrada
        }
        if (ocupadas) {
            throw ValidacionException("La habitacion $numeroHabitacion ya esta ocupada en esas fechas")
        }

        val reserva = Reserva(
            idCliente = idCliente,
            numeroHabitacion = numeroHabitacion,
            fechaEntrada = fechaEntrada,
            fechaSalida = fechaSalida,
            pagada = pagadaAhora,
            numPersonas = numPersonas,
            segundoHuesped = segundoHuesped
        )
        return repositorio.guardar(reserva)
    }

    /**
     * Busca una reserva por su ID.
     * @param id ID de la reserva
     * @return La reserva encontrada
     * @throws EntidadNoEncontradaException si no existe la reserva
     */
    fun buscarReserva(id: Int): Reserva {
        return repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Reserva con id $id no encontrada")
    }

    /**
     * Obtiene la lista completa de reservas registradas.
     * @return Lista de todas las reservas
     */
    fun listarReservas(): List<Reserva> {
        return repositorio.buscarTodos()
    }

    /**
     * Obtiene las reservas de un cliente específico.
     * @param idCliente NIF del cliente
     * @return Lista de reservas del cliente
     */
    fun listarReservasPorCliente(idCliente: String): List<Reserva> {
        return repositorio.buscarTodos().filter { it.idCliente == idCliente }
    }

    /**
     * Obtiene las reservas pendientes para el día de hoy.
     * @return Lista de reservas con entrada hoy y no canceladas
     */
    fun listarPendientesHoy(): List<Reserva> {
        return repositorio.buscarTodos().filter {
            it.fechaEntrada == LocalDate.now() && it.estado != Reserva.ESTADO_CANCELADA
        }
    }

    /**
     * Modifica las fechas de entrada y salida de una reserva existente.
     * @param id ID de la reserva
     * @param nuevaEntrada Nueva fecha de entrada
     * @param nuevaSalida Nueva fecha de salida
     * @return La reserva actualizada
     * @throws ValidacionException si las fechas no son válidas o la habitación está ocupada
     * @throws EntidadNoEncontradaException si no existe la reserva
     */
    fun modificarFechas(id: Int, nuevaEntrada: LocalDate, nuevaSalida: LocalDate): Reserva {
        val reserva = repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Reserva con id $id no encontrada")

        if (nuevaEntrada.isBefore(LocalDate.now())) {
            throw ValidacionException("La nueva fecha de entrada no puede ser anterior a hoy")
        }
        if (nuevaSalida.isBefore(nuevaEntrada) || nuevaSalida.isEqual(nuevaEntrada)) {
            throw ValidacionException("La nueva fecha de salida debe ser posterior a la de entrada")
        }

        val ocupadas = repositorio.buscarTodos().any {
            it.id != id
            && it.numeroHabitacion == reserva.numeroHabitacion
            && it.estado != Reserva.ESTADO_CANCELADA
            && it.estado != Reserva.ESTADO_FINALIZADA
            && it.fechaEntrada < nuevaSalida
            && it.fechaSalida > nuevaEntrada
        }
        if (ocupadas) {
            throw ValidacionException("La habitacion ${reserva.numeroHabitacion} ya esta ocupada en esas fechas")
        }

        val actualizada = reserva.copy(fechaEntrada = nuevaEntrada, fechaSalida = nuevaSalida)
        return repositorio.actualizar(actualizada)
    }

    /**
     * Marca una reserva como pagada.
     * @param id ID de la reserva
     * @return La reserva actualizada
     * @throws ValidacionException si la reserva ya está pagada
     * @throws EntidadNoEncontradaException si no existe la reserva
     */
    fun marcarComoPagada(id: Int): Reserva {
        val reserva = repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Reserva con id $id no encontrada")
        if (reserva.pagada) {
            throw ValidacionException("La reserva ya esta pagada")
        }
        val actualizada = reserva.copy(pagada = true)
        return repositorio.actualizar(actualizada)
    }

    /**
     * Confirma una reserva cambiando su estado a confirmada.
     * @param id ID de la reserva
     * @return La reserva actualizada
     * @throws EntidadNoEncontradaException si no existe la reserva
     */
    fun confirmarReserva(id: Int): Reserva {
        val reserva = repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Reserva con id $id no encontrada")
        val actualizada = reserva.copy(estado = Reserva.ESTADO_CONFIRMADA)
        return repositorio.actualizar(actualizada)
    }

    /**
     * Cancela una reserva cambiando su estado a cancelada.
     * @param id ID de la reserva
     * @return La reserva actualizada
     * @throws EntidadNoEncontradaException si no existe la reserva
     */
    fun cancelarReserva(id: Int): Reserva {
        val reserva = repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Reserva con id $id no encontrada")
        val actualizada = reserva.copy(estado = Reserva.ESTADO_CANCELADA)
        return repositorio.actualizar(actualizada)
    }

    /**
     * Finaliza una reserva cambiando su estado a finalizada.
     * @param id ID de la reserva
     * @return La reserva actualizada
     * @throws ValidacionException si la reserva ya está cancelada
     * @throws EntidadNoEncontradaException si no existe la reserva
     */
    fun finalizarReserva(id: Int): Reserva {
        val reserva = repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Reserva con id $id no encontrada")
        if (reserva.estado == Reserva.ESTADO_CANCELADA) {
            throw ValidacionException("No se puede finalizar una reserva cancelada")
        }
        val actualizada = reserva.copy(estado = Reserva.ESTADO_FINALIZADA)
        return repositorio.actualizar(actualizada)
    }

    /**
     * Elimina una reserva del sistema.
     * @param id ID de la reserva
     * @return true si se eliminó correctamente
     * @throws EntidadNoEncontradaException si no existe la reserva
     */
    fun eliminarReserva(id: Int): Boolean {
        val reserva = repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Reserva con id $id no encontrada")
        return repositorio.eliminar(reserva.id)
    }


}
