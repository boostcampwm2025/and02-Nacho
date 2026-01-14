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
(1, 1, 2, '세로 영상과 이미지가 섞인 피드입니다.', NOW(), NOW()),
(2, 1, 3, '가로 영상 3개가 연속으로 나옵니다.', NOW(), NOW()),
(3, 1, 1, '아무 미디어 없이 텍스트만 있는 방명록입니다.', NOW(), NOW()),
(4, 1, 2, '세로 영상 4개로 구성된 숏폼 스타일 피드.', NOW(), NOW()),
(5, 1, 3, '이미지 4개로 구성된 깔끔한 갤러리입니다.', NOW(), NOW()),
(6, 1, 1, '음성 메시지만 2개 포함되어 있습니다.', NOW(), NOW()),
(7, 1, 2, '이미지, 가로 영상, 음성이 모두 섞인 종합 선물 세트.', NOW(), NOW()),
(8, 1, 3, '축하드립니다! 대박나세요.', NOW(), NOW()),
(9, 1, 1, '세로 영상과 음성의 조합.', NOW(), NOW()),
(10, 1, 2, '가로 영상과 이미지가 교차로 등장합니다.', NOW(), NOW()),
(11, 1, 3, '이미지 2개와 음성 1개 조합.', NOW(), NOW()),
(12, 1, 1, '가로 영상 5개 대량 업로드 테스트.', NOW(), NOW()),
(13, 1, 2, '세로 영상과 이미지의 빠른 교체.', NOW(), NOW()),
(14, 1, 3, '다양한 이미지들의 향연.', NOW(), NOW()),
(15, 1, 1, '음성 3개가 연속으로 나오는 피드.', NOW(), NOW()),
(16, 1, 2, '가로 영상 위주의 구성.', NOW(), NOW()),
(17, 1, 3, '세로 영상 위주의 구성.', NOW(), NOW()),
(18, 1, 1, '이미지 1개와 가로 영상 1개.', NOW(), NOW()),
(19, 1, 2, '마지막에서 두 번째, 모든 매체 혼합.', NOW(), NOW()),
(20, 1, 3, '20번째 마지막 방명록 샘플입니다.', NOW(), NOW());

-- 4. 미디어 상세 데이터 삽입 (제공해주신 URL 매칭)
-- =========================================================
-- Guestbook Videos Seed (20 posts, no reused videos)
-- Zero-video posts: 5, 10, 15, 20
-- =========================================================

-- ========================
-- test01 (posts 1~10)
-- ========================

-- 1 (3개)
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order) VALUES
(1,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/08b80407-0ed5-4ba0-bf32-2d11432fc2d8.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png',10,0),
(1,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/0e9697ca-2fe8-4f46-96c5-fcc2228c59a1.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png',10,1),
(1,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/11cbb9c6-71e4-4e02-9f8b-0005e68c9af2.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png',10,2);


-- 2 (3개)
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order) VALUES
(2,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/2106c39b-c8fd-4743-9648-7b9337333e6f.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png',10,0),
(2,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/3472f283-6b6a-4fa4-b349-8c7effc8c672.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png',10,1),
(2,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/3d41811a-afc8-4597-be41-cf8fc72ddb34.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png',10,2),
(2,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/000-test-01.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000-test-01.jpeg',130,3),
(2,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/000-test-02.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000-test-01.jpeg',120,4),
(2,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/000-test-03.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000-test-01.jpeg',440,5);

-- 3 (2개)
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order) VALUES
(3,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/51997dee-9294-4928-ad7a-b47fc4f46dbf.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png',10,0),
(3,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/6e01402e-e5c6-4d26-92d6-55a6893ca5c6.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png',10,1);

-- 4 (2개)
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order) VALUES
(4,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/7376e27c-81af-49b6-b39d-cab40e0dbf0e.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png',10,0),
(4,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/75890f9f-2e36-4cf4-8999-19eb4786e54e.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png',10,1);

-- 5 (0개)

-- 6 (2개)
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order) VALUES
(6,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/76aadf88-4287-432d-9da3-44b765d16c2d.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png',10,0),
(6,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/7fff5624-5c72-483d-8e67-909b96ba4609.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png',10,1);

-- 7 (2개)
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order) VALUES
(7,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/9618021c-7b90-45fa-be5d-c73bb90af4f3.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png',10,0),
(7,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/98e99d7b-ab70-4445-872b-fae30091282c.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png',10,1);

-- 8 (2개)
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order) VALUES
(8,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/bf4155d0-20a2-4f19-965f-40a7d634c8be.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png',10,0),
(8,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/cffcfbe2-e14b-4fdd-b807-9973f9b8fe7a.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test01.png',10,1);

-- 9 (0개)
-- 10 (0개)

-- ========================
-- test02 (posts 11~20)
-- ========================

-- 11 (3개)
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order) VALUES
(11,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/24ecd1d1-d271-4359-9751-7b3a8176ac23.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png',10,0),
(11,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/6cfbc48b-1272-4d2b-bd68-370816a520a7.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png',10,1),
(11,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/bec2c2af-7cad-4705-ac34-83a58672bd23.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png',10,2);

-- 12 (3개)
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order) VALUES
(12,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/d017f428-cbad-446b-a627-68debb045dd8.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png',10,0),
(12,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/d127fe56-1b1a-41cd-825e-fc23356a7481.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png',10,1),
(12,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/dbeb1cd0-28df-4381-997d-4daecefbd97f.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png',10,2);

-- 13 (3개)
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order) VALUES
(13,'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/videos/fcf31769-b53c-4fc9-aa17-2d6efef8a04a.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png',10,0),
(13,'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png',60,1),
(13,'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png',50,2);

-- 14 (2개)
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order) VALUES
(14,'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png',45,0),
(14,'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png',30,1);

-- 15 (0개)

-- 16 (3개)
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order) VALUES
(16,'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png',30,0),
(16,'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png',10,1),
(16,'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png',10,2);

-- 17 (3개)
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order) VALUES
(17,'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png',10,0),
(17,'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png',10,1),
(17,'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png',10,2);

-- 18 (2개)
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order) VALUES
(18,'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png',10,0),
(18,'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png',10,1);

-- 19 (1개)
INSERT INTO guestbook_videos (guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order) VALUES
(19,'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WhatCarCanYouGetForAGrand.mp4','https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/images/000thumb_test02.png',10,0);

-- 20 (0개)