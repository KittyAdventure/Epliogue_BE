# 에필로그 - 책을 덮은 후 시작되는 또 다른 이야기 📕


<div align="center">
    <img src="https://github.com/user-attachments/assets/06fdbc83-241f-46a8-88c0-977a46a9ec75" width="500">
</div>


**"에필로그 – 책을 읽고, 나누며 함께 성장하는 독서 커뮤니티입니다!"**   

- **사용자 기능**
    - 로그인 기능
        - 소셜 로그인 지원(카카오 or 구글)
        - JWT를 이용해 구현 예정
    - 회원가입 기능
        - 회원가입 시 수집하는 정보들
            - ID
            - PW
            - 닉네임
            - 이름
            - 생년월일
            - 이메일
            - 전화번호
            - 프로필 사진(AWS S3 이용하여 구현 예정 / 필수 X)
    - 회원정보 수정 기능
    - 회원탈퇴
    - 팔로우 기능
        - 인증된 사용자가 특정 사용자를 팔로우 한다.
        - 인증된 사용자가 특정 사용자를 언팔로우 한다.
        - 인증된 사용자가 팔로우한 유저의 리뷰를 조회할 수 있다.
        - 책 상세 페이지에서 팔로우한 유저의 별점을 조회할 수 있다.
    - 좋아요 기능
        - 회원은 다른 회원의 코멘트 , 컬렉션에 좋아요를 남길 수 있다.
        - 좋아요의 갯수를 확인 할 수 있다.
    - 포인트 충전 , 환불 기능
        - 회원은 카카오페이 결제 API 를 이용해서 포인트를 충전할 수 있다
        - 회원은 카카오페이 결제를 취소할 수 있다.
            - 회원은 2일이내 카카오페이 결제건을 취소요청 할 수 있다.
            - 카카오페이로 환불받을땐, 결제했던 금액보다 적은 금액을 보유하고 있을 시 환불이 불가하다.
            (ex. 결제 금액 10000원 → 아이템 구매로 500원 차감 → 보유 금액 9500 포인트로 10000원 환불 불가)
            - Ref. https://developers.kakaopay.com/docs/payment/online/common
    
    ---
    
- **공통 기능**
    - 모임 기능
        - 온라인 북토크 기능
            - 그룹 채팅 시스템 ( 최대 30 명 까지 )
            - 방제목 = 책제목
            - 유저가 입장하면 방이 생성된다. 모든유저가 퇴장하면 방이 삭제된다
            - 채팅 방이 폐쇄되면 메시지는 삭제된다
            - 메시지를 보낸 회원 프로필로 이동가능
        - 오프라인 북토크 기능
            - 회원은 자유롭게 모임을 개설할 수 있다.
            - 모임 개설 할때 수집하는 정보
                - 날짜 & 시간
                - 장소
                - 모임 제목
                - 모임 소개글
                - 현재 인원 , 최대 인원 (50명까지)
            - 모임은 자유롭게 참석이 가능하다
    - 오늘의 HOT한 책보기 기능
        - 오늘 가장 많이 조회된 책을 10개까지 조회할 수 있다.
    - 컬렉션 기능
        - 회원은 자유롭게 책을 컬렉션에 넣을 수 있다.
    - 리뷰 남기기
        - 회원은 책에 대한 리뷰를 남길 수 있다.(최대 10000자까지 가능)
        - 리뷰에는 사진 첨부도 가능하다 ( 5개까지 )
        - 사진은 텍스트 중간에 삽입할 수도 있다.
        - 리뷰를 최신순, 좋아요 순으로 정렬가능하다.
        - 한 리뷰에는 여러개의 댓글이 달릴 수 있다.
    - 평가 기능
        - 회원은 책에 대해 별점을 남길 수 있다 (한 책에는 하나의 별점만 남길 수 있다.)(0.5 점 단위 0.0~5.0 )
    - 검색 기능
        - 다양한 필터를 적용하여 정보 검색이 가능하다
        
               ( ex. 책 제목으로 검색, 코멘트 내용으로 검색, 컬렉션 제목으로 검색 , 회원 검색)
        
    - 알림 기능
        - 회원의 댓글에 대한 답글 , 좋아요가 달리면 알림을 보내준다
    - 상점 기능 / 포인트 기능
        - 사용자는 보유한 포인트를 이용해 코멘트 색 커스터마이징이 가능하다
    - 공유 기능
        - 인증된 사용자가 특정 리소스(책, 리뷰)에 대한 URI 공유
        - 인증된 사용자가 특정 리소스(책, 리뷰)를 카카오 메시지 보내기 API를 사용해 공유
    - 책 정보 조회
        - 회원은 한 책에 대한 상세 정보를 조회할 수 있다 (네이버 책검색 API)
            - 책 제목
            - 책 작가
            - 책 평균 별점
            - Ref. https://developers.naver.com/docs/serviceapi/search/book/book.md
    - 인기 검색어 기능
        - Redis 를 이용하여, 24시간동안 가장 많이 검색된 단어를 10개까지 조회할 수 있다.
    - 인기 책 조회 기능
        - 작일 00:00 ~ 24:00 데이터 + 금일 00:00 ~ 12:00 기준으로 상세 조회된 책을 수집하여 조회할 수 있다
        - 매일 12:00 마다 작일 00:00 ~ 23:59 데이터는 삭제된다
    - 이 작가의 다른책 추천 기능
        - 책 상세 페이지에서 같은 작가의 다른 책을 리스트로 보여주는 기능이다

## ERD 
![Epilogue (3)](https://github.com/user-attachments/assets/f8475b09-b6cf-4a84-9933-fd47491b2f28)
URL : https://www.erdcloud.com/d/4sjqx5n55nPJKsNY5

# Tech Stack
## Back-end
- Spring Boot
- Spring Security
- Spring Data JPA
- QueryDSL
- JWT
- Lombok
- SSE
- WebSocket

## DataBase
- MariaDB
- MongoDB
- Reids

## Infra
- AWS EC2
- AWS S3
- Docker
