package org.iesra.model

import java.time.LocalDate

/**
 * Representa una reserva de habitacion realizada por un cliente.
 * @property id Identificador unico de la reserva
 * @property idCliente NIF del cliente que realiza la reserva
 * @property numeroHabitacion Numero de la habitacion reservada
 * @property fechaEntrada Fecha de entrada del huesped
 * @property fechaSalida Fecha de salida del huesped
 * @property estado Estado actual de la reserva (pendiente, confirmada, cancelada, finalizada)
 * @property pagada Indica si la reserva ha sido pagada
 * @property numPersonas Numero de personas que ocupan la habitacion
 * @property segundoHuesped Nombre del segundo huesped si lo hubiera
 */
data class Reserva(
    val id: Int = 0,
    val idCliente: String,
    val numeroHabitacion: Int,
    val fechaEntrada: LocalDate,
    val fechaSalida: LocalDate,
    var estado: String = ESTADO_PENDIENTE,
    var pagada: Boolean = false,
    val numPersonas: Int = 1,
    val segundoHuesped: String = ""
) {
    companion object {
        /** Estado que indica que la reserva esta pendiente de confirmacion. */
        const val ESTADO_PENDIENTE = "pendiente"
        /** Estado que indica que la reserva ha sido confirmada. */
        const val ESTADO_CONFIRMADA = "confirmada"
        /** Estado que indica que la reserva ha sido cancelada. */
        const val ESTADO_CANCELADA = "cancelada"
        /** Estado que indica que la reserva ha finalizado. */
        const val ESTADO_FINALIZADA = "finalizada"
    }
}
