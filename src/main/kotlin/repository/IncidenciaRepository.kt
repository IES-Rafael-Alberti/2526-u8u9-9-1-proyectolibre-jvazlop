package org.iesra.repository

import com.mongodb.client.model.Filters
import org.bson.Document
import org.bson.types.ObjectId
import org.iesra.exception.MongoDBException
import org.iesra.model.Incidencia
import org.iesra.util.ConexionMongo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class IncidenciaRepository : Repositorio<Incidencia, String> {

    private val formatoFecha = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun guardar(entidad: Incidencia): Incidencia {
        try {
            val coleccion = ConexionMongo.obtenerColeccion()
            val doc = Document()
                .append("numeroHabitacion", entidad.numeroHabitacion)
                .append("descripcion", entidad.descripcion)
                .append("fecha", entidad.fecha.format(formatoFecha))
                .append("resuelta", entidad.resuelta)
            coleccion.insertOne(doc)
            return entidad.copy(id = doc.getObjectId("_id").toHexString())
        } catch (e: Exception) {
            throw MongoDBException("Error al guardar la incidencia en MongoDB", e)
        }
    }

    override fun buscarPorId(id: String): Incidencia? {
        try {
            val coleccion = ConexionMongo.obtenerColeccion()
            val doc = coleccion.find(Filters.eq("_id", ObjectId(id))).first() ?: return null
            return documentAEntidad(doc)
        } catch (e: Exception) {
            throw MongoDBException("Error al buscar incidencia por id", e)
        }
    }

    override fun buscarTodos(): List<Incidencia> {
        try {
            val coleccion = ConexionMongo.obtenerColeccion()
            return coleccion.find().into(mutableListOf()).map { documentAEntidad(it) }
        } catch (e: Exception) {
            throw MongoDBException("Error al listar incidencias", e)
        }
    }

    fun buscarPorHabitacion(numeroHabitacion: Int): List<Incidencia> {
        try {
            val coleccion = ConexionMongo.obtenerColeccion()
            return coleccion.find(Filters.eq("numeroHabitacion", numeroHabitacion))
                .into(mutableListOf()).map { documentAEntidad(it) }
        } catch (e: Exception) {
            throw MongoDBException("Error al buscar incidencias por habitacion", e)
        }
    }

    fun buscarNoResueltas(): List<Incidencia> {
        try {
            val coleccion = ConexionMongo.obtenerColeccion()
            return coleccion.find(Filters.eq("resuelta", false))
                .into(mutableListOf()).map { documentAEntidad(it) }
        } catch (e: Exception) {
            throw MongoDBException("Error al buscar incidencias no resueltas", e)
        }
    }

    override fun actualizar(entidad: Incidencia): Incidencia {
        try {
            val coleccion = ConexionMongo.obtenerColeccion()
            val filtro = Filters.eq("_id", ObjectId(entidad.id))
            val actualizacion = Document("\$set", Document()
                .append("descripcion", entidad.descripcion)
                .append("resuelta", entidad.resuelta))
            val resultado = coleccion.updateOne(filtro, actualizacion)
            if (resultado.matchedCount == 0L) {
                throw MongoDBException("Incidencia con id ${entidad.id} no encontrada")
            }
            return entidad
        } catch (e: MongoDBException) {
            throw e
        } catch (e: Exception) {
            throw MongoDBException("Error al actualizar incidencia", e)
        }
    }

    override fun eliminar(id: String): Boolean {
        try {
            val coleccion = ConexionMongo.obtenerColeccion()
            val resultado = coleccion.deleteOne(Filters.eq("_id", ObjectId(id)))
            return resultado.deletedCount > 0
        } catch (e: Exception) {
            throw MongoDBException("Error al eliminar incidencia", e)
        }
    }

    private fun documentAEntidad(doc: Document): Incidencia {
        return Incidencia(
            id = doc.getObjectId("_id").toHexString(),
            numeroHabitacion = doc.getInteger("numeroHabitacion"),
            descripcion = doc.getString("descripcion"),
            fecha = LocalDateTime.parse(doc.getString("fecha"), formatoFecha),
            resuelta = doc.getBoolean("resuelta", false)
        )
    }
}
