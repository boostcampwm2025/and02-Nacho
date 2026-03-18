package com.andlife.nachoserver.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "fcm_tokens")
class FcmToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @Column(name = "fcm_token", nullable = false, unique = true)
    val fcmToken: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    val user: User? = null,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null,

    @Column(name = "last_pushed_at", nullable = true)
    var lastPushedAt: LocalDateTime? = null
)
 : BaseCreatedEntity()