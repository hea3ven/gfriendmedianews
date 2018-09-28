package com.hea3ven.gfriendmedianews.mods.f1announcement.dao

import com.hea3ven.gfriendmedianews.mods.f1announcement.model.F1ServerConfig
import com.hea3ven.gfriendmedianews.persistance.AbstractDao
import com.hea3ven.gfriendmedianews.persistance.DaoFactory
import org.mongodb.morphia.Datastore

class F1ServerConfigDao(ds: Datastore) : AbstractDao<F1ServerConfig>(ds) {

    override fun getEntityClass() = F1ServerConfig::class.java

    fun findByServerId(id: String): F1ServerConfig? {
        return createQuery().field("serverId").equal(id).get()
    }
}

class F1ServerConfigDaoFactory : DaoFactory<F1ServerConfig> {
    override fun create(ds: Datastore) = F1ServerConfigDao(ds)
}
