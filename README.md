# Codeit Sprint Spring 백엔드 트랙 1기 스프린트 미션 1

---
## 📋 프로젝트 소개
디스코드 서비스를 활용해보며 각 도메인 모델에 필요한 정보를 도출하고 Java Class로 구현
- **도메인 모델** 
  - User
  - Channel
  - Message

---
## 🖥 개발 환경
- **Version**: Java 17
- **IDE**: IntelliJ

---
## 📌 주요 기능
- ### User
  - 새로운 유저 등록
    - 단, e-mail과 전화번호는 중복될 수 없음
  - 등록된 유저 목록 확인
  - 유저의 e-mail, 전화번호 수정
  - 유저의 처음 생성 시간 확인
  - 유저의 마지막 업데이트 시간 확인
  - 유저 삭제

- ### Channel
  - 새로운 채널 생성
    - 단, 채널명은 중복될 수 없음
  - 생성된 채널 목록 확인
  - 채널명 수정
  - 채널에 포함된 유저 등록
  - 채널에 포함된 유저 삭제
  - 채널에 포함된 유저 목록 확인
  - 채널의 처음 생성 시간 확인
  - 채널의 마지막 업데이트 시간 확인
  - 채널 삭제

- ### Message
  - 새로운 메세지 전송
    - 단, 존재하는 유저끼리만 전송 가능함
    - 빈 메세지는 전송할 수 없음
  - 전송된 메세지 목록 확인
  - 메세지 내용 수정
  - 메세지가 전송된 채널 확인
  - 전송된 메세지 삭제
  