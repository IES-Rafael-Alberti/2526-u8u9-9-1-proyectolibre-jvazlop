package org.iesra.service

import org.iesra.exception.EntidadNoEncontradaException
import org.iesra.exception.ValidacionException
import org.iesra.model.Reserva
import org.iesra.repository.Repositorio
import org.iesra.repository.ReservaDao
import java.time.LocalDate

class ReservaService(
    private val repositorio: Repositorio<Reserva, Int> = ReservaDao()
) {

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

    fun buscarReserva(id: Int): Reserva {
        return repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Reserva con id $id no encontrada")
    }

    fun listarReservas(): List<Reserva> {
        return repositorio.buscarTodos()
    }

    fun listarReservasPorCliente(idCliente: String): List<Reserva> {
        val dao = repositorio as? ReservaDao
            ?: return repositorio.buscarTodos().filter { it.idCliente == idCliente }
        return dao.buscarPorCliente(idCliente)
    }

    fun listarPendientesHoy(): List<Reserva> {
        return repositorio.buscarTodos().filter {
            it.fechaEntrada == LocalDate.now() && it.estado != Reserva.ESTADO_CANCELADA
        }
    }

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

    fun marcarComoPagada(id: Int): Reserva {
        val reserva = repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Reserva con id $id no encontrada")
        if (reserva.pagada) {
            throw ValidacionException("La reserva ya esta pagada")
        }
        val actualizada = reserva.copy(pagada = true)
        return repositorio.actualizar(actualizada)
    }

    fun confirmarReserva(id: Int): Reserva {
        val reserva = repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Reserva con id $id no encontrada")
        val actualizada = reserva.copy(estado = Reserva.ESTADO_CONFIRMADA)
        return repositorio.actualizar(actualizada)
    }

    fun cancelarReserva(id: Int): Reserva {
        val reserva = repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Reserva con id $id no encontrada")
        val actualizada = reserva.copy(estado = Reserva.ESTADO_CANCELADA)
        return repositorio.actualizar(actualizada)
    }

    fun finalizarReserva(id: Int): Reserva {
        val reserva = repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Reserva con id $id no encontrada")
        if (reserva.estado == Reserva.ESTADO_CANCELADA) {
            throw ValidacionException("No se puede finalizar una reserva cancelada")
        }
        val actualizada = reserva.copy(estado = Reserva.ESTADO_FINALIZADA)
        return repositorio.actualizar(actualizada)
    }

    fun eliminarReserva(id: Int): Boolean {
        val reserva = repositorio.buscarPorId(id)
            ?: throw EntidadNoEncontradaException("Reserva con id $id no encontrada")
        return repositorio.eliminar(reserva.id)
    }


}
