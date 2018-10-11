package com.hea3ven.gfriendmedianews.persistance

import com.hea3ven.gfriendmedianews.Config
import org.mongodb.morphia.Datastore
import org.mongodb.morphia.Morphia
import java.io.Closeable


class Persistence : Closeable {
    private val morphia = Morphia().apply {
        ds = this.createDatastore(com.mongodb.MongoClient(Config.mongoDbHost, Config.mongoDbPort), "gfriendmedianews")
        this.mapPackage("com.hea3ven.gfriendmedianews")
    }

    private lateinit var ds: Datastore

    private val daoFactories: MutableMap<Class<*>, AbstractDao<*>> = mutableMapOf()

    fun <T, R : AbstractDao<T>> registerDaoFactory(daoClass: Class<R>, daoFactory: DaoFactory<T>) {
        daoFactories[daoClass] = daoFactory.create(ds)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T, R : AbstractDao<T>> getDao(daoClass: Class<R>): R {
        return daoFactories[daoClass] as R
    }

    override fun close() {
    }

    fun beginTransaction() = PersistenceTransaction(this)

}

interface DaoFactory<T> {
    fun create(ds: Datastore): AbstractDao<T>
}

class PersistenceTransaction(val persistence: Persistence) : Closeable {
    override fun close() {
    }

    fun <T, R : AbstractDao<T>> getDao(daoClass: Class<R>): R {
        return persistence.getDao(daoClass)
    }

}

abstract class AbstractDao<T>(protected val ds: Datastore) {

    protected abstract fun getEntityClass(): Class<T>

    protected fun createQuery() = createQuery(getEntityClass())
    protected fun <R> createQuery(klass: Class<R>) = ds.createQuery(klass)!!

    protected fun createUpdate() = createUpdate(getEntityClass())
    protected fun <R> createUpdate(klass: Class<R>) = ds.createUpdateOperations(klass)!!

    fun persist(obj: T) {
        ds.save(obj)
    }

    fun delete(obj: T) {
        ds.delete(obj)
    }
}

