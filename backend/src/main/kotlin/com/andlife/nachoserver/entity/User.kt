package com.andlife.nachoserver.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Table
import jakarta.persistence.Id

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "kakao_id", nullable = false, unique = true)
    val kakaoId: Long,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    var name: String,

    @Column(name = "profile_image_url")
    var profileImageUrl: String? = null,
): BaseTimeEntity() {
    fun updateNickname(newName: String) {
        this.name
    }

    fun updateProfileImage(newUrl: String) {
        this.profileImageUrl = newUrl
    }
}