package org.iesra.model

import java.time.LocalDateTime

/**
 * Representa una incidencia registrada en una habitacion del hotel.
 * @property id Identificador unico de la incidencia
 * @property numeroHabitacion Numero de la habitacion donde ocurrio la incidencia
 * @property descripcion Descripcion detallada del problema o incidencia
 * @property fecha Fecha y hora en que se registro la incidencia
 * @property resuelta Indica si la incidencia ha sido resuelta
 */
data class Incidencia(
    val id: String = "",
    val numeroHabitacion: Int,
    val descripcion: String,
    val fecha: LocalDateTime = LocalDateTime.now(),
    var resuelta: Boolean = false
)
