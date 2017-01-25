package com.hea3ven.gfriendmedianews.domain

import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
@Table(name = "server_news_source")
class SourceConfig(
		@Id
		@GeneratedValue(generator = "increment")
		@GenericGenerator(name = "increment", strategy = "increment")
		var id: Long,
		@ManyToOne()
		@JoinColumn(name = "server_config_id")
		var server: ServerConfig,
		var type: String,
		@Column(name = "connection_data")
		var connectionData: String,
		@Column(name = "state_data")
		var stateData: String,
		@Column(name = "channel")
		var channel: String) {
	constructor() : this(0, ServerConfig(), "", "", "", "")

	constructor(server: ServerConfig, sourceType: String, sourceData: String, channel: String) :
			this(0, server, sourceType, sourceData, "", channel)
}