package org.iesra.repository

import org.iesra.exception.EntidadNoEncontradaException
import org.iesra.model.Reserva
import org.iesra.util.ConexionH2
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.LocalDate

/**
 * Implementación del repositorio de reservas usando una base de datos H2.
 * Proporciona operaciones CRUD para la entidad [Reserva] y métodos adicionales de búsqueda.
 */
class ReservaDao : Repositorio<Reserva, Int> {

    /**
     * Guarda una nueva reserva en la base de datos y asigna el identificador generado.
     *
     * @param entidad La reserva a guardar.
     * @return La reserva guardada con el id asignado.
     */
    override fun guardar(entidad: Reserva): Reserva {
        val conexion = ConexionH2.obtenerConexion()
        val sql = """
            INSERT INTO reservas (id_cliente, numero_habitacion, fecha_entrada, fecha_salida, estado, pagada, num_personas, segundo_huesped)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
        val statement: PreparedStatement = conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)
        statement.setString(1, entidad.idCliente)
        statement.setInt(2, entidad.numeroHabitacion)
        statement.setDate(3, Date.valueOf(entidad.fechaEntrada))
        statement.setDate(4, Date.valueOf(entidad.fechaSalida))
        statement.setString(5, entidad.estado)
        statement.setBoolean(6, entidad.pagada)
        statement.setInt(7, entidad.numPersonas)
        statement.setString(8, entidad.segundoHuesped)
        statement.executeUpdate()
        val claves = statement.generatedKeys
        val idGenerado = if (claves.next()) claves.getInt(1) else 0
        claves.close()
        statement.close()
        return entidad.copy(id = idGenerado)
    }

    /**
     * Busca una reserva por su identificador.
     *
     * @param id El identificador de la reserva.
     * @return La reserva encontrada, o null si no existe.
     */
    override fun buscarPorId(id: Int): Reserva? {
        val conexion = ConexionH2.obtenerConexion()
        val sql = "SELECT * FROM reservas WHERE id = ?"
        val statement: PreparedStatement = conexion.prepareStatement(sql)
        statement.setInt(1, id)
        val resultado: ResultSet = statement.executeQuery()
        val reserva = if (resultado.next()) {
            resultadoAEntidad(resultado)
        } else null
        resultado.close()
        statement.close()
        return reserva
    }

    /**
     * Obtiene todas las reservas ordenadas por fecha de entrada descendente.
     *
     * @return Lista con todas las reservas.
     */
    override fun buscarTodos(): List<Reserva> {
        val conexion = ConexionH2.obtenerConexion()
        val sql = "SELECT * FROM reservas ORDER BY fecha_entrada DESC"
        val statement = conexion.createStatement()
        val resultado: ResultSet = statement.executeQuery(sql)
        val reservas = mutableListOf<Reserva>()
        while (resultado.next()) {
            reservas.add(resultadoAEntidad(resultado))
        }
        resultado.close()
        statement.close()
        return reservas
    }

    /**
     * Busca todas las reservas asociadas a un cliente.
     *
     * @param idCliente El identificador del cliente.
     * @return Lista de reservas del cliente ordenadas por fecha de entrada descendente.
     */
    fun buscarPorCliente(idCliente: String): List<Reserva> {
        val conexion = ConexionH2.obtenerConexion()
        val sql = "SELECT * FROM reservas WHERE id_cliente = ? ORDER BY fecha_entrada DESC"
        val statement: PreparedStatement = conexion.prepareStatement(sql)
        statement.setString(1, idCliente)
        val resultado: ResultSet = statement.executeQuery()
        val reservas = mutableListOf<Reserva>()
        while (resultado.next()) {
            reservas.add(resultadoAEntidad(resultado))
        }
        resultado.close()
        statement.close()
        return reservas
    }

    /**
     * Actualiza los datos de una reserva existente.
     *
     * @param entidad La reserva con los datos actualizados.
     * @return La reserva actualizada.
     * @throws EntidadNoEncontradaException si no existe una reserva con ese id.
     */
    override fun actualizar(entidad: Reserva): Reserva {
        val conexion = ConexionH2.obtenerConexion()
        val sql = """
            UPDATE reservas SET id_cliente = ?, numero_habitacion = ?, 
            fecha_entrada = ?, fecha_salida = ?, estado = ?, pagada = ?,
            num_personas = ?, segundo_huesped = ? WHERE id = ?
        """.trimIndent()
        val statement: PreparedStatement = conexion.prepareStatement(sql)
        statement.setString(1, entidad.idCliente)
        statement.setInt(2, entidad.numeroHabitacion)
        statement.setDate(3, Date.valueOf(entidad.fechaEntrada))
        statement.setDate(4, Date.valueOf(entidad.fechaSalida))
        statement.setString(5, entidad.estado)
        statement.setBoolean(6, entidad.pagada)
        statement.setInt(7, entidad.numPersonas)
        statement.setString(8, entidad.segundoHuesped)
        statement.setInt(9, entidad.id)
        val filasAfectadas = statement.executeUpdate()
        statement.close()
        if (filasAfectadas == 0) {
            throw EntidadNoEncontradaException("Reserva con id ${entidad.id} no encontrada")
        }
        return entidad
    }

    /**
     * Elimina una reserva por su identificador.
     *
     * @param id El identificador de la reserva a eliminar.
     * @return true si se eliminó correctamente, false en caso contrario.
     */
    override fun eliminar(id: Int): Boolean {
        val conexion = ConexionH2.obtenerConexion()
        val sql = "DELETE FROM reservas WHERE id = ?"
        val statement: PreparedStatement = conexion.prepareStatement(sql)
        statement.setInt(1, id)
        val filasAfectadas = statement.executeUpdate()
        statement.close()
        return filasAfectadas > 0
    }

    /**
     * Convierte un [ResultSet] en una entidad [Reserva].
     *
     * @param r El resultado de la consulta SQL.
     * @return La entidad [Reserva] construida a partir del resultado.
     */
    private fun resultadoAEntidad(r: ResultSet): Reserva {
        return Reserva(
            id = r.getInt("id"),
            idCliente = r.getString("id_cliente"),
            numeroHabitacion = r.getInt("numero_habitacion"),
            fechaEntrada = r.getDate("fecha_entrada").toLocalDate(),
            fechaSalida = r.getDate("fecha_salida").toLocalDate(),
            estado = r.getString("estado"),
            pagada = r.getBoolean("pagada"),
            numPersonas = r.getInt("num_personas"),
            segundoHuesped = r.getString("segundo_huesped") ?: ""
        )
    }
}
