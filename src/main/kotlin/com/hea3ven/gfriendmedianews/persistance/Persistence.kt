package com.hea3ven.gfriendmedianews.persistance

import com.hea3ven.gfriendmedianews.domain.ServerConfig
import com.hea3ven.gfriendmedianews.domain.SlapStat
import com.hea3ven.gfriendmedianews.domain.SourceConfig
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import java.io.Closeable
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery


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
	val slapStatDao: SlapStatDao = SlapStatDao(sess)
}

abstract class AbstractDao<T>(val sess: Session) {

	protected abstract fun getEntityClass(): Class<T>

	protected val cb: CriteriaBuilder
		get() = sess.criteriaBuilder

	protected fun createCrit() = createCrit(getEntityClass())
	protected fun <R> createCrit(klass: Class<R>) = cb.createQuery(klass)!!

	protected fun <R> find(crit: CriteriaQuery<R>): R? {
		return find(sess.createQuery(crit))
	}

	protected fun <R> find(query: TypedQuery<R>): R? {
		val result = query.resultList
		if (result.size != 1)
			return null
		return result[0]
	}

	private fun findNamed(queryBuilder: (TypedQuery<T>) -> TypedQuery<T>, queryName: String) = find(
			queryBuilder.invoke(sess.createNamedQuery(queryName, getEntityClass())))

	fun persist(obj: T) {
		sess.saveOrUpdate(obj)
	}
}

class ServerConfigDao(sess: Session) : AbstractDao<ServerConfig>(sess) {
	override fun getEntityClass() = ServerConfig::class.java

	fun findByServerId(id: String): ServerConfig? {
		val crit = createCrit()
		val servConf = crit.from(ServerConfig::class.java)
		crit.where(cb.equal(servConf.get<String>("serverId"), id))
		return find(crit)
	}
}

class SourceConfigDao(sess: Session) : AbstractDao<SourceConfig>(sess) {
	override fun getEntityClass() = SourceConfig::class.java
}

class SlapStatDao(sess: Session) : AbstractDao<SlapStat>(sess) {
	override fun getEntityClass() = SlapStat::class.java

	fun find(slapperId: String?, slappeeId: String): SlapStat? {
		val crit = createCrit()
		val slapStat = crit.from(SlapStat::class.java)
		crit.where(cb.and(cb.equal(slapStat.get<String>("slapperId"), slapperId),
				cb.equal(slapStat.get<String>("slappeeId"), slappeeId)))
		return find(crit)
	}

	fun countTimesSlapper(slapperId: String): Int? {
		val crit = createCrit(Int::class.java)
		val slapStat = crit.from(SlapStat::class.java)
		crit.select(cb.sum(slapStat.get<Int>("count")))
		crit.where(cb.equal(slapStat.get<String>("slapperId"), slapperId))
		return find(crit)
	}

	fun countTimesSlappee(slappeeId: String?): Int? {
		val crit = createCrit(Int::class.java)
		val slapStat = crit.from(SlapStat::class.java)
		crit.select(cb.sum(slapStat.get<Int>("count")))
		crit.where(cb.equal(slapStat.get<String>("slappeeId"), slappeeId))
		return find(crit)
	}
}
