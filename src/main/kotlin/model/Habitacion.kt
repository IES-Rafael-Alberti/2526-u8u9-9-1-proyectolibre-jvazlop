package org.iesra.model

data class Habitacion(
    val numero: Int,
    var tipo: String,
    var precioNoche: Double,
    var disponible: Boolean = true
)
