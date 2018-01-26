package com.hea3ven.gfriendmedianews.mods.socialinteraction

import com.hea3ven.gfriendmedianews.persistance.AbstractDao
import com.hea3ven.gfriendmedianews.persistance.DaoFactory
import org.hibernate.Session

class SocialInteractionDao(sess: Session) : AbstractDao<SocialInteractionStat>(sess) {
	override fun getEntityClass() = SocialInteractionStat::class.java

	fun countTimesSource(type: InteractionType, sourceId: String): Long? {
		val crit = createCrit(Long::class.java)
		val slapStat = crit.from(SocialInteractionStat::class.java)
		crit.select(cb.count(slapStat.get<Long>("id")))
		crit.where(cb.and(cb.equal(slapStat.get<InteractionType>("type"), type),
				cb.equal(slapStat.get<String>("sourceId"), sourceId)))
		return find(crit)
	}

	fun countTimesTarget(type: InteractionType, targetId: String): Long? {
		val crit = createCrit(Long::class.java)
		val slapStat = crit.from(SocialInteractionStat::class.java)
		crit.select(cb.count(slapStat.get<Long>("id")))
		crit.where(cb.and(cb.equal(slapStat.get<InteractionType>("type"), type),
				cb.equal(slapStat.get<String>("targetId"), targetId)))
		return find(crit)
	}
}

class SocialInteractionDaoFactory : DaoFactory<SocialInteractionDao> {
	override fun create(sess: Session) = SocialInteractionDao(sess)
}
