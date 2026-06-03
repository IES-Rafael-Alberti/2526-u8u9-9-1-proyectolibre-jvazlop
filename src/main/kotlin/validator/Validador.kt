package org.iesra.validator

import org.iesra.exception.ValidacionException

object Validador {

    private val REGEX_EMAIL = Regex("^[\\w.-]+@[\\w.-]+\\.\\w{2,}\$")
    private val REGEX_TELEFONO = Regex("^\\+?\\d{9,15}\$")
    private val REGEX_NIF = Regex("^\\d{8}[A-Z]\$")
    private val LETRAS_NIF = "TRWAGMYFPDXBNJZSQVHLCKE"

    fun validarEmail(email: String): Boolean {
        return email.matches(REGEX_EMAIL)
    }

    fun validarTelefono(telefono: String): Boolean {
        return telefono.matches(REGEX_TELEFONO)
    }

    fun validarNif(nif: String): Boolean {
        if (!nif.matches(REGEX_NIF)) return false
        val numero = nif.substring(0, 8).toInt()
        val letra = nif.last()
        return letra == LETRAS_NIF[numero % 23]
    }

    fun comprobarEmail(email: String) {
        if (!validarEmail(email)) {
            throw ValidacionException("El email '$email' no tiene un formato valido")
        }
    }

    fun comprobarTelefono(telefono: String) {
        if (!validarTelefono(telefono)) {
            throw ValidacionException("El telefono '$telefono' no tiene un formato valido (9-15 digitos)")
        }
    }

    fun comprobarNif(nif: String) {
        if (!validarNif(nif)) {
            throw ValidacionException("El NIF '$nif' no es valido (8 digitos + letra de control correcta)")
        }
    }

    fun comprobarTextoNoVacio(valor: String, campo: String) {
        if (valor.isBlank()) {
            throw ValidacionException("El campo '$campo' no puede estar vacio")
        }
    }
}
