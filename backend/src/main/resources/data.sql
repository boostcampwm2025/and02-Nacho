INSERT INTO users (email, name, profile_image_url, created_at, updated_at)
VALUES ('donghyun@boostcamp.com', '동현', 'https://picsum.photos/200/200?random=10', NOW(), NOW());

INSERT INTO users (email, name, profile_image_url, created_at, updated_at)
VALUES ('ant@boostcamp.com', '미오', 'https://picsum.photos/200/200?random=11', NOW(), NOW());

INSERT INTO users (email, name, profile_image_url, created_at, updated_at)
VALUES ('dk0000@boostcamp.com', '동경', 'https://picsum.photos/200/200?random=12', NOW(), NOW());

INSERT INTO users (email, name, profile_image_url, created_at, updated_at)
VALUES ('jeongwoo@boostcamp.com', '정우', 'https://picsum.photos/200/200?random=14', NOW(), NOW());

INSERT INTO users (email, name, profile_image_url, created_at, updated_at)
VALUES ('jimin@boostcamp.com', '지민', 'https://picsum.photos/200/200?random=15', NOW(), NOW());


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
INSERT INTO guestbooks (invitation_id, user_id, text_content, created_at, updated_at)
VALUES (1, 4, '계절이 지나가는 하늘에는
가을로 가득 차 있습니다.
나는 아무 걱정도 없이
가을 속의 별들을 다 헤일 듯합니다.
가슴속에 하나둘 새겨지는 별을
이제 다 못 헤는 것은
쉬이 아침이 오는 까닭이요,
내일 밤이 남은 까닭이요,
아직 나의 청춘이 다하지 않은 까닭입니다.
별 하나에 추억과
별 하나에 사랑과
별 하나에 쓸쓸함과
별 하나에 동경과
별 하나에 시와
별 하나에 어머니, 어머니.', '2026-01-04 14:30:00', '2026-01-04 14:30:00');
INSERT INTO guestbooks (invitation_id, user_id, text_content, created_at, updated_at)
VALUES (1, 5, '2026년 송년회 정말 멋진 시간이었습니다!
함께한 모든 순간들이 소중한 추억으로 남을 것 같아요.
특히 다같이 게임하면서 웃었던 시간들,
맛있는 음식을 나누며 이야기 나눴던 순간들,
그리고 새해를 맞이하며 함께 카운트다운 했던 그 순간까지
모든 것이 완벽했습니다.
이렇게 좋은 사람들과 함께할 수 있어서 너무 행복했어요.
내년에도 꼭 다시 만나요!
모두 건강하시고 2026년도 행복한 일만 가득하길 바랍니다.
사진도 많이 찍었는데 공유할게요.
다들 정말 수고 많으셨고 감사합니다.
새해 복 많이 받으세요!', '2026-01-05 09:15:00', '2026-01-05 09:15:00');

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