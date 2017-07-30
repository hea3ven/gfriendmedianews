package com.hea3ven.gfriendmedianews.persistance

import com.hea3ven.gfriendmedianews.domain.ServerConfig
import com.hea3ven.gfriendmedianews.domain.SourceConfig
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import java.io.Closeable
import javax.persistence.TypedQuery


class Persistence : Closeable {
	private val sessionFactory: SessionFactory

	init {
		val registry = StandardServiceRegistryBuilder().configure().build()
		try {
			sessionFactory = MetadataSources(registry).buildMetadata().buildSessionFactory()
		} catch (e: Exception) {
			StandardServiceRegistryBuilder.destroy(registry)
			throw RuntimeException(e)
		}
	}

	override fun close() {
		sessionFactory.close()
	}

	fun beginTransaction() = PersistenceTransaction(sessionFactory.openSession())

}

class PersistenceTransaction(val sess: Session) : Closeable {
	init {
		sess.beginTransaction()
	}

	override fun close() {
		sess.transaction.commit()
		sess.close()
	}

	val serverConfigDao: ServerConfigDao = ServerConfigDao(sess)
	val sourceConfigDao: SourceConfigDao = SourceConfigDao(sess)
}

abstract class AbstractDao<T>(val sess: Session) {

	protected abstract fun getEntityClass(): Class<T>

	protected fun find(queryName: String, queryBuilder: (TypedQuery<T>) -> TypedQuery<T>): T? {
		val result = queryBuilder.invoke(sess.createNamedQuery(queryName, getEntityClass())).resultList
		if (result.size != 1)
			return null
		return result[0]
	}

	fun persist(obj: T) {
		sess.saveOrUpdate(obj)
	}
}

class ServerConfigDao(sess: Session) : AbstractDao<ServerConfig>(sess) {
	override fun getEntityClass() = ServerConfig::class.java

	fun findByServerId(id: String) = find(ServerConfig.FIND_BY_SERVER_ID,
			{ q -> q.setParameter("serverId", id) })
}

class SourceConfigDao(sess: Session) : AbstractDao<SourceConfig>(sess) {
	override fun getEntityClass() = SourceConfig::class.java
}
