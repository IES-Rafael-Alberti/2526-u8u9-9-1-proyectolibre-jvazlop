package org.iesra.model

import java.time.LocalDateTime

data class Incidencia(
    val id: String = "",
    val numeroHabitacion: Int,
    val descripcion: String,
    val fecha: LocalDateTime = LocalDateTime.now(),
    var resuelta: Boolean = false
)
