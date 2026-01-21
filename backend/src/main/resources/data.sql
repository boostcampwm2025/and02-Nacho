-- 1. 유저 데이터
INSERT INTO users (id, email, name, profile_image_url, created_at, updated_at) VALUES
(1, 'donghyun@boostcamp.com', '동현', 'https://picsum.photos/200/200?random=10', NOW(), NOW()),
(2, 'ant@boostcamp.com', '미오', 'https://picsum.photos/200/200?random=11', NOW(), NOW()),
(3, 'dk0000@boostcamp.com', '동경', 'https://picsum.photos/200/200?random=12', NOW(), NOW()),
(4, 'jeongwoo@boostcamp.com', '정우', 'https://picsum.photos/200/200?random=14', NOW(), NOW()),
(5, 'jimin@boostcamp.com', '지민', 'https://picsum.photos/200/200?random=15', NOW(), NOW()),
(6, 'andlife@example.com', '안드라이프', 'https://picsum.photos/200', NOW(), NOW());

-- 2. 초대장 데이터
INSERT INTO invitations (id, host_id, title, display_host_name, thumbnail_urls, invitation_date, start_time, end_time, place_name, address, lat, lng, location_guide, created_at, updated_at) VALUES
(1, 1, '2026 부스트캠프 송년 파티', '최동현', 'https://picsum.photos/800/400?random=1,https://picsum.photos/800/400?random=2', '2026-12-31', '18:00:00', '23:00:00', '강남역 어느 카페', '서울특별시 강남구 테헤란로', 37.4979, 127.0276, '강남역 10번 출구에서 직진하세요!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1, '2026 부스트캠프 파티', '미오', 'https://picsum.photos/800/400?random=1,https://picsum.photos/800/400?random=2', '2026-01-31', '18:30:00', '23:00:00', '강남역 어느 카페', '서울특별시 강남구 테헤란로', 37.4979, 127.0276, '강남역 10번 출구에서 직진하세요!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 6, '2026년 안드라이프 신년회', '안드라이프', 'https://picsum.photos/800/600?random=3', '2026-01-29', '13:30:00', NULL, '코드스쿼드', '서울특별시 강남구 강남대로62길 23, 4층 역삼빌딩', 37.4936874, 127.0302304, '양재역 3번 출구에서 801m', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. 방명록 게시글
INSERT INTO guestbooks (id, invitation_id, user_id, text_content, created_at, updated_at) VALUES
(1, 1, 1, '오늘 파티 너무 즐거웠어요! 사진 공유합니다.', '2026-01-01 18:00:00', '2026-01-01 18:00:00'),
(2, 1, 2, '다들 새해 복 많이 받으세요!', '2026-01-02 10:00:00', '2026-01-02 10:00:00'),
(3, 1, 3, '생생한 현장 영상입니다 ㅎㅎ', '2026-01-03 12:00:00', '2026-01-03 12:00:00'),
(4, 1, 4, '풍경이 너무 예쁘네요.', '2026-01-04 15:00:00', '2026-01-04 15:00:00'),
(5, 1, 5, '추억 저장 완료!', '2026-01-05 09:00:00', '2026-01-05 09:00:00');

-- 4. 방명록 이미지 (게시글 ID 1, 4, 5번 참조)
INSERT INTO guestbook_images (guestbook_post_id, image_url, display_order) VALUES
(1, 'https://picsum.photos/400/600?random=1', 0),
(1, 'https://picsum.photos/400/600?random=2', 1),
(4, 'https://picsum.photos/400/600?random=6', 0),
(5, 'https://picsum.photos/400/600?random=7', 0),
(5, 'https://picsum.photos/400/600?random=8', 1),
(5, 'https://picsum.photos/400/600?random=9', 2);

-- 5. 방명록 오디오 (게시글 ID 2번 참조)
INSERT INTO guestbook_audios (guestbook_post_id, audio_url, duration_seconds, display_order) VALUES
(2, 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3', 15, 0);

-- 6. 방명록 비디오 (게시글 ID 3번 참조)
INSERT INTO guestbook_videos (id, guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order) VALUES
(1, 3, 'https://www.w3schools.com/html/mov_bbb.mp4', 'https://picsum.photos/400/600?random=3', 10, 0);

-- 7. 비디오 썸네일 (비디오 ID 1번 참조)
INSERT INTO video_preview_thumbnails (guestbook_video_id, thumbnail_url, time_seconds) VALUES
(1, 'https://picsum.photos/200/300?random=4', 2.5),
(1, 'https://picsum.photos/200/300?random=5', 5.0);

-- 8. 초대장 카드 및 공지사항
INSERT INTO announcement_sections (invitation_id, title, content, display_order, created_at) VALUES
(1, '주차 안내', '건물 지하 주차장을 이용해 주세요. 3시간 무료 주차가 가능합니다.', 1, CURRENT_TIMESTAMP),
(1, '준비물', '만원 이하의 소소한 선물을 준비해 주세요. 랜덤 선물 교환식이 있습니다!', 2, CURRENT_TIMESTAMP),
(2, '준비물', ' - 건강한 마음 \n - 건강한 정신', 1, CURRENT_TIMESTAMP),
(2, '이벤트 안내', ' - 소정의 행사가 있습니다. \n - 입구에서 참여해보세요~', 2, CURRENT_TIMESTAMP);

-- 9. 초대장 참여자 데이터 (invitation_participants)
INSERT INTO invitation_participants (invitation_id, user_id, joined_at) VALUES
-- '2026 부스트캠프 송년 파티' (1번 초대장) 참여자들
(1, 2, NOW()), -- 미오 참여
(1, 3, NOW()), -- 동경 참여
(1, 4, NOW()), -- 정우 참여
(1, 5, NOW()), -- 지민 참여
(1, 6, NOW()), -- 안드라이프 참여
-- '2026 부스트캠프 파티' (2번 초대장) 참여자들
(2, 3, NOW()), -- 동경 참여
(2, 4, NOW()), -- 정우 참여
-- '2026년 안드라이프 신년회' (3번 초대장) 참여자들
(3, 1, NOW()), -- 동현 참여
(3, 2, NOW()), -- 미오 참여
(3, 5, NOW()); -- 지민 참여