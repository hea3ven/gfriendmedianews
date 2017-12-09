package com.hea3ven.gfriendmedianews.domain

import org.hibernate.annotations.GenericGenerator
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "slap_stat")
class SlapStat(
		@Id
		@GeneratedValue(generator = "increment")
		@GenericGenerator(name = "increment", strategy = "increment")
		var id: Long,
		var slapperId: String,
		var slappeeId: String,
		var count: Int) {

	constructor() : this(0, "", "", 0)

	constructor(slapper: String, slappee: String) : this(0, slapper, slappee, 0)
}