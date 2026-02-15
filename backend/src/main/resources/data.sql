
-- 1. 유저 데이터 (ID를 1씩 증가: 1-10 → 2-11)
-- 샘플 유저 추가 (ID = 1)
INSERT IGNORE INTO users (id, kakao_id, email, name, profile_image_url, created_at, updated_at) VALUES
(1, 0, 'sample@example.com', '나에게로의 초대 팀', 'https://picsum.photos/200/200?random=0', NOW(), NOW());

-- 2. 초대장 데이터 (ID를 1씩 증가: 0-30 → 1-31)
-- 기존 ID 0 (샘플)을 ID 1로 변경
INSERT IGNORE INTO invitations (id, host_id, title, display_host_name, thumbnail_urls, invitation_date, start_time, end_time, place_name, address, lat, lng, location_guide, created_at, updated_at) VALUES
(1, 1, '우리 앱에 오신 것을 환영합니다!', '나에게로의 초대 팀', null, '2030-02-06', '14:00:00', '15:00:00', '나에게로의 초대', '샘플 주소', 0.0, 0.0, '이 초대장은 샘플입니다.\n실제 초대를 만들면 위치와 길 안내를 자유롭게 입력할 수 있어요.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. 방명록 게시글 (invitation_id와 user_id를 1씩 증가)
INSERT IGNORE INTO guestbooks (id, invitation_id, user_id, text_content, created_at, updated_at) VALUES
(51, 1, 1, '샘플 방명록 글입니다.', NOW(), NOW()),
(52, 1, 1, '샘플 유저가 남긴 두번째 글입니다.', NOW(), NOW()),
(53, 1, 1, '샘플 유저의 세번째 방명록 글입니다.', NOW(), NOW());

-- 4. 방명록 미디어 데이터
INSERT IGNORE INTO guestbook_images (guestbook_post_id, image_url, display_order) VALUES
(51, 'https://picsum.photos/400/600?random=52', 1),
(52, 'https://picsum.photos/400/600?random=53', 1),
(53, 'https://picsum.photos/400/600?random=54', 0);

INSERT IGNORE INTO guestbook_audios (guestbook_post_id, audio_url, duration_seconds, display_order) VALUES
(53, 'https://pub-a78ca76ec21b4920a70cb63c4a63236a.r2.dev/audios/000test05.mp3', 20, 1);

INSERT IGNORE INTO guestbook_videos (id, guestbook_post_id, video_url, thumbnail_url, duration_seconds, display_order) VALUES
(4, 51, 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4', 'https://picsum.photos/400/600?random=64', 25, 0),
(5, 52, 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4', 'https://picsum.photos/400/600?random=65', 30, 0);


INSERT IGNORE INTO video_preview_thumbnails (guestbook_video_id, thumbnail_url, time_seconds) VALUES
(1, 'https://picsum.photos/200/300?random=4', 2.5),
(1, 'https://picsum.photos/200/300?random=5', 5.0),
(2, 'https://picsum.photos/200/300?random=62', 5.0),
(3, 'https://picsum.photos/200/300?random=63', 7.0),
(4, 'https://picsum.photos/200/300?random=66', 10.0),
(5, 'https://picsum.photos/200/300?random=67', 15.0);

-- 5. 공지사항 데이터 (invitation_id를 1씩 증가)
INSERT IGNORE INTO announcement_sections (invitation_id, title, content, display_order, created_at) VALUES
(1, '📌 안내사항', ' - 이 초대장은 샘플 초대장입니다.\n - 실제 초대를 만들면 이 내용을 자유롭게 수정할 수 있어요.', 1, CURRENT_TIMESTAMP),
(1, '📸 방명록 작성 안내', ' - 사진, 영상, 음성으로 마음을 남길 수 있어요.\n - 참여한 사람들의 기록은 하나의 타임라인으로 정리됩니다.', 2, CURRENT_TIMESTAMP),
(1, '💌 초대장 꾸미기', ' - 초대 카드와 감사카드를 활용하여 사람들과 특별한 추억을 만들어보세요!\n - 다양한 테마와 함께 순간을 위한 초대장을 꾸며보세요.', 3, CURRENT_TIMESTAMP),
(1, '🎁 이렇게 활용해보세요',' - 결혼식, 돌잔치, 졸업식 같은 큰 행사부터\n - 생일, 집들이, 송년회 같은 일상의 모임\n - 동호회, 스터디, 회사 행사\n - 번개 약속, 여행 기록, 소소한 만남까지\n - 감사 인사와 추억 아카이빙',4, CURRENT_TIMESTAMP),
(1, '❓ 문의사항', ' - 궁금한 점이 있으면 언제든지 고객센터로 연락주세요!\n - 이메일:nacho@example.com', 5, CURRENT_TIMESTAMP);
