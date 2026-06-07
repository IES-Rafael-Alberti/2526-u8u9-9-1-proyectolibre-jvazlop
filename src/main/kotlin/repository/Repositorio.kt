package org.iesra.repository

/**
 * Interfaz genérica que define las operaciones básicas de un repositorio CRUD.
 *
 * @param T Tipo de la entidad gestionada por el repositorio.
 * @param ID Tipo del identificador único de la entidad.
 */
interface Repositorio<T, ID> {

    /**
     * Guarda una nueva entidad en el repositorio.
     *
     * @param entidad La entidad a guardar.
     * @return La entidad guardada, posiblemente con datos generados (como el id).
     */
    fun guardar(entidad: T): T

    /**
     * Busca una entidad por su identificador único.
     *
     * @param id El identificador único de la entidad.
     * @return La entidad encontrada, o null si no existe.
     */
    fun buscarPorId(id: ID): T?

    /**
     * Obtiene todas las entidades del repositorio.
     *
     * @return Lista con todas las entidades.
     */
    fun buscarTodos(): List<T>

    /**
     * Actualiza una entidad existente en el repositorio.
     *
     * @param entidad La entidad con los datos actualizados.
     * @return La entidad actualizada.
     */
    fun actualizar(entidad: T): T

    /**
     * Elimina una entidad del repositorio por su identificador.
     *
     * @param id El identificador único de la entidad a eliminar.
     * @return true si se eliminó correctamente, false en caso contrario.
     */
    fun eliminar(id: ID): Boolean
}
