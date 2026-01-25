package com.andlife.nachoserver.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "thanks_cards")
class ThanksCard(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitation_id", nullable = false)
    val invitation: Invitation,

    @Column(name = "content_json", columnDefinition = "TEXT")
    var contentJson: String,

    @Column(name = "background_image_url")
    var backgroundImageUrl: String? = null
): BaseTimeEntity()