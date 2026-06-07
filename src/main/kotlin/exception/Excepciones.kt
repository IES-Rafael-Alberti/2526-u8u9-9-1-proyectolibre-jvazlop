package org.iesra.exception

/**
 * Excepcion lanzada cuando una entidad solicitada no se encuentra en el sistema.
 * @param mensaje Descripcion del error
 */
class EntidadNoEncontradaException(mensaje: String) : Exception(mensaje)

/**
 * Excepcion lanzada cuando un dato no supera las validaciones de formato o contenido.
 * @param mensaje Descripcion del error de validacion
 */
class ValidacionException(mensaje: String) : Exception(mensaje)

/**
 * Excepcion lanzada cuando se produce un error al conectar con la base de datos.
 * @param mensaje Descripcion del error
 * @param causa Excepcion original que origino el error (opcional)
 */
class ConexionBaseDatosException(mensaje: String, causa: Throwable? = null) : Exception(mensaje, causa)

/**
 * Excepcion lanzada cuando se produce un error al leer o escribir un fichero.
 * @param mensaje Descripcion del error
 * @param causa Excepcion original que origino el error (opcional)
 */
class FicheroException(mensaje: String, causa: Throwable? = null) : Exception(mensaje, causa)

/**
 * Excepcion lanzada cuando se produce un error en la interaccion con MongoDB.
 * @param mensaje Descripcion del error
 * @param causa Excepcion original que origino el error (opcional)
 */
class MongoDBException(mensaje: String, causa: Throwable? = null) : Exception(mensaje, causa)
