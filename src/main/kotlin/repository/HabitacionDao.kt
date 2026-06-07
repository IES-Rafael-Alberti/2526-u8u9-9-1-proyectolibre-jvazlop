package org.iesra.repository

import org.iesra.exception.EntidadNoEncontradaException
import org.iesra.model.Habitacion
import org.iesra.util.ConexionH2
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * Implementación del repositorio de habitaciones usando una base de datos H2.
 * Proporciona operaciones CRUD para la entidad [Habitacion] y métodos adicionales de búsqueda.
 */
class HabitacionDao : Repositorio<Habitacion, Int> {

    /**
     * Guarda una nueva habitación en la base de datos.
     *
     * @param entidad La habitación a guardar.
     * @return La habitación guardada.
     */
    override fun guardar(entidad: Habitacion): Habitacion {
        val conexion = ConexionH2.obtenerConexion()
        val sql = "INSERT INTO habitaciones (numero, tipo, precio_noche, disponible) VALUES (?, ?, ?, ?)"
        val statement: PreparedStatement = conexion.prepareStatement(sql)
        statement.setInt(1, entidad.numero)
        statement.setString(2, entidad.tipo)
        statement.setDouble(3, entidad.precioNoche)
        statement.setBoolean(4, entidad.disponible)
        statement.executeUpdate()
        statement.close()
        return entidad
    }

    /**
     * Busca una habitación por su número.
     *
     * @param id El número de la habitación.
     * @return La habitación encontrada, o null si no existe.
     */
    override fun buscarPorId(id: Int): Habitacion? {
        val conexion = ConexionH2.obtenerConexion()
        val sql = "SELECT * FROM habitaciones WHERE numero = ?"
        val statement: PreparedStatement = conexion.prepareStatement(sql)
        statement.setInt(1, id)
        val resultado: ResultSet = statement.executeQuery()
        val habitacion = if (resultado.next()) {
            resultadoAEntidad(resultado)
        } else null
        resultado.close()
        statement.close()
        return habitacion
    }

    /**
     * Obtiene todas las habitaciones ordenadas por número.
     *
     * @return Lista con todas las habitaciones.
     */
    override fun buscarTodos(): List<Habitacion> {
        val conexion = ConexionH2.obtenerConexion()
        val sql = "SELECT * FROM habitaciones ORDER BY numero"
        val statement = conexion.createStatement()
        val resultado: ResultSet = statement.executeQuery(sql)
        val habitaciones = mutableListOf<Habitacion>()
        while (resultado.next()) {
            habitaciones.add(resultadoAEntidad(resultado))
        }
        resultado.close()
        statement.close()
        return habitaciones
    }

    /**
     * Obtiene todas las habitaciones disponibles.
     *
     * @return Lista de habitaciones disponibles ordenadas por número.
     */
    fun buscarDisponibles(): List<Habitacion> {
        val conexion = ConexionH2.obtenerConexion()
        val sql = "SELECT * FROM habitaciones WHERE disponible = TRUE ORDER BY numero"
        val statement = conexion.createStatement()
        val resultado: ResultSet = statement.executeQuery(sql)
        val habitaciones = mutableListOf<Habitacion>()
        while (resultado.next()) {
            habitaciones.add(resultadoAEntidad(resultado))
        }
        resultado.close()
        statement.close()
        return habitaciones
    }

    /**
     * Actualiza los datos de una habitación existente.
     *
     * @param entidad La habitación con los datos actualizados.
     * @return La habitación actualizada.
     * @throws EntidadNoEncontradaException si no existe una habitación con ese número.
     */
    override fun actualizar(entidad: Habitacion): Habitacion {
        val conexion = ConexionH2.obtenerConexion()
        val sql = "UPDATE habitaciones SET tipo = ?, precio_noche = ?, disponible = ? WHERE numero = ?"
        val statement: PreparedStatement = conexion.prepareStatement(sql)
        statement.setString(1, entidad.tipo)
        statement.setDouble(2, entidad.precioNoche)
        statement.setBoolean(3, entidad.disponible)
        statement.setInt(4, entidad.numero)
        val filasAfectadas = statement.executeUpdate()
        statement.close()
        if (filasAfectadas == 0) {
            throw EntidadNoEncontradaException("Habitacion ${entidad.numero} no encontrada")
        }
        return entidad
    }

    /**
     * Elimina una habitación por su número.
     *
     * @param id El número de la habitación a eliminar.
     * @return true si se eliminó correctamente, false en caso contrario.
     */
    override fun eliminar(id: Int): Boolean {
        val conexion = ConexionH2.obtenerConexion()
        val sql = "DELETE FROM habitaciones WHERE numero = ?"
        val statement: PreparedStatement = conexion.prepareStatement(sql)
        statement.setInt(1, id)
        val filasAfectadas = statement.executeUpdate()
        statement.close()
        return filasAfectadas > 0
    }

    /**
     * Convierte un [ResultSet] en una entidad [Habitacion].
     *
     * @param r El resultado de la consulta SQL.
     * @return La entidad [Habitacion] construida a partir del resultado.
     */
    private fun resultadoAEntidad(r: ResultSet): Habitacion {
        return Habitacion(
            numero = r.getInt("numero"),
            tipo = r.getString("tipo"),
            precioNoche = r.getDouble("precio_noche"),
            disponible = r.getBoolean("disponible")
        )
    }
}
