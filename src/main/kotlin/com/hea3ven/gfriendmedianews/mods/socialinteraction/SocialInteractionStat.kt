package com.hea3ven.gfriendmedianews.mods.socialinteraction

import org.hibernate.annotations.GenericGenerator
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "social_interaction_stat")
class SocialInteractionStat(
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