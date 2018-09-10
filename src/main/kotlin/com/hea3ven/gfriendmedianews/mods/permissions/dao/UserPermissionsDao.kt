package com.hea3ven.gfriendmedianews.mods.permissions.dao

import com.hea3ven.gfriendmedianews.mods.permissions.model.UserPermissions
import com.hea3ven.gfriendmedianews.persistance.AbstractDao
import com.hea3ven.gfriendmedianews.persistance.DaoFactory
import de.btobastian.javacord.entities.User
import org.mongodb.morphia.Datastore

class UserPermissionsDao(ds: Datastore) : AbstractDao<UserPermissions>(ds) {
    override fun getEntityClass() = UserPermissions::class.java

    fun findByUserId(user: User): UserPermissions? {
        return createQuery().field("userId").equal(user.id).get()
    }
}

class UserPermissionsDaoFactory : DaoFactory<UserPermissions> {
    override fun create(ds: Datastore) = UserPermissionsDao(ds)
}
