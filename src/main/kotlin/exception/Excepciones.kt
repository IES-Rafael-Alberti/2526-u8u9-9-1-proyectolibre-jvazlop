package org.iesra.exception

class EntidadNoEncontradaException(mensaje: String) : Exception(mensaje)

class ValidacionException(mensaje: String) : Exception(mensaje)

class ConexionBaseDatosException(mensaje: String, causa: Throwable? = null) : Exception(mensaje, causa)

class FicheroException(mensaje: String, causa: Throwable? = null) : Exception(mensaje, causa)

class MongoDBException(mensaje: String, causa: Throwable? = null) : Exception(mensaje, causa)
