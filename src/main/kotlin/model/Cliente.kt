package org.iesra.model

/**
 * Representa un cliente del hotel.
 * @property id NIF del cliente (8 digitos + letra de control)
 * @property nombre Nombre completo del cliente
 * @property email Direccion de correo electronico
 * @property telefono Numero de telefono
 */
data class Cliente(
    val id: String,
    var nombre: String,
    var email: String,
    var telefono: String
)
