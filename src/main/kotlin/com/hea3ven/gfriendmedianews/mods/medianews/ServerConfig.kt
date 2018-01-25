package com.hea3ven.gfriendmedianews.mods.medianews

import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
@Table(name = "server")
class ServerConfig(
		@Id
		@GeneratedValue(generator = "increment")
		@GenericGenerator(name = "increment", strategy = "increment")
		var id: Long,
		var serverId: String,
		@OneToMany(mappedBy = "server", fetch = FetchType.EAGER, cascade = arrayOf(CascadeType.ALL))
		var sourceConfigs: MutableList<SourceConfig>) {

	constructor() : this(0, "", mutableListOf())

	constructor(serverId: String) : this(0, serverId, mutableListOf())
}