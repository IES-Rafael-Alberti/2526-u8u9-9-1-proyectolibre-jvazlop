package org.iesra.repository

import com.mongodb.client.model.Filters
import org.bson.Document
import org.bson.types.ObjectId
import org.iesra.exception.MongoDBException
import org.iesra.model.Incidencia
import org.iesra.util.ConexionMongo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Implementación del repositorio de incidencias usando MongoDB.
 * Proporciona operaciones CRUD para la entidad [Incidencia] y métodos adicionales de búsqueda.
 */
class IncidenciaRepository : Repositorio<Incidencia, String> {

    private val formatoFecha = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    /**
     * Guarda una nueva incidencia en MongoDB.
     *
     * @param entidad La incidencia a guardar.
     * @return La incidencia guardada con el id asignado por MongoDB.
     * @throws MongoDBException si ocurre un error al guardar.
     */
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

    /**
     * Busca una incidencia por su identificador.
     *
     * @param id El identificador de la incidencia.
     * @return La incidencia encontrada, o null si no existe.
     * @throws MongoDBException si ocurre un error al buscar.
     */
    override fun buscarPorId(id: String): Incidencia? {
        try {
            val coleccion = ConexionMongo.obtenerColeccion()
            val doc = coleccion.find(Filters.eq("_id", ObjectId(id))).first() ?: return null
            return documentAEntidad(doc)
        } catch (e: Exception) {
            throw MongoDBException("Error al buscar incidencia por id", e)
        }
    }

    /**
     * Obtiene todas las incidencias.
     *
     * @return Lista con todas las incidencias.
     * @throws MongoDBException si ocurre un error al listar.
     */
    override fun buscarTodos(): List<Incidencia> {
        try {
            val coleccion = ConexionMongo.obtenerColeccion()
            return coleccion.find().into(mutableListOf()).map { documentAEntidad(it) }
        } catch (e: Exception) {
            throw MongoDBException("Error al listar incidencias", e)
        }
    }

    /**
     * Busca todas las incidencias de una habitación específica.
     *
     * @param numeroHabitacion El número de la habitación.
     * @return Lista de incidencias de la habitación.
     * @throws MongoDBException si ocurre un error al buscar.
     */
    fun buscarPorHabitacion(numeroHabitacion: Int): List<Incidencia> {
        try {
            val coleccion = ConexionMongo.obtenerColeccion()
            return coleccion.find(Filters.eq("numeroHabitacion", numeroHabitacion))
                .into(mutableListOf()).map { documentAEntidad(it) }
        } catch (e: Exception) {
            throw MongoDBException("Error al buscar incidencias por habitacion", e)
        }
    }

    /**
     * Obtiene todas las incidencias que aún no han sido resueltas.
     *
     * @return Lista de incidencias no resueltas.
     * @throws MongoDBException si ocurre un error al buscar.
     */
    fun buscarNoResueltas(): List<Incidencia> {
        try {
            val coleccion = ConexionMongo.obtenerColeccion()
            return coleccion.find(Filters.eq("resuelta", false))
                .into(mutableListOf()).map { documentAEntidad(it) }
        } catch (e: Exception) {
            throw MongoDBException("Error al buscar incidencias no resueltas", e)
        }
    }

    /**
     * Actualiza los datos de una incidencia existente.
     *
     * @param entidad La incidencia con los datos actualizados.
     * @return La incidencia actualizada.
     * @throws MongoDBException si no existe una incidencia con ese id o si ocurre un error.
     */
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

    /**
     * Elimina una incidencia por su identificador.
     *
     * @param id El identificador de la incidencia a eliminar.
     * @return true si se eliminó correctamente, false en caso contrario.
     * @throws MongoDBException si ocurre un error al eliminar.
     */
    override fun eliminar(id: String): Boolean {
        try {
            val coleccion = ConexionMongo.obtenerColeccion()
            val resultado = coleccion.deleteOne(Filters.eq("_id", ObjectId(id)))
            return resultado.deletedCount > 0
        } catch (e: Exception) {
            throw MongoDBException("Error al eliminar incidencia", e)
        }
    }

    /**
     * Convierte un documento de MongoDB en una entidad [Incidencia].
     *
     * @param doc El documento de MongoDB.
     * @return La entidad [Incidencia] construida a partir del documento.
     */
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
