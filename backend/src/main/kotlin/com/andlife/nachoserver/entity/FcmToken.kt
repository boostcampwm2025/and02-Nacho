package com.andlife.nachoserver.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "fcm_tokens")
class FcmToken(
    @Id
    @Column(name = "fcm_token", nullable = false, unique = true)
    val fcmToken: String,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    val user: User? = null
) : BaseCreatedEntity()