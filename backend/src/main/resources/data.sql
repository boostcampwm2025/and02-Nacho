INSERT INTO users (email, name, profile_image_url, created_at, updated_at)
VALUES ('donghyun@boostcamp.com', '동현', 'https://picsum.photos/200/200?random=10', NOW(), NOW());

INSERT INTO users (email, name, profile_image_url, created_at, updated_at)
VALUES ('ant@boostcamp.com', '미오', 'https://picsum.photos/200/200?random=11', NOW(), NOW());

INSERT INTO users (email, name, profile_image_url, created_at, updated_at)
VALUES ('dk0000@boostcamp.com', '동경', 'https://picsum.photos/200/200?random=12', NOW(), NOW());

INSERT INTO invitations (
    host_id, title, display_host_name, thumbnail_urls,
    invitation_date, start_time, end_time,
    place_name, address, lat, lng, location_guide,
    created_at, updated_at
) VALUES (
    1,
    '2026 부스트캠프 송년 파티',
    '최동현',
    '["https://picsum.photos/800/400?random=20"]',
    '2026-12-31', '18:00:00', '23:00:00',
    '강남역 어느 카페', '서울특별시 강남구 테헤란로',
    37.4979, 127.0276,
    '강남역 10번 출구에서 직진하세요!',
    NOW(), NOW()
);

INSERT INTO guestbooks (invitation_id, user_id, text_content, created_at, updated_at)
VALUES (1, 1, '오늘 파티 너무 즐거웠어요! 사진 공유합니다.', '2026-01-01 18:00:00', '2026-01-01 18:00:00');

INSERT INTO guestbooks (invitation_id, user_id, text_content, created_at, updated_at)
VALUES (1, 2, '다들 새해 복 많이 받으세요!', '2026-01-02 10:00:00', '2026-01-02 10:00:00');

INSERT INTO guestbooks (invitation_id, user_id, text_content, created_at, updated_at)
VALUES (1, 3, '생생한 현장 영상입니다 ㅎㅎ', '2026-01-03 12:00:00', '2026-01-03 12:00:00');


INSERT INTO guestbook_images (guestbook_post_id, image_url, display_order)
VALUES (1, 'https://picsum.photos/400/600?random=1', 0);
INSERT INTO guestbook_images (guestbook_post_id, image_url, display_order)
VALUES (1, 'https://picsum.photos/400/600?random=2', 1);


INSERT INTO guestbook_audios (guestbook_post_id, audio_url, duration_seconds, display_order)
VALUES (2, 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3', 15, 0);


INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order)
VALUES (3, 'https://www.w3schools.com/html/mov_bbb.mp4', 'https://picsum.photos/400/600?random=3', 10, 0);


INSERT INTO video_preview_thumbnails (guestbook_video_id, thumbnail_url, time_seconds)
VALUES (1, 'https://picsum.photos/200/300?random=4', 2.5);
INSERT INTO video_preview_thumbnails (guestbook_video_id, thumbnail_url, time_seconds)
VALUES (1, 'https://picsum.photos/200/300?random=5', 5.0);