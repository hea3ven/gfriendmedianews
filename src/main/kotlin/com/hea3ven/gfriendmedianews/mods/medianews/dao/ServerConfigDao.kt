package com.hea3ven.gfriendmedianews.mods.medianews.dao

import com.hea3ven.gfriendmedianews.mods.medianews.model.NewsConfig
import com.hea3ven.gfriendmedianews.persistance.AbstractDao
import com.hea3ven.gfriendmedianews.persistance.DaoFactory
import org.mongodb.morphia.Datastore

class NewsConfigDao(ds: Datastore) : AbstractDao<NewsConfig>(ds) {
    override fun getEntityClass() = NewsConfig::class.java

    fun findByServerId(id: String): List<NewsConfig> {
        return createQuery().field("serverId").equal(id).asList()
    }

    //	fun updateSourceConfig(serverCfg: ServerConfig, sourceCfg: SourceConfig) {
    //		ds.update(createQuery().filter("id", serverCfg.id).filter("sourceConfigs.id", sourceCfg.id),
    //				createUpdate().set("sourceConfigs.$", sourceCfg))
    //	}
}

class ServerConfigDaoFactory : DaoFactory<NewsConfig> {
    override fun create(ds: Datastore) = NewsConfigDao(ds)
}
