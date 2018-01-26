package com.hea3ven.gfriendmedianews.mods.socialinteraction

import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "social_interaction_stat")
class SocialInteractionStat(
		@Id
		@GeneratedValue(generator = "increment")
		@GenericGenerator(name = "increment", strategy = "increment")
		var id: Long,
		@Enumerated(EnumType.STRING)
		var type: InteractionType,
		var sourceId: String,
		var targetId: String,
		var date: Date) {

	constructor() : this(0, InteractionType.SLAP, "", "", Date())

	constructor(type: InteractionType, sourceId: String, targetId: String) : this(0, type, sourceId, targetId, Date())
}

enum class InteractionType(val verb: kotlin.String) {
	SLAP("slapped"), HUG("hugged");
}