package com.andlife.nachoserver.entity

import com.andlife.nachoserver.util.StringListConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalTime

@Entity
@Table(name = "invitations")
class Invitation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    val host: User,

    @Column(nullable = false)
    var title: String,

    @Column(name = "display_host_name", nullable = false)
    var displayHostName: String,

    @Convert(converter = StringListConverter::class)
    @Column(name = "thumbnail_urls", columnDefinition = "TEXT")
    var thumbnailUrls: List<String> = emptyList(),

    @Column(name = "invitation_date", nullable = false)
    var invitationDate: LocalDate,

    @Column(name = "start_time", nullable = false)
    var startTime: LocalTime,

    @Column(name = "end_time")
    var endTime: LocalTime? = null,

    @Column(name = "place_name", nullable = false)
    var placeName: String,

    @Column(nullable = false)
    var address: String,

    @Column(nullable = false)
    var lat: Double,

    @Column(nullable = false)
    var lng: Double,

    @Column(name = "location_guide")
    var locationGuide: String? = null
) : BaseTimeEntity()