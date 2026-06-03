package org.iesra.util

import org.iesra.exception.ConexionBaseDatosException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

object ConexionH2 {

    private const val URL = "jdbc:h2:./data/hotelmaligno;DB_CLOSE_DELAY=-1"
    private const val USUARIO = "sa"
    private const val CONTRASENA = ""

    private var conexion: Connection? = null

    fun obtenerConexion(): Connection {
        if (conexion == null || conexion!!.isClosed) {
            try {
                Class.forName("org.h2.Driver")
                conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA)
            } catch (e: Exception) {
                throw ConexionBaseDatosException("Error al conectar con H2", e)
            }
        }
        return conexion!!
    }

    fun cerrarConexion() {
        try {
            conexion?.close()
            conexion = null
        } catch (e: Exception) {
            throw ConexionBaseDatosException("Error al cerrar conexion H2", e)
        }
    }

    fun inicializarTablas() {
        val conexion = obtenerConexion()
        try {
            val statement: Statement = conexion.createStatement()

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS clientes (
                    id VARCHAR(20) PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL,
                    email VARCHAR(100) NOT NULL,
                    telefono VARCHAR(20)
                )
            """.trimIndent())

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS habitaciones (
                    numero INT PRIMARY KEY,
                    tipo VARCHAR(50) NOT NULL,
                    precio_noche DOUBLE NOT NULL,
                    disponible BOOLEAN DEFAULT TRUE
                )
            """.trimIndent())

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS reservas (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    id_cliente VARCHAR(20) NOT NULL,
                    numero_habitacion INT NOT NULL,
                    fecha_entrada DATE NOT NULL,
                    fecha_salida DATE NOT NULL,
                    estado VARCHAR(20) DEFAULT 'pendiente',
                    pagada BOOLEAN DEFAULT FALSE,
                    num_personas INT DEFAULT 1,
                    segundo_huesped VARCHAR(100) DEFAULT '',
                    FOREIGN KEY (id_cliente) REFERENCES clientes(id)
                )
            """.trimIndent())

            statement.close()
        } catch (e: Exception) {
            throw ConexionBaseDatosException("Error al crear tablas en H2", e)
        }
    }
}
