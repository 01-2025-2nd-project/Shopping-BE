# FarmPlus Web Project
### 팀원 소개
----------
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;한유진🐰 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 임홍현😺 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; | 
| :--------------- | :--------------- | 
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[@yj267](https://github.com/yj267) | &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[@limhhyeon](https://github.com/limhhyeon) 



### 1. 프로젝트 소개
----------
- 본 프로젝트는 농산물 및 농업 관련 제품을 온라인에서 공동구매 방식으로 구매할 수 있는 쇼핑몰입니다.
- 농산업의 활성화와 소비자들에게 더 많은 혜택을 제공하는 것을 목표로 인원수에 따른 할인율이 적용됩니다.
- 유저는 회원가입, 로그인 기능 외에도 마이페이지를 통한 개인정보 수정 및 삭제가 가능합니다.
- 구매순, 가격순, 등록순에 따른 정렬이 가능하며, 카테고리별로 구분되어 원하는 상품을 빠르게 찾을 수 있습니다.
- 구매 및 구매 목록 조회는 물론, 공동구매 팟 생성 & 참여 & 관리가 가능합니다.
- 실시간 알림 및 읽음 여부에 따른 알림 처리 기능을 제공합니다.
 

### 2. 기술 스택
----------
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
----------
`username` : MySql username  
`password` : MySql password  
`secret key` : JWT secretKey  


### 4. ERD
----------
![image](https://github.com/user-attachments/assets/143542b9-9780-4157-8857-eb9ec9b592d8)


### 5. Environment Variables
----------
`username` : MySql username  
`password` : MySql password  
`secret key` : JWT secretKey  

### 5. 기능 전략
----------
1. Auth
- ` 로그인 ` :  
- ` 회원가입 ` :
- ` 닉네임 중복확인 ` :
- ` 이메일 중복 확인 ` :

### 6. 트러블 슈팅

| 🔴 error                        | 🔵 문제                                                                 | 🟢 해결 방법                                                               |
|---------------------------------|----------------------------------------------------------------------|--------------------------------------------------------------------------|
| `org.hibernate.MappingException`  | BaseEntity를 상속 받는데 BaseEntity와 필드명이 DB 컬럼명과 일치하지 않아 찾을 수 없다는 에러 발생 | 컬럼 명 일치로 해결 완료                                                     |
| `UnsatisfiedDependencyException`  | 의존성 도입 도중 repository 메소드에 제대로 된 필드 값이 들어오지 않아 발생                  | JpaRepository에 페이지네이션으로 구현했으나 파라미터에 pageable을 넣지 않아 발생했다. pageable을 파라미터로 넣어주어 해결 |
| `SQLSyntaxErrorException`        | JPQL을 쓰던 중 GroupBy절을 쓰는데 group으로 묶은 필드가 들어오지 않아 발생             | JGroupBy에 필요한 필드들을 담아서 해결 | 
| `PropertyValueException`        | PartyUser 엔티티는 Party 필드값이 NotNull인데 파티를 먼저 삭제하고 파티 유저를 삭제하려고 하니 파티 유저의 파티 값이 null로 되어 발생                  | 파티유저를 먼저 삭제 후 파티를 삭제하여 해결 | 
| `N+1`        | @ManyToOne과 @OneToMany 사용 시 조회를 하는 과정에서 한 번의 쿼리문이 아닌 반복적인 쿼리 문 발생                  | 1. 필요한 필드명만 따로 뺀 클래스를 만들어 해결  | 
|        | Fetch Join을 사용하여 해결                  | 파티유저를 먼저 삭제 후 파티를 삭제하여 해결 | 
| `cors(Mixed Content)`        | 백엔드가 배포한 프로젝트에서 프론트 배포 주소 허용 안해서 발생                  | 프론트 요청 주소 허용해주는 config 빈으로 등록 후 SecutiryConfig에 추가하여 해결 | 
| `동시성 문제`        | 파티 등록할 때 여러 사람이 동시에 파티에 참여할 때 참여 가능한 인원수보다 오바되는 경우 발생              | 파티를 조회할 때 낙관적 락을 걸어 처리가 끝난 후에 다음 조회 처리가 가능하도록 하여 해결 |
|         | 한 사람이 여러 파티에 동시에 가입할 때 돈이 한 파티에 관해서만 빠지는 현상 발생                  | 유저의 돈을 업데이트 할 때 db에서 처리하도록 UserRepository에서 update문을 넣어주어 실행하도록 변경하여 해결 |



### 7. Lessons Learned
----------

### Lessons

#### *ArgumentResolver*
프로젝트를 진행하면서 토큰 기반 사용자 인증을 효율적으로 처리하기 위해 ArgumentResolver를 적용하는 방법을 배웠습니다. 이를 통해 컨트롤러에서 매번 토큰을 가지고 서비스에서 유저를 조회하는 로직을 제거하고, 반복되었던 코드를 줄일 수 있게되었습니다.

배운점:
###### 1. ArgumentResolver의 역할
- 컨트롤러에서 반복적으로 토큰을 기반으로 사용자 정보를 찾는 코드를 줄일 수 있다.
###### 2.설정 방법
- HandlerMethodArgumentResolver를 구현하여 JWT 토큰을 해석하고 유저 정보를 반환하도록 구현한다.
- Spring의 WebMvcConfigurer를 이용해 ArgumentResolver를 등록한다.
- 컨트롤러에서는 @RequestBody처럼 바로 유저 정보를 받을 수 있다.
###### 3.적용 후 개선점
- 컨트롤러 코드 간소화 → @Authentificatio CustomUserDetails로 매번 받고 User를 찾는 반복적인 코드를 파라미터 하나로 정리가 가능.
- 보안 강화 → 인증 검증을 중앙에서 처리하여 중복 코드 제거 및 일관된 보안 적용 가능.

#### *Scheduler*
프로젝트를 진행하면서 기간이 지난 파티들을 자동으로 실패 처리하기 위해 @Scheduled를 적용하는 방법을 배웠습니다. 이를 통해 수동으로 갱신할 필요 없이 정해진 시간마다 상태를 업데이트할 수 있었습니다.

배운 점 :
###### 1.Scheduler의 역할
- 특정 주기마다 자동으로 실행되는 작업을 수행할 수 있다.
- Spring의 @Scheduled를 활용하면 별도의 트리거 없이 정기적으로 특정 메서드를 실행할 수 있다.
###### 2. 설정 방법
- @EnableScheduling을 설정하여 스케줄러 기능 활성화
- @Scheduled(fixedRate = 10000) 또는 @Scheduled(cron = "0 0 0 * * *")을 사용하여 주기적으로 실행할 작업을 등록할 수 읶다.
- 서비스 또는 별도 컴포넌트에서 기간이 지난 파티를 조회하고 상태를 변경할 수 있다.
###### 3. 적용 후 개선점
- 자동화 → 수동으로 만료된 파티를 변경할 필요 없이 자동으로 상태가 업데이트됨
- 성능 최적화 → 매번 DB를 조회하지 않고, 정해진 시간에만 처리하도록 스케줄링 가능
- 유지보수 용이 → @Scheduled를 활용하면 비즈니스 로직을 컨트롤러와 분리하여 깔끔하게 관리 가능

#### *비관적 락 (Pessimistic Lock)*
프로젝트를 진행하면서 상품 수량이나 파티 참여 인원 등의 동시성 문제를 해결하기 위해 **비관적 락(Pessimistic Lock)**을 적용하는 방법을 배웠습니다. 이를 통해 동시에 여러 트랜잭션이 동일한 데이터를 수정하는 충돌을 방지할 수 있었습니다.

배운 점:
##### 1.비관적 락의 역할
- 트랜잭션이 데이터를 조회할 때, 다른 트랜잭션이 해당 데이터를 수정하지 못하도록 잠금(Lock) 설정
즉, 데이터 충돌 가능성을 미리 차단하여 동시성 이슈를 방지
##### 2.설정 방법
-JPA의 @Lock(LockModeType.PESSIMISTIC_WRITE)을 사용하여 테이블 레벨에서 락을 걸 수 있음
- 상품 수량이나 파티 참여 인원을 조정할 때, 트랜잭션이 종료될 때까지 다른 트랜잭션의 접근을 차단
- PESSIMISTIC_READ와 PESSIMISTIC_WRITE의 차이를 이해하고 적절한 방식 선택
###### 3.적용 후 개선점
- 데이터 일관성 보장 → 동시 요청이 많아도 상품 수량이나 파티 참여 인원 데이터가 정확하게 유지됨
- 경쟁 조건 해결 → 동시에 여러 사용자가 접근해도 잘못된 데이터 저장 문제를 방지
- 안전하지만 성능 저하 가능성 → 트랜잭션이 길어지면 데드락(deadlock) 발생 가능성이 있으므로 주의해야 함

#### *@Modifying을 활용한 유저 금액 업데이트*
프로젝트를 진행하면서 비관적 락(Pessimistic Lock)의 성능 비용이 크다는 점을 고려하여, 유저 돈을 업데이트하는 것은 비관적인 락을 사용하는 것이 아닌 다른 방법인 데이터베이스에서 직접 처리하는 방식을 적용했습니다. 이를 위해 JPA의 @Modifying과 @Query를 사용하여 유저의 잔액을 업데이트하는 방법을 배웠습니다.

배운 점:
###### 1. @Modifying을 활용한 직접 쿼리 실행
- @Modifying을 사용하면 JPA가 아닌 DB 레벨에서 바로 데이터를 업데이트할 수 있음
- 엔터티를 조회 후 변경하는 방식 대신, 한 번의 SQL 실행으로 데이터 수정 가능
###### 2. 설정 방법
- Spring Data JPA에서 @Modifying과 @Query를 사용하여 잔액을 바로 감소시키는 SQL 실행
- @Transactional을 함께 적용하여 트랜잭션 내에서 처리되도록 보장
  
```
@Modifying
@Query("UPDATE User u SET u.balance = u.balance - :amount WHERE u.id = :userId AND u.balance >= :amount")
int deductBalance(@Param("userId") Long userId, @Param("amount") int amount);
```
- 위 코드를 실행하면, 잔액이 충분한 경우에만 감소하며, 여러 사용자가 동시에 요청해도 충돌이 줄어듦

###### 3. 적용 후 개선점
- 성능 향상 → 비관적 락(Pessimistic Lock) 대신, 데이터베이스가 직접 처리하여 성능 비용을 절감
- 경쟁 조건 해결 → SQL 한 줄로 업데이트하므로 동시성 문제가 줄어듦
- 트랜잭션 충돌 방지 → 기존에 엔터티를 조회하고 수정하는 방식보다 트랜잭션 시간이 짧아져 데드락 위험이 감소

### Learned

#### *Pagination*
이번 프로젝트에서는 데이터 처리와 페이지네이션을 다루는 데 어려움을 겪었는데, 팀원이 작성한 페이지네이션 코드를 보면서 효율적인 데이터 표시 방법에 대해 많은 것을 배웠습니다. 특히, 대용량 데이터를 처리할 때 성능을 고려한 페이지네이션의 중요성을 깨달았고, 이를 통해 효율적인 데이터 조회가 가능하다는 점을 알게 되었습니다. 다음 프로젝트에서는 제가 직접 페이지네이션을 적용해 보고, 더 나은 성능을 위해 최적화도 시도해보려 합니다.

#### *AWS EC2 배포*
이전 프로젝트에서는 AWS EC2를 사용해 서버를 배포하는 과정에서 여러 가지 문제에 부딪혔습니다. 특히, 서버 설정과 배포 후 운영에 어려움이 있었지만, 팀원이 EC2 배포 과정을 깔끔하게 처리한 코드를 보면서 많은 것을 배웠습니다.  다음 프로젝트에서는 AWS EC2를 사용해 직접 배포하고 운영하는 경험을 쌓아볼 계획입니다 추가로 매번 코드가 수정되면 재배포를 진행했어야 했는데 다음 프로젝트에서는 지속적인 통합(CI) 지속적인 배포(CD)도 적용해 볼 것입니다.





### 8. Feedback

### 9. 회고
