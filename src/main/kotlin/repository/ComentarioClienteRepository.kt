package org.iesra.repository

import com.mongodb.client.model.Filters
import org.bson.Document
import org.bson.types.ObjectId
import org.iesra.exception.MongoDBException
import org.iesra.model.ComentarioCliente
import org.iesra.util.ConexionMongo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val COLECCION_COMENTARIOS = "comentarios_clientes"

/**
 * Repositorio para gestionar comentarios de clientes usando MongoDB.
 * Proporciona operaciones de guardado, búsqueda y eliminación de comentarios.
 */
class ComentarioClienteRepository {

    private val formatoFecha = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    /**
     * Guarda un nuevo comentario de cliente en MongoDB.
     *
     * @param entidad El comentario a guardar.
     * @return El comentario guardado con el id asignado por MongoDB.
     * @throws MongoDBException si ocurre un error al guardar.
     */
    fun guardar(entidad: ComentarioCliente): ComentarioCliente {
        try {
            val coleccion = ConexionMongo.obtenerColeccion(COLECCION_COMENTARIOS)
            val doc = Document()
                .append("nombreCliente", entidad.nombreCliente)
                .append("fecha", entidad.fecha.format(formatoFecha))
            coleccion.insertOne(doc)
            return entidad.copy(id = doc.getObjectId("_id").toHexString())
        } catch (e: Exception) {
            throw MongoDBException("Error al guardar el comentario en MongoDB", e)
        }
    }

    /**
     * Busca un comentario por su identificador.
     *
     * @param id El identificador del comentario.
     * @return El comentario encontrado, o null si no existe.
     * @throws MongoDBException si ocurre un error al buscar.
     */
    fun buscarPorId(id: String): ComentarioCliente? {
        try {
            val coleccion = ConexionMongo.obtenerColeccion(COLECCION_COMENTARIOS)
            val doc = coleccion.find(Filters.eq("_id", ObjectId(id))).first() ?: return null
            return documentAEntidad(doc)
        } catch (e: Exception) {
            throw MongoDBException("Error al buscar comentario por id", e)
        }
    }

    /**
     * Obtiene todos los comentarios de clientes.
     *
     * @return Lista con todos los comentarios.
     * @throws MongoDBException si ocurre un error al listar.
     */
    fun buscarTodos(): List<ComentarioCliente> {
        try {
            val coleccion = ConexionMongo.obtenerColeccion(COLECCION_COMENTARIOS)
            return coleccion.find().into(mutableListOf()).map { documentAEntidad(it) }
        } catch (e: Exception) {
            throw MongoDBException("Error al listar comentarios", e)
        }
    }

    /**
     * Elimina un comentario por su identificador.
     *
     * @param id El identificador del comentario a eliminar.
     * @return true si se eliminó correctamente, false en caso contrario.
     * @throws MongoDBException si ocurre un error al eliminar.
     */
    fun eliminar(id: String): Boolean {
        try {
            val coleccion = ConexionMongo.obtenerColeccion(COLECCION_COMENTARIOS)
            val resultado = coleccion.deleteOne(Filters.eq("_id", ObjectId(id)))
            return resultado.deletedCount > 0
        } catch (e: Exception) {
            throw MongoDBException("Error al eliminar comentario", e)
        }
    }

    /**
     * Convierte un documento de MongoDB en una entidad [ComentarioCliente].
     *
     * @param doc El documento de MongoDB.
     * @return La entidad [ComentarioCliente] construida a partir del documento.
     */
    private fun documentAEntidad(doc: Document): ComentarioCliente {
        return ComentarioCliente(
            id = doc.getObjectId("_id").toHexString(),
            nombreCliente = doc.getString("nombreCliente"),
            fecha = LocalDateTime.parse(doc.getString("fecha"), formatoFecha)
        )
    }
}
