package org.iesra.model

import java.time.LocalDate

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
        const val ESTADO_PENDIENTE = "pendiente"
        const val ESTADO_CONFIRMADA = "confirmada"
        const val ESTADO_CANCELADA = "cancelada"
        const val ESTADO_FINALIZADA = "finalizada"
    }
}
