package org.iesra.util

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.iesra.exception.MongoDBException

/**
 * Objeto singleton para la gestión de la conexión con MongoDB Atlas.
 * Proporciona métodos para obtener colecciones y cerrar la conexión.
 */
object ConexionMongo {

    private const val CADENA_CONEXION = "mongodb+srv://jordi:gILIPOLLAS26@gestorhotel.ukdr4dk.mongodb.net/?appName=gestorhotel"
    private const val NOMBRE_BD = "hotel_maligno"
    private const val NOMBRE_COLECCION = "incidencias"

    private var cliente: MongoClient? = null
    private var baseDatos: MongoDatabase? = null

    /**
     * Obtiene una colección de MongoDB por su nombre.
     * Si el cliente no existe, crea una nueva conexión.
     * @param nombreColeccion Nombre de la colección (por defecto "incidencias")
     * @return La colección de MongoDB
     * @throws MongoDBException si no se puede conectar con MongoDB Atlas
     */
    fun obtenerColeccion(nombreColeccion: String = NOMBRE_COLECCION): MongoCollection<Document> {
        try {
            if (cliente == null) {
                cliente = MongoClients.create(CADENA_CONEXION)
                baseDatos = cliente?.getDatabase(NOMBRE_BD)
            }
            return baseDatos?.getCollection(nombreColeccion)
                ?: throw MongoDBException("La base de datos MongoDB no se pudo obtener")
        } catch (e: Exception) {
            throw MongoDBException("Error al conectar con MongoDB Atlas", e)
        }
    }

    /**
     * Cierra la conexión con MongoDB Atlas.
     * @throws MongoDBException si ocurre un error al cerrar la conexión
     */
    fun cerrarConexion() {
        try {
            cliente?.close()
            cliente = null
            baseDatos = null
        } catch (e: Exception) {
            throw MongoDBException("Error al cerrar conexion MongoDB", e)
        }
    }
}
