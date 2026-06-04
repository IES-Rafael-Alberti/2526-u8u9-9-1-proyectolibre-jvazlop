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

class ComentarioClienteRepository {

    private val formatoFecha = DateTimeFormatter.ISO_LOCAL_DATE_TIME

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

    fun buscarPorId(id: String): ComentarioCliente? {
        try {
            val coleccion = ConexionMongo.obtenerColeccion(COLECCION_COMENTARIOS)
            val doc = coleccion.find(Filters.eq("_id", ObjectId(id))).first() ?: return null
            return documentAEntidad(doc)
        } catch (e: Exception) {
            throw MongoDBException("Error al buscar comentario por id", e)
        }
    }

    fun buscarTodos(): List<ComentarioCliente> {
        try {
            val coleccion = ConexionMongo.obtenerColeccion(COLECCION_COMENTARIOS)
            return coleccion.find().into(mutableListOf()).map { documentAEntidad(it) }
        } catch (e: Exception) {
            throw MongoDBException("Error al listar comentarios", e)
        }
    }

    fun eliminar(id: String): Boolean {
        try {
            val coleccion = ConexionMongo.obtenerColeccion(COLECCION_COMENTARIOS)
            val resultado = coleccion.deleteOne(Filters.eq("_id", ObjectId(id)))
            return resultado.deletedCount > 0
        } catch (e: Exception) {
            throw MongoDBException("Error al eliminar comentario", e)
        }
    }

    private fun documentAEntidad(doc: Document): ComentarioCliente {
        return ComentarioCliente(
            id = doc.getObjectId("_id").toHexString(),
            nombreCliente = doc.getString("nombreCliente"),
            fecha = LocalDateTime.parse(doc.getString("fecha"), formatoFecha)
        )
    }
}
