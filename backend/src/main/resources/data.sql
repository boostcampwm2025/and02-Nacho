-- 1. 유저 데이터 생성
INSERT INTO users (name, email, profile_image_url, created_at, updated_at) VALUES
('동현', 'donghyun@boostcamp.com', 'https://picsum.photos/id/1/200', NOW(), NOW()),
('AndLife', 'andlife@boostcamp.com', 'https://picsum.photos/id/2/200', NOW(), NOW()),
('동경', 'dk0000@boostcamp.com', 'https://picsum.photos/id/3/200', NOW(), NOW());

-- 2. 초대장 데이터 생성
INSERT INTO invitations (host_id, title, display_host_name, invitation_date, start_time, place_name, address, lat, lng, created_at, updated_at)
VALUES (1, 'AndLife 런칭 기념 파티', '동현', '2026-02-14', '18:00:00', '부스트 캠프 강남', '서울시 강남구', 37.4979, 127.0276, NOW(), NOW());

-- 3. 방명록 20개 생성 (invitation_id: 1)
INSERT INTO guestbooks (id, invitation_id, user_id, text_content, created_at, updated_at) VALUES
(1, 1, 2, '세로 영상과 이미지가 섞인 피드입니다.', '2026-01-01 18:00:00', '2026-01-01 18:00:00'),
(2, 1, 3, '가로 영상 3개가 연속으로 나옵니다.', '2026-01-01 18:05:00', '2026-01-01 18:05:00'),
(3, 1, 1, '아무 미디어 없이 텍스트만 있는 방명록입니다.', '2024-04-01 12:00:00', '2024-04-01 12:00:00'),
(4, 1, 2, '세로 영상 4개로 구성된 숏폼 스타일 피드.', '2023-12-25 09:30:00', '2023-12-25 09:30:00'),
(5, 1, 3, '이미지 4개로 구성된 깔끔한 갤러리입니다.', '2025-07-04 15:45:00', '2025-07-04 15:45:00'),
(6, 1, 1, '음성 메시지만 2개 포함되어 있습니다.', '2026-01-10 20:20:00', '2026-01-10 20:20:00'),
(7, 1, 2, '이미지, 가로 영상, 음성이 모두 섞인 종합 선물 세트.', '2024-11-11 11:11:00', '2024-11-11 11:11:00'),
(8, 1, 3, '축하드립니다! 대박나세요.', '2024-05-05 14:00:00', '2024-05-05 14:00:00'),
(9, 1, 1, '세로 영상과 음성의 조합.', '2025-10-31 18:30:00', '2025-10-31 18:30:00'),
(10, 1, 2, '가로 영상과 이미지가 교차로 등장합니다.', '2024-08-15 10:10:00', '2024-08-15 10:10:00'),
(11, 1, 3, '이미지 2개와 음성 1개 조합.', '2025-12-24 19:00:00', '2025-12-24 19:00:00'),
(12, 1, 1, '가로 영상 5개 대량 업로드 테스트.', '2024-09-09 09:09:00', '2024-09-09 09:09:00'),
(13, 1, 2, '세로 영상과 이미지의 빠른 교체.', '2025-06-01 13:30:00', '2025-06-01 13:30:00'),
(14, 1, 3, '다양한 이미지들의 향연.', '2024-03-03 16:16:00', '2024-03-03 16:16:00'),
(15, 1, 1, '음성 3개가 연속으로 나오는 피드.', '2025-02-02 22:22:00', '2025-02-02 22:22:00'),
(16, 1, 2, '가로 영상 위주의 구성.', '2024-12-12 12:12:00', '2024-12-12 12:12:00'),
(17, 1, 3, '세로 영상 위주의 구성.', '2025-01-01 01:01:00', '2025-01-01 01:01:00'),
(18, 1, 1, '이미지 1개와 가로 영상 1개.', '2024-10-10 10:10:00', '2024-10-10 10:10:00'),
(19, 1, 2, '마지막에서 두 번째, 모든 매체 혼합.', '2025-11-11 11:11:00', '2025-11-11 11:11:00'),
(20, 1, 3, '20번째 마지막 방명록 샘플입니다.', '2024-06-06 06:06:00', '2024-06-06 06:06:00');

-- 4. 미디어 상세 데이터 삽입 (제공해주신 URL 매칭)

-- [1번] 세로 영상 1, 이미지 1, 세로 영상 1
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order)
VALUES (1, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/08b80407-0ed5-4ba0-bf32-2d11432fc2d8.mp4', 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png', 15, 0);
INSERT INTO guestbook_images (guestbook_post_id, image_url, display_order)
VALUES (1, 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg', 1);
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order)
VALUES (1, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/0e9697ca-2fe8-4f46-96c5-fcc2228c59a1.mp4', 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png', 12, 2);

-- [2번] 가로 영상 3개
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order) VALUES
(2, 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4', 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png', 60, 0),
(2, 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4', 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png', 50, 1),
(2, 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4', 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png', 45, 2);

-- [4번] 세로 영상 4개
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order) VALUES
(4, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/11cbb9c6-71e4-4e02-9f8b-0005e68c9af2.mp4', 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png', 10, 0),
(4, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/2106c39b-c8fd-4743-9648-7b9337333e6f.mp4', 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png', 10, 1),
(4, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/3472f283-6b6a-4fa4-b349-8c7effc8c672.mp4', 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png', 10, 2),
(4, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/3d41811a-afc8-4597-be41-cf8fc72ddb34.mp4', 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png', 10, 3);

-- [5번] 이미지 4개
INSERT INTO guestbook_images (guestbook_post_id, image_url, display_order) VALUES
(5, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/091bd2e7-424b-41c0-8b0b-9bbc833b6ea7.jpg', 0),
(5, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/0e133f1a-1442-45c7-994a-19c72d061cb2.jpg', 1),
(5, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/11eeefc1-c820-4deb-89fd-325b920f70d9.jpg', 2),
(5, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/125850ad-64af-4305-b9a8-f97763197199.jpg', 3);

-- [6번] 음성 전용 2개
INSERT INTO guestbook_audios (guestbook_post_id, audio_url, duration_seconds, display_order) VALUES
(6, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/audios/000test01.mp3', 45, 0),
(6, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/audios/000test02.mp3', 30, 1);

-- [7번] 이미지 + 가로 영상 + 음성 혼합
INSERT INTO guestbook_images (guestbook_post_id, image_url, display_order) VALUES (7, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/13d03d07-75d4-4c45-ba4d-d1308b78d8a4.jpg', 0);
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order)
VALUES (7, 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4', 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png', 120, 1);
INSERT INTO guestbook_audios (guestbook_post_id, audio_url, duration_seconds, display_order) VALUES (7, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/audios/000test03.mp3', 15, 2);

-- [9번] 세로 영상 + 음성
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order)
VALUES (9, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/51997dee-9294-4928-ad7a-b47fc4f46dbf.mp4', 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png', 8, 0);
INSERT INTO guestbook_audios (guestbook_post_id, audio_url, duration_seconds, display_order) VALUES (9, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/audios/000test04.mp3', 20, 1);

-- [10번] 가로 영상 + 이미지 교차
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order)
VALUES (10, 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4', 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png', 30, 0);
INSERT INTO guestbook_images (guestbook_post_id, image_url, display_order) VALUES (10, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/19ccbac5-14ad-4d6e-b679-0ae4b44d7305.jpg', 1);

-- [12번] 가로 영상 대량 (5개)
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order) VALUES
(12, 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4', 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png', 10, 0),
(12, 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4', 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png', 10, 1),
(12, 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4', 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png', 10, 2),
(12, 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4', 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png', 10, 3),
(12, 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4', 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png', 10, 4);

-- [13번] 세로 영상 + 이미지
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order)
VALUES (13, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/6e01402e-e5c6-4d26-92d6-55a6893ca5c6.mp4', 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png', 15, 0);
INSERT INTO guestbook_images (guestbook_post_id, image_url, display_order) VALUES (13, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/1d0bc0a4-200c-4e27-9160-2f0dcda56ac9.jpg', 1);

-- [15번] 음성 3개
INSERT INTO guestbook_audios (guestbook_post_id, audio_url, duration_seconds, display_order) VALUES
(15, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/audios/000test05.mp3', 30, 0),
(15, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/audios/000test06.mp3', 30, 1),
(15, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/audios/000test07.mp3', 30, 2);

-- [19번] 올인원 혼합 (세로영상 + 가로영상 + 이미지 + 음성)
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order)
VALUES (19, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/7376e27c-81af-49b6-b39d-cab40e0dbf0e.mp4', 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png', 10, 0);
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order)
VALUES (19, 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4', 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png', 40, 1);
INSERT INTO guestbook_images (guestbook_post_id, image_url, display_order) VALUES (19, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/2091bc48-f506-430b-8533-48da2ea33fff.jpg', 2);
INSERT INTO guestbook_audios (guestbook_post_id, audio_url, duration_seconds, display_order) VALUES (19, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/audios/000test08.mp3', 5, 3);

-- [20번] 가로 영상 1개 피날레
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order)
VALUES (20, 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4', 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png', 25, 0);


INSERT INTO invitation_participants (invitation_id, user_id, joined_at) VALUES
(1, 1, '2026-12-01 10:30:00');