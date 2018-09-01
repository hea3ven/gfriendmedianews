package com.hea3ven.gfriendmedianews.mods.socialinteraction

import com.hea3ven.gfriendmedianews.persistance.AbstractDao
import com.hea3ven.gfriendmedianews.persistance.DaoFactory
import org.mongodb.morphia.Datastore

class SocialInteractionDao(ds: Datastore) : AbstractDao<SocialInteractionStat>(ds) {
    override fun getEntityClass() = SocialInteractionStat::class.java

    fun countTimesSource(type: InteractionType, sourceId: String): Long? {
        return createQuery().field("type").equal(type).field("sourceId").equal(sourceId).count()
    }

    fun countTimesTarget(type: InteractionType, targetId: String): Long? {
        return createQuery().field("type").equal(type).field("targetId").equal(targetId).count()
    }
}

class SocialInteractionDaoFactory : DaoFactory<SocialInteractionStat> {
    override fun create(ds: Datastore) = SocialInteractionDao(ds)
}
