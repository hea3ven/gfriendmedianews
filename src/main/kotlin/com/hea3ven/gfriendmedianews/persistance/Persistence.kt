package com.hea3ven.gfriendmedianews.persistance

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

	private val daoFactories: MutableMap<Class<*>, DaoFactory<*>> = mutableMapOf()

	init {
		val registry = StandardServiceRegistryBuilder().configure().build()
		try {
			sessionFactory = MetadataSources(registry).buildMetadata().buildSessionFactory()
		} catch (e: Throwable) {
			StandardServiceRegistryBuilder.destroy(registry)
			throw RuntimeException(e)
		}
	}

	fun registerDaoFactory(daoClass:Class<*>, daoFactory: DaoFactory<*>){
		daoFactories.put(daoClass, daoFactory)
	}

	fun <R> getDaoFactory(daoClass: Class<R>): DaoFactory<R> {
		return daoFactories[daoClass] as DaoFactory<R>
	}

	override fun close() {
		sessionFactory.close()
	}

	fun beginTransaction() = PersistenceTransaction(this, sessionFactory.openSession())

}

interface DaoFactory<T> {
	fun create(sess: Session): T
}

class PersistenceTransaction(val persistence: Persistence, val sess: Session) : Closeable {
	init {
		sess.beginTransaction()
	}

	override fun close() {
		sess.transaction.commit()
		sess.close()
	}

	fun <T, R : AbstractDao<T>> getDao(daoClass :Class<R>): R {
		return persistence.getDaoFactory(daoClass).create(sess)
	}

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

