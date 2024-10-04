# 강의 신청 시스템 ERD 설계
<img width="1204" alt="image" src="https://github.com/user-attachments/assets/161a9cdb-3cd7-4752-9ec1-ef2afb6a058e">

- Lecture : 강의는 강연자, 강의이름 으로 구성된다.
- LectureItem: 강의에 대해 여러개의 시간대로 구성할 수 있고, LectureItem 마다 수용 인원을 관리할 수 있다.
- LectureRegistration: User 와 LectureItem 의 FK 를 하나씩 받아 유저의 참여 여부를 레코드로 삽입 될 수 있다. User 와 LectureItem 의 N:M 관계를 일대다 다대일로 나누었음.
- User: 강의에 참여할 유저

