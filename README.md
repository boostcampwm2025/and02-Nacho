

<img src="https://github.com/user-attachments/assets/09d55d61-3c3a-42a3-9e55-63cdef98e3eb" alt="AndLife Logo" width="200"/>

# AndLife: 나에게로의 초대

**이벤트의 시작부터 끝까지 초대, 기록, 감사를 연결하는 미디어 기반 아카이빙 플랫폼**

<br>


## Project Overview
AndLife는 흩어지기 쉬운 모임의 기록을 '초대장'이라는 이벤트 단위로 통합 관리합니다.    
단순한 정보 전달을 넘어 음성, 영상 등 풍부한 미디어를 활용한 방명록과 커스텀 에디터를 통한 초대장 제작 환경을 제공합니다.    

생일, 결혼, 졸업, 모임 등 다양한 이벤트에서 초대장을 작성하고 참여자들이 방명록으로 추억을 기록하며     
호스트가 감사 카드로 감사의 마음을 전달합니다.    

모든 과정이 하나의 앱에서 자연스럽게 연결되어, 이벤트가 끝난 후에도 소중한 기록으로 남습니다.    

<br>

## Tech Stack
| Category | Stack |
| :--- | :--- |
| **Core & UI** | ![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white) ![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white) ![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white) |
| **Architecture** | ![Clean Architecture](https://img.shields.io/badge/Clean%20Architecture-000000?style=for-the-badge) ![MVI](https://img.shields.io/badge/MVI-4CAF50?style=for-the-badge) ![Hilt](https://img.shields.io/badge/Hilt-2196F3?style=for-the-badge) |
| **Async & Reactive** | ![Coroutines](https://img.shields.io/badge/Coroutines-1565C0?style=for-the-badge&logo=kotlin&logoColor=white) ![Flow](https://img.shields.io/badge/Flow-00BCD4?style=for-the-badge&logo=kotlin&logoColor=white) |
| **Network & Storage** | ![Retrofit2](https://img.shields.io/badge/Retrofit2-424242?style=for-the-badge) ![OkHttp3](https://img.shields.io/badge/OkHttp3-212121?style=for-the-badge) ![Room](https://img.shields.io/badge/Room-3DDC84?style=for-the-badge&logo=android&logoColor=white) ![DataStore](https://img.shields.io/badge/DataStore-4285F4?style=for-the-badge&logo=android&logoColor=white) |
| **Media** | ![ExoPlayer](https://img.shields.io/badge/ExoPlayer-FF5722?style=for-the-badge&logo=google&logoColor=white) ![Media3](https://img.shields.io/badge/Media3-673AB7?style=for-the-badge&logo=android&logoColor=white) |

</div>

<br>


## Core Features
<table>
  <tr>
    <th align="left" width="35%">기능</th>
    <th align="left">미리보기</th>
  </tr>

  <!-- 초대 -->
  <tr>
    <td valign="top">
      <b>초대 (Invitation)</b>
      <ul>
        <li>이벤트 상세 정보를 정의하고, 커스텀 에디터로 나만의 초대 카드 제작</li>
        <li>생성 전 미리보기 제공</li>
      </ul>
    </td>
    <td>
      <p align="center">
        <img src="https://github.com/user-attachments/assets/7ebafbdb-7d12-4062-a392-f00be26a4316" width="31%" hspace="6" />
        <img src="https://github.com/user-attachments/assets/cfd02fc7-8926-40e0-8a64-857d3ed7c524" width="31%" hspace="6" />
        <img src="https://github.com/user-attachments/assets/42f4a0f7-8c9d-47d5-917b-956884a5c2b0" width="31%" />
      </p>
    </td>
  </tr>

  <!-- 공유 -->
  <tr>
    <td valign="top">
      <b>공유 (Sharing)</b>
      <ul>
        <li>딥링크로 앱 설치 여부와 무관하게 이벤트 연결</li>
        <li>카카오톡 공유 및 링크 복사 지원</li>
      </ul>
    </td>
    <td>
      <p align="center">
        <img src="https://github.com/user-attachments/assets/f85584a0-b6b5-411b-a690-89a923290922" width="31%" hspace="6" />
        <img src="https://github.com/user-attachments/assets/2727c218-087e-4540-8603-12181dd8e96a" width="31%" hspace="6" />
        <img src="https://github.com/user-attachments/assets/ee5ce917-033b-42fb-b07b-dcc48ab46cef" width="31%" />
      </p>
    </td>
  </tr>

  <!-- 참여 -->
  <tr>
    <td valign="top">
      <b>참여 (Participation)</b>
      <ul>
        <li>실시간 통합 피드 및 초대장 리스트 정렬</li>
        <li>다가오는 초대와 최근 방명록 한눈에 확인</li>
        <li>비로그인 사용자도 로컬 데이터로 참여 가능</li>
      </ul>
    </td>
    <td>
      <p align="center">
        <img src="https://github.com/user-attachments/assets/1f05b7cd-ed32-4133-94e6-8d852d06d3ac" width="31%" hspace="6" />
        <img src="https://github.com/user-attachments/assets/8baecbbb-98cf-4e41-88f6-025909b785c4" width="31%" hspace="6" />
        <img src="https://github.com/user-attachments/assets/b8a81c21-8a8b-4136-8a8d-c62a5365c7fe" width="31%" />
      </p>
    </td>
  </tr>

  <!-- 기록 -->
  <tr>
    <td valign="top">
      <b>기록 (Archiving)</b>
      <ul>
        <li>사진·영상·음성 멀티미디어 방명록 아카이빙</li>
        <li>촬영한 사진과 녹음 음성 메시지 바로 등록</li>
        <li>Grid / 스토리 뷰로 추억 모아보기</li>
      </ul>
    </td>
    <td>
      <p align="center">
        <img src="https://github.com/user-attachments/assets/6ddda54a-feb3-4c55-a735-5ef6e7200e35" width="31%" hspace="6" />
        <img src="https://github.com/user-attachments/assets/26d9a70f-7b3b-4ac7-841b-63f852950846" width="31%" hspace="6" />
        <img src="https://github.com/user-attachments/assets/ca4b0523-03fc-448d-91f6-e2f406d9f811" width="31%" />
      </p>
    </td>
  </tr>
</table>


<br>

## Module Dependency
<img width="5442" height="3488" alt="image" src="https://github.com/user-attachments/assets/54643434-9c76-46df-aaac-cba46b4cae37" />

<br>
<br>
<br>

## Team Members

| **김동경 (K004)** | **서정우 (K016)** | **윤미오 (K019)** | **최동현 (K028)** | **홍지민 (K032)** |
| :---: | :---: | :---: | :---: | :---: |
| <a href="https://github.com/dongykung"><img src="https://avatars.githubusercontent.com/u/92030316?v=4" width="120" height="120" /></a> | <a href="https://github.com/seojw0124"><img src="https://avatars.githubusercontent.com/u/89926090?v=4" width="120" height="120" /></a> | <a href="https://github.com/ant5555"><img src="https://avatars.githubusercontent.com/u/216482602?v=4" width="120" height="120" /></a> | <a href="https://github.com/CDHyun"><img src="https://avatars.githubusercontent.com/u/95002650?v=4" width="120" height="120" /></a> | <a href="https://github.com/jm3789"><img src="https://avatars.githubusercontent.com/u/84127399?v=4" width="120" height="120" /></a> |
| [@dongykung](https://github.com/dongykung) | [@seojw0124](https://github.com/seojw0124) | [@ant5555](https://github.com/ant5555) | [@CDHyun](https://github.com/CDHyun) | [@jm3789](https://github.com/jm3789) |

<br>
