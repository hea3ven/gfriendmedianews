package com.hea3ven.gfriendmedianews.mods.f1announcement

import com.hea3ven.gfriendmedianews.persistance.AbstractDao
import com.hea3ven.gfriendmedianews.persistance.DaoFactory
import org.hibernate.Session

class F1ServerConfigDao(sess: Session) : AbstractDao<F1ServerConfig>(sess) {

	override fun getEntityClass() = F1ServerConfig::class.java

	fun findByServerId(id: String): F1ServerConfig? {
		val crit = createCrit()
		val servConf = crit.from(F1ServerConfig::class.java)
		crit.where(cb.equal(servConf.get<String>("serverId"), id))
		return find(crit)
	}
}

class F1ServerConfigDaoFactory : DaoFactory<F1ServerConfigDao> {
	override fun create(sess: Session) = F1ServerConfigDao(sess)
}
