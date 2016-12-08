package com.hea3ven.gfriendmedianews.domain

import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
@Table(name = "server")
@NamedQueries(
		NamedQuery(name = ServerConfig.FIND_BY_SERVER_ID, query = "SELECT s FROM ServerConfig s")
)
class ServerConfig(
		@Id
		@GeneratedValue(generator = "increment")
		@GenericGenerator(name = "increment", strategy = "increment")
		var id: Long,
		var serverId: String,
		var targetChannel: String?,
		@OneToMany(mappedBy = "server", fetch = FetchType.EAGER, cascade = arrayOf(CascadeType.ALL))
		var sourceConfigs: MutableList<SourceConfig>) {

	constructor() : this(0, "", null, mutableListOf()) {
	}

	constructor(serverId: String) : this(0, serverId, null, mutableListOf()) {
	}

	companion object {
		const val FIND_BY_SERVER_ID = "ServerConfig.findByServerId"
	}
}