package com.andlife.nachoserver.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.BatchSize

@Entity
@Table(name = "guestbooks")
class GuestBook(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitation_id", nullable = false)
    val invitation: Invitation,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "text_content", columnDefinition = "TEXT", nullable = false)
    var textContent: String,

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "guestBook", cascade = [CascadeType.ALL], orphanRemoval = true)
    val images: MutableList<GuestBookImage> = mutableListOf(),

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "guestBook", cascade = [CascadeType.ALL], orphanRemoval = true)
    val audios: MutableList<GuestBookAudio> = mutableListOf(),

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "guestBook", cascade = [CascadeType.ALL], orphanRemoval = true)
    val videos: MutableList<GuestBookVideo> = mutableListOf()
) : BaseTimeEntity()

@Entity
@Table(name = "guestbook_images")
class GuestBookImage(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guestbook_post_id", nullable = false)
    val guestBook: GuestBook,

    @Column(name = "image_url", nullable = false)
    val imageUrl: String,

    @Column(name = "display_order", nullable = false)
    val displayOrder: Int = 0
)

@Entity
@Table(name = "guestbook_audios")
class GuestBookAudio(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guestbook_post_id", nullable = false)
    val guestBook: GuestBook,

    @Column(name = "audio_url", nullable = false)
    val audioUrl: String,

    @Column(name = "duration_seconds", nullable = false)
    val durationSeconds: Int,

    @Column(name = "display_order", nullable = false)
    val displayOrder: Int = 0
)

@Entity
@Table(name = "guestbook_videos")
class GuestBookVideo(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guestbook_post_id", nullable = false)
    val guestBook: GuestBook,

    @Column(name = "video_url", nullable = false)
    val videoUrl: String,

    @Column(name = "thumbnail_url", nullable = false)
    val thumbnailUrl: String,


    @Column(name = "duration_seconds", nullable = false)
    val durationSeconds: Int,

    @Column(name = "display_order", nullable = false)
    val displayOrder: Int = 0,

    @OneToMany(mappedBy = "video", cascade = [CascadeType.ALL])
    val previewThumbnails: MutableList<VideoPreviewThumbnail> = mutableListOf()
)


@Entity
@Table(name = "video_preview_thumbnails")
class VideoPreviewThumbnail(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guestbook_video_id", nullable = false)
    val video: GuestBookVideo,

    @Column(name = "thumbnail_url", nullable = false)
    val thumbnailUrl: String,

    @Column(name = "time_seconds", nullable = false)
    val timeSeconds: Double
)