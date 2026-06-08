package org.iesra.model

/**
 * Representa una habitacion del hotel.
 * @property numero Numero unico de la habitacion
 * @property tipo Tipo de habitacion (individual, doble, suite, etc.)
 * @property precioNoche Precio por noche en euros
 * @property disponible Indica si la habitacion esta disponible para reservar
 */
data class Habitacion(
    val numero: Int,
    var tipo: String,
    var precioNoche: Double,
    var disponible: Boolean = true
)
