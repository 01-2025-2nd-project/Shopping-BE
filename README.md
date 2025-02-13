# FarmPlus Web Project
### 팀원 소개
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;한유진🐰 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 임홍현😺 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; | 
| :--------------- | :--------------- | 
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[@yj267](https://github.com/yj267) | &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[@limhhyeon](https://github.com/limhhyeon) 



### 1. 프로젝트 소개
- 본 프로젝트는 농산물 및 농업 관련 제품을 온라인에서 공동구매 방식으로 구매할 수 있는 쇼핑몰입니다.
- 농산업의 활성화와 소비자들에게 더 많은 혜택을 제공하는 것을 목표로 인원수에 따른 할인율이 적용됩니다.
- 유저는 회원가입, 로그인 기능 외에도 마이페이지를 통한 개인정보 수정 및 삭제가 가능합니다.
- 구매순, 가격순, 등록순에 따른 정렬이 가능하며, 카테고리별로 구분되어 원하는 상품을 빠르게 찾을 수 있습니다.
- 구매 및 구매 목록 조회는 물론, 공동구매 팟 생성 & 참여 & 관리가 가능합니다.
- 실시간 알림 및 읽음 여부에 따른 알림 처리 기능을 제공합니다.
 

### 2. 기술 스택
` Java 17+ ` : 최신 기능과 성능 개선을 위해 사용  
` Spring Boot `:  REST API 및 웹 애플리케이션 표준  
` Gradle ` : 프로젝트 관리 및 의존성 관리  
` MriaDB ` : 관계형 DBMS  
` JPA(Hibernate) ` : Java와 DB 간의 객체-관계 매핑을 위해 사용  
` SLF4J `: 애플리케이션 로깅을 위해 사용  
` Spring MVC + REST API ` : RESTful API 개발  
` Spring Security ` : 인증 및 권한 관리  
` Spring Cache ` : DB 리소스를 줄이기 위한 스프링 캐시 생성해서 관리  
` Spring Schedule` : 스케줄 관리 필요하여 사용  
` 비관적 락(Pessimistic Lock) ` : 동시성을 위해  
` AWS EC2 ` : 클라우드 서비스  
` RDS ` : 클라우드 DB 서버  
` Git / GitHub ` : 코드 형상 관리  


### 3. 환경변수
`username` : MySql username  
`password` : MySql password  
`secret key` : JWT secretKey  


### 4. ERD
![image.png](attachment:60e463cc-b4fa-48d9-bffe-eefcdd6174f4:image.png)

### 5. Environment Variables
`username` : MySql username
`password` : MySql password
`secret key` : JWT secretKey

### 5. 기능 전략
1. Auth
- ` 로그인 ` :  
- ` 회원가입 ` :
- ` 닉네임 중복확인 ` :
- ` 이메일 중복 확인 ` :

### 6. 트러블 슈팅
| 🔴 error                        | 🔵 문제                                                                 | 🟢 해결 방법                                                               |
|---------------------------------|----------------------------------------------------------------------|--------------------------------------------------------------------------|
| org.hibernate.MappingException  | BaseEntity를 상속 받는데 BaseEntity와 필드명이 DB 컬럼명과 일치하지 않아 찾을 수 없다는 에러 발생 | 컬럼 명 일치로 해결 완료                                                     |
| UnsatisfiedDependencyException  | 의존성 도입 도중 repository 메소드에 제대로 된 필드 값이 들어오지 않아 발생                  | JpaRepository에 페이지네이션으로 구현했으나 파라미터에 pageable을 넣지 않아 발생했다. pageable을 파라미터로 넣어주어 해결 |
| SQLSyntaxErrorException        | 의존성 도입 도중 repository 메소드에 제대로 된 필드 값이 들어오지 않아 발생                  | JpaRepository에 페이지네이션으로 구현했으나 파라미터에 pageable을 넣지 않아 발생했다. pageable을 파라미터로 넣어주어 해결 |



### 7. Lessons Learned

### 8. Feedback

### 9. 회고
