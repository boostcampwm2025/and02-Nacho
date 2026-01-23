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
(2, 6, '2026년 안드라이프 신년회', '안드라이프', 'https://picsum.photos/800/600?random=3', '2026-01-31', '13:30:00', NULL, '코드스쿼드', '서울특별시 강남구 강남대로62길 23, 4층 역삼빌딩', 37.4936874, 127.0302304, '양재역 3번 출구에서 801m', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- [PAST] 이미 지난 모임들
(3, 2, '미오의 자취방 집들이', '집주인 미오', 'https://picsum.photos/400/400?random=10,https://picsum.photos/400/400?random=11', '2026-01-25', '14:00:00', '20:00:00', '우리집', '서울 관악구 관악로 1', 37.478, 126.952, '서울대입구역 2번 출구로 나와서 5분만 걸어오세요!', '2025-11-01 10:00:00', NOW()),
(4, 3, '동경의 알고리즘 정복 스터디', '알고장인 동경', 'https://picsum.photos/400/400?random=12', '2026-02-05', '19:00:00', '22:00:00', '강남역 스터디룸', '서울 강남구 강남대로 396', 37.498, 127.028, '입구에서 닉네임을 말씀해주세요.', '2025-12-01 10:00:00', NOW()),
(5, 6, '안드라이프 25년 연말 회식', '안드라이프', 'https://picsum.photos/400/400?random=13,https://picsum.photos/400/400?random=14', '2026-01-28', '18:30:00', '23:59:00', '교대역 맛집', '서울 서초구 서초대로 314', 37.493, 127.014, '예약석 명단 확인 부탁드립니다.', '2025-12-10 10:00:00', NOW()),
-- [UPCOMING] 곧 다가올 모임들
(6, 4, '정우의 졸업 축하 파티', '안드로이드 마스터 정우', 'https://picsum.photos/400/400?random=15', '2026-02-10', '13:00:00', '16:00:00', '홍대 파티룸', '서울 마포구 홍익로 10', 37.556, 126.923, '9번 출구에서 가깝습니다!', NOW(), NOW()),
(7, 5, '지민과 함께하는 클라이밍', '지민', 'https://picsum.photos/400/400?random=16', '2026-02-07', '10:00:00', '13:00:00', '클라이밍파크', '서울 강남구 논현로 559', 37.506, 127.036, '편한 복장으로 오세요.', NOW(), NOW()),
(8, 2, '미오의 화이트데이 번개', '미오안드', 'https://picsum.photos/400/400?random=17', '2026-02-14', '19:00:00', NULL, '연남동 와인바', '서울 마포구 동교로 212', 37.561, 126.924, '연남동 골목 안쪽입니다.', NOW(), NOW()),
(9, 6, '안드라이프 벚꽃 출사', '안드라이프', 'https://picsum.photos/400/400?random=18', '2026-04-01', '14:00:00', '17:00:00', '여의도 한강공원', '서울 영등포구 여의동로 330', 37.528, 126.934, '벚꽃 나무 아래 돗자리 펴고 있을게요!', NOW(), NOW()),
(10, 1, '동현의 테니스 한 판', '최동현', 'https://picsum.photos/400/400?random=19', '2026-05-15', '08:00:00', '10:00:00', '올림픽공원 테니스장', '서울 송파구 올림픽로 424', 37.520, 127.120, '코트 번호는 추후 공지합니다.', NOW(), NOW());

-- 3. 방명록 게시글
INSERT INTO guestbooks (id, invitation_id, user_id, text_content, created_at, updated_at) VALUES
(1, 1, 1, '오늘 파티 너무 즐거웠어요! 사진 공유합니다.', '2026-01-01 18:00:00', '2026-01-01 18:00:00'),
(2, 1, 2, '다들 새해 복 많이 받으세요!', '2026-01-02 10:00:00', '2026-01-02 10:00:00'),
(3, 1, 3, '생생한 현장 영상입니다 ㅎㅎ', '2026-01-03 12:00:00', '2026-01-03 12:00:00'),
(4, 1, 4, '풍경이 너무 예쁘네요.', '2026-01-04 15:00:00', '2026-01-04 15:00:00'),
(5, 1, 5, '추억 저장 완료!', '2026-01-05 09:00:00', '2026-01-05 09:00:00'),
(6, 4, 1, '지민아 생일 축하해! 선물 마음에 들었으면 좋겠다.', NOW(), NOW()),
(7, 4, 3, '파티룸 분위기 너무 좋네요~ 생일 축하합니다!', NOW(), NOW()),
(8, 5, 3, '오랜만에 다들 얼굴 보니까 너무 반가웠어요!', NOW(), NOW()),
(9, 6, 4, '정상 도착 인증샷! 다들 고생하셨습니다.', NOW(), NOW()),
(10, 6, 5, '내려와서 먹은 파전에 막걸리 대박이었죠.', NOW(), NOW());

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

-- 8. 공지사항
INSERT INTO announcement_sections (invitation_id, title, content, display_order, created_at) VALUES
(1, '주차 안내', '건물 지하 주차장을 이용해 주세요. 3시간 무료 주차가 가능합니다.', 1, CURRENT_TIMESTAMP),
(1, '준비물', '만원 이하의 소소한 선물을 준비해 주세요. 랜덤 선물 교환식이 있습니다!', 2, CURRENT_TIMESTAMP),
(2, '준비물', ' - 건강한 마음 \n - 건강한 정신', 1, CURRENT_TIMESTAMP),
(2, '이벤트 안내', ' - 소정의 행사가 있습니다. \n - 입구에서 참여해보세요~', 2, CURRENT_TIMESTAMP),
(4, '드레스 코드', '드레스 코드는 화이트입니다! 예쁜 사진 찍어요.', 1, NOW()),
(4, '케이크 이벤트', '8시 정각에 케이크 촛불 이벤트가 있습니다.', 2, NOW()),
(6, '준비물 안내', '개인 물, 간식, 그리고 등산화는 필수입니다!', 1, NOW()),
(6, '하산 후 일정', '하산 후에 근처 유명한 맛집 예약해 두었습니다.', 2, NOW());

-- 9. 초대장 참여자 데이터 (invitation_participants)
INSERT INTO invitation_participants (invitation_id, user_id, joined_at) VALUES
(1, 1, '2026-01-10 10:00:00'),
(2, 1, '2026-01-12 15:00:00'),
(3, 1, '2025-11-02 09:00:00'),
(4, 1, '2025-12-02 13:00:00'),
(5, 1, '2025-12-11 18:00:00'),
(6, 1, '2026-01-16 10:00:00'),
(7, 2, '2026-01-17 11:30:00'),
(8, 2, '2026-01-19 09:00:00'),
(9, 2, '2026-01-21 10:00:00'),
(10, 2, now());