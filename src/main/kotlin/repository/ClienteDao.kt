package org.iesra.repository

import org.iesra.exception.EntidadNoEncontradaException
import org.iesra.model.Cliente
import org.iesra.util.ConexionH2
import java.sql.PreparedStatement
import java.sql.ResultSet

class ClienteDao : Repositorio<Cliente, String> {

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
