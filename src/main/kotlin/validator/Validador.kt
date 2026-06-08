package org.iesra.validator

import org.iesra.exception.ValidacionException

/**
 * Objeto utilitario con metodos de validacion de formatos comunes (email, telefono, NIF).
 */
object Validador {

    private val REGEX_EMAIL = Regex("^[\\w.-]+@[\\w.-]+\\.\\w{2,}\$")
    private val REGEX_TELEFONO = Regex("^\\+?\\d{9,15}\$")
    private val REGEX_NIF = Regex("^\\d{8}[A-Z]\$")
    private val LETRAS_NIF = "TRWAGMYFPDXBNJZSQVHLCKE"

    /**
     * Valida el formato de una direccion de correo electronico.
     * @param email Direccion de email a validar
     * @return true si el email tiene un formato valido, false en caso contrario
     */
    fun validarEmail(email: String): Boolean {
        return email.matches(REGEX_EMAIL)
    }

    /**
     * Valida el formato de un numero de telefono (9-15 digitos, opcionalmente con prefijo +).
     * @param telefono Numero de telefono a validar
     * @return true si el telefono tiene un formato valido, false en caso contrario
     */
    fun validarTelefono(telefono: String): Boolean {
        return telefono.matches(REGEX_TELEFONO)
    }

    /**
     * Valida el formato de un NIF espanol (8 digitos + letra de control) y verifica la letra mediante modulo 23.
     * @param nif NIF a validar
     * @return true si el NIF es valido, false en caso contrario
     */
    fun validarNif(nif: String): Boolean {
        if (!nif.matches(REGEX_NIF)) return false
        val numero = nif.substring(0, 8).toInt()
        val letra = nif.last()
        return letra == LETRAS_NIF[numero % 23]
    }

    /**
     * Comprueba que el email tenga un formato valido y lanza una excepcion si no es asi.
     * @param email Direccion de email a comprobar
     * @throws ValidacionException si el email no tiene un formato valido
     */
    fun comprobarEmail(email: String) {
        if (!validarEmail(email)) {
            throw ValidacionException("El email '$email' no tiene un formato valido")
        }
    }

    /**
     * Comprueba que el telefono tenga un formato valido y lanza una excepcion si no es asi.
     * @param telefono Numero de telefono a comprobar
     * @throws ValidacionException si el telefono no tiene un formato valido
     */
    fun comprobarTelefono(telefono: String) {
        if (!validarTelefono(telefono)) {
            throw ValidacionException("El telefono '$telefono' no tiene un formato valido (9-15 digitos)")
        }
    }

    /**
     * Comprueba que el NIF tenga un formato valido y lanza una excepcion si no es asi.
     * @param nif NIF a comprobar
     * @throws ValidacionException si el NIF no es valido
     */
    fun comprobarNif(nif: String) {
        if (!validarNif(nif)) {
            throw ValidacionException("El NIF '$nif' no es valido (8 digitos + letra de control correcta)")
        }
    }

    /**
     * Comprueba que un texto no este vacio o en blanco y lanza una excepcion si lo esta.
     * @param valor Texto a comprobar
     * @param campo Nombre del campo para el mensaje de error
     * @throws ValidacionException si el texto esta vacio o en blanco
     */
    fun comprobarTextoNoVacio(valor: String, campo: String) {
        if (valor.isBlank()) {
            throw ValidacionException("El campo '$campo' no puede estar vacio")
        }
    }
}
