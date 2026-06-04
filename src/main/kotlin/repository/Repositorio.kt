package org.iesra.repository

interface Repositorio<T, ID> {
    fun guardar(entidad: T): T
    fun buscarPorId(id: ID): T?
    fun buscarTodos(): List<T>
    fun actualizar(entidad: T): T
    fun eliminar(id: ID): Boolean
}
