package org.iesra.model

import java.time.LocalDateTime

/**
 * Representa un comentario u opinion dejado por un cliente del hotel.
 * @property id Identificador unico del comentario
 * @property nombreCliente Nombre del cliente que realizo el comentario
 * @property fecha Fecha y hora en que se publico el comentario
 */
data class ComentarioCliente(
    val id: String = "",
    val nombreCliente: String,
    val fecha: LocalDateTime = LocalDateTime.now()
)
