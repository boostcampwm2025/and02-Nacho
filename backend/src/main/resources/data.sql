INSERT INTO users (id, email, name, profile_image_url, created_at, updated_at) VALUES
(1, 'donghyun@boostcamp.com', '동현', 'https://picsum.photos/200/200?random=10', NOW(), NOW()),
(2, 'ant@boostcamp.com', '미오', 'https://picsum.photos/200/200?random=11', NOW(), NOW()),
(3, 'dk0000@boostcamp.com', '동경', 'https://picsum.photos/200/200?random=12', NOW(), NOW()),
(4, 'jeongwoo@boostcamp.com', '정우', 'https://picsum.photos/200/200?random=14', NOW(), NOW()),
(5, 'jimin@boostcamp.com', '지민', 'https://picsum.photos/200/200?random=15', NOW(), NOW()),
(6, 'andlife@example.com', '안드라이프', 'https://picsum.photos/200', NOW(), NOW());
INSERT INTO `invitations` (
    `id`, `host_id`, `title`, `display_host_name`, `thumbnail_urls`,
    `invitation_date`, `start_time`, `end_time`,
    `place_name`,
    `address`, `lat`, `lng`, `location_guide`,
    `created_at`, `updated_at`
) VALUES
(
    1, 1, '2026 부스트캠프 송년 파티', '최동현',
    'https://picsum.photos/800/400?random=1,https://picsum.photos/800/400?random=2',
    '2026-12-31', '18:00:00', '23:00:00',
    '강남역 어느 카페', '서울특별시 강남구 테헤란로',
    37.4979, 127.0276, '강남역 10번 출구에서 직진하세요!',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    2, 6, '2026년 안드라이프 신년회', '안드라이프',
    'https://picsum.photos/800/600?random=3',
    '2026-01-31', '13:30:00', NULL,
    '코드스쿼드', '서울특별시 강남구 강남대로62길 23, 4층 역삼빌딩',
    37.4936874, 127.0302304, '양재역 3번 출구에서 801m',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

INSERT INTO `invitation_cards` (`invitation_id`, `content_json`, `background_image_url`)
VALUES
(1, '{"text": "부스트캠프 멤버들, 한 해 동안 고생 많으셨습니다!"}', NULL),
(2, '{"message": "안드라이프 한해 잘 보내봅시다~"}', NULL);

INSERT INTO `announcement_sections` (`invitation_id`, `title`, `content`, `display_order`, `created_at`)
VALUES
(1, '주차 안내', '건물 지하 주차장을 이용해 주세요. 3시간 무료 주차가 가능합니다.', 1, CURRENT_TIMESTAMP),
(1, '준비물', '만원 이하의 소소한 선물을 준비해 주세요. 랜덤 선물 교환식이 있습니다!', 2, CURRENT_TIMESTAMP),
(2, '준비물', ' - 건강한 마음 \n - 건강한 정신', 1, CURRENT_TIMESTAMP),
(2, '이벤트 안내', ' - 소정의 행사가 있습니다. \n - 입구에서 참여해보세요~', 2, CURRENT_TIMESTAMP);

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
INSERT INTO guestbook_images (guestbook_post_id, image_url, display_order)
VALUES (4, 'https://picsum.photos/400/600?random=6', 0);
INSERT INTO guestbook_images (guestbook_post_id, image_url, display_order)
VALUES (5, 'https://picsum.photos/400/600?random=7', 0);
INSERT INTO guestbook_images (guestbook_post_id, image_url, display_order)
VALUES (5, 'https://picsum.photos/400/600?random=8', 1);
INSERT INTO guestbook_images (guestbook_post_id, image_url, display_order)
VALUES (5, 'https://picsum.photos/400/600?random=9', 2);

INSERT INTO guestbook_audios (guestbook_post_id, audio_url, duration_seconds, display_order)
VALUES (2, 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3', 15, 0);


INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order)
VALUES (3, 'https://www.w3schools.com/html/mov_bbb.mp4', 'https://picsum.photos/400/600?random=3', 10, 0);


INSERT INTO video_preview_thumbnails (guestbook_video_id, thumbnail_url, time_seconds)
VALUES (1, 'https://picsum.photos/200/300?random=4', 2.5);
INSERT INTO video_preview_thumbnails (guestbook_video_id, thumbnail_url, time_seconds)
VALUES (1, 'https://picsum.photos/200/300?random=5', 5.0);