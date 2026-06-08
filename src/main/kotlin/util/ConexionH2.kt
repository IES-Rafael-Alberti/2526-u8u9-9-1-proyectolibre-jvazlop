package org.iesra.util

import org.iesra.exception.ConexionBaseDatosException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

/**
 * Objeto singleton para la gestión de la conexión con la base de datos H2.
 * Proporciona métodos para obtener, cerrar la conexión e inicializar las tablas.
 */
object ConexionH2 {

    private const val URL = "jdbc:h2:./data/gestorhotel;DB_CLOSE_DELAY=-1"
    private const val USUARIO = "sa"
    private const val CONTRASENA = ""

    private var conexion: Connection? = null

    /**
     * Obtiene la conexión a la base de datos H2.
     * Si la conexión no existe o está cerrada, crea una nueva.
     * @return Conexión activa a H2
     * @throws ConexionBaseDatosException si no se puede establecer la conexión
     */
    fun obtenerConexion(): Connection {
        if (conexion == null || conexion?.isClosed == true) {
            try {
                Class.forName("org.h2.Driver")
                conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA)
            } catch (e: Exception) {
                throw ConexionBaseDatosException("Error al conectar con H2", e)
            }
        }
        return conexion ?: throw ConexionBaseDatosException("La conexion H2 no se pudo establecer")
    }

    /**
     * Cierra la conexión con la base de datos H2.
     * @throws ConexionBaseDatosException si ocurre un error al cerrar la conexión
     */
    fun cerrarConexion() {
        try {
            conexion?.close()
            conexion = null
        } catch (e: Exception) {
            throw ConexionBaseDatosException("Error al cerrar conexion H2", e)
        }
    }

    /**
     * Inicializa las tablas necesarias en la base de datos H2 si no existen.
     * Crea las tablas de clientes, habitaciones y reservas.
     * @throws ConexionBaseDatosException si ocurre un error al crear las tablas
     */
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
