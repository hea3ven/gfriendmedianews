package com.hea3ven.gfriendmedianews.mods.socialinteraction

import com.hea3ven.gfriendmedianews.persistance.AbstractDao
import com.hea3ven.gfriendmedianews.persistance.DaoFactory
import org.hibernate.Session

class SocialInteractionDao(sess: Session) : AbstractDao<SocialInteractionStat>(sess) {
	override fun getEntityClass() = SocialInteractionStat::class.java

	fun find(slapperId: String?, slappeeId: String): SocialInteractionStat? {
		val crit = createCrit()
		val slapStat = crit.from(SocialInteractionStat::class.java)
		crit.where(cb.and(cb.equal(slapStat.get<String>("slapperId"), slapperId),
				cb.equal(slapStat.get<String>("slappeeId"), slappeeId)))
		return find(crit)
	}

	fun countTimesSlapper(slapperId: String): Int? {
		val crit = createCrit(Int::class.java)
		val slapStat = crit.from(SocialInteractionStat::class.java)
		crit.select(cb.sum(slapStat.get<Int>("count")))
		crit.where(cb.equal(slapStat.get<String>("slapperId"), slapperId))
		return find(crit)
	}

	fun countTimesSlappee(slappeeId: String?): Int? {
		val crit = createCrit(Int::class.java)
		val slapStat = crit.from(SocialInteractionStat::class.java)
		crit.select(cb.sum(slapStat.get<Int>("count")))
		crit.where(cb.equal(slapStat.get<String>("slappeeId"), slappeeId))
		return find(crit)
	}
}

class SocialInteractionDaoFactory : DaoFactory<SocialInteractionDao> {
	override fun create(sess: Session) = SocialInteractionDao(sess)
}
