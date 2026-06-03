package org.iesra.model

import java.time.LocalDateTime

data class ComentarioCliente(
    val id: String = "",
    val nombreCliente: String,
    val fecha: LocalDateTime = LocalDateTime.now()
)
