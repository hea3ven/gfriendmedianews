package com.hea3ven.gfriendmedianews.mods.medianews

import com.hea3ven.gfriendmedianews.persistance.AbstractDao
import com.hea3ven.gfriendmedianews.persistance.DaoFactory
import org.hibernate.Session

class ServerConfigDao(sess: Session) : AbstractDao<ServerConfig>(sess) {
	override fun getEntityClass() = ServerConfig::class.java

	fun findByServerId(id: String): ServerConfig? {
		val crit = createCrit()
		val servConf = crit.from(ServerConfig::class.java)
		crit.where(cb.equal(servConf.get<String>("serverId"), id))
		return find(crit)
	}
}

class ServerConfigDaoFactory : DaoFactory<ServerConfigDao> {
	override fun create(sess: Session) = ServerConfigDao(sess)
}
