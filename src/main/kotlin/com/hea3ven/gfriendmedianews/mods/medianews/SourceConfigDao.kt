package com.hea3ven.gfriendmedianews.mods.medianews

import com.hea3ven.gfriendmedianews.persistance.AbstractDao
import com.hea3ven.gfriendmedianews.persistance.DaoFactory
import org.hibernate.Session

class SourceConfigDao(sess: Session) : AbstractDao<SourceConfig>(sess) {
	override fun getEntityClass() = SourceConfig::class.java
}

class SourceConfigDaoFactory : DaoFactory<SourceConfigDao> {
	override fun create(sess: Session) = SourceConfigDao(sess)
}