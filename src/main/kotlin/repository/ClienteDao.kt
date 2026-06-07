package org.iesra.repository

import org.iesra.exception.EntidadNoEncontradaException
import org.iesra.model.Cliente
import org.iesra.util.ConexionH2
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * Implementación del repositorio de clientes usando una base de datos H2.
 * Proporciona operaciones CRUD para la entidad [Cliente].
 */
class ClienteDao : Repositorio<Cliente, String> {

    /**
     * Guarda un nuevo cliente en la base de datos.
     *
     * @param entidad El cliente a guardar.
     * @return El cliente guardado.
     */
    override fun guardar(entidad: Cliente): Cliente {
        val conexion = ConexionH2.obtenerConexion()
        val sql = "INSERT INTO clientes (id, nombre, email, telefono) VALUES (?, ?, ?, ?)"
        val statement: PreparedStatement = conexion.prepareStatement(sql)
        statement.setString(1, entidad.id)
        statement.setString(2, entidad.nombre)
        statement.setString(3, entidad.email)
        statement.setString(4, entidad.telefono)
        statement.executeUpdate()
        statement.close()
        return entidad
    }

    /**
     * Busca un cliente por su identificador.
     *
     * @param id El identificador del cliente.
     * @return El cliente encontrado, o null si no existe.
     */
    override fun buscarPorId(id: String): Cliente? {
        val conexion = ConexionH2.obtenerConexion()
        val sql = "SELECT * FROM clientes WHERE id = ?"
        val statement: PreparedStatement = conexion.prepareStatement(sql)
        statement.setString(1, id)
        val resultado: ResultSet = statement.executeQuery()
        val cliente = if (resultado.next()) {
            Cliente(
                id = resultado.getString("id"),
                nombre = resultado.getString("nombre"),
                email = resultado.getString("email"),
                telefono = resultado.getString("telefono")
            )
        } else null
        resultado.close()
        statement.close()
        return cliente
    }

    /**
     * Obtiene todos los clientes ordenados por nombre.
     *
     * @return Lista con todos los clientes.
     */
    override fun buscarTodos(): List<Cliente> {
        val conexion = ConexionH2.obtenerConexion()
        val sql = "SELECT * FROM clientes ORDER BY nombre"
        val statement = conexion.createStatement()
        val resultado: ResultSet = statement.executeQuery(sql)
        val clientes = mutableListOf<Cliente>()
        while (resultado.next()) {
            clientes.add(
                Cliente(
                    id = resultado.getString("id"),
                    nombre = resultado.getString("nombre"),
                    email = resultado.getString("email"),
                    telefono = resultado.getString("telefono")
                )
            )
        }
        resultado.close()
        statement.close()
        return clientes
    }

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param entidad El cliente con los datos actualizados.
     * @return El cliente actualizado.
     * @throws EntidadNoEncontradaException si no existe un cliente con ese id.
     */
    override fun actualizar(entidad: Cliente): Cliente {
        val conexion = ConexionH2.obtenerConexion()
        val sql = "UPDATE clientes SET nombre = ?, email = ?, telefono = ? WHERE id = ?"
        val statement: PreparedStatement = conexion.prepareStatement(sql)
        statement.setString(1, entidad.nombre)
        statement.setString(2, entidad.email)
        statement.setString(3, entidad.telefono)
        statement.setString(4, entidad.id)
        val filasAfectadas = statement.executeUpdate()
        statement.close()
        if (filasAfectadas == 0) {
            throw EntidadNoEncontradaException("Cliente con id ${entidad.id} no encontrado")
        }
        return entidad
    }

    /**
     * Elimina un cliente por su identificador.
     *
     * @param id El identificador del cliente a eliminar.
     * @return true si se eliminó correctamente, false en caso contrario.
     */
    override fun eliminar(id: String): Boolean {
        val conexion = ConexionH2.obtenerConexion()
        val sql = "DELETE FROM clientes WHERE id = ?"
        val statement: PreparedStatement = conexion.prepareStatement(sql)
        statement.setString(1, id)
        val filasAfectadas = statement.executeUpdate()
        statement.close()
        return filasAfectadas > 0
    }
}
