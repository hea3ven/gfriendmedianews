package com.hea3ven.gfriendmedianews.mods.f1announcement

import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
@Table(name = "server_f1")
class F1ServerConfig(
		@Id
		@GeneratedValue(generator = "increment")
		@GenericGenerator(name = "increment", strategy = "increment")
		var id: Long,
		var serverId: String,
		var enabled: Boolean,
		var mentionRole: String?,
		var channel: String?) {

	constructor() : this(0, "", false, null, null)

	constructor(serverId: String) : this(0, serverId, false, null, null)
}