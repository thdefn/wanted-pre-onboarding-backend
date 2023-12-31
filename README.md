
###### 성명 : 송원선
##### Index
1. [애플리케이션 설명과 구현 이유](#애플리케이션-설명과-구현-이유)
   1. [서버 도식화](#1-클라우드-환경-도식화)
   2. [데이터베이스 테이블 구조](#2-데이터베이스-테이블-구조)
   3. [소스 코드 구조](#3-소스-코드-구조)
2. [데모 영상 링크](#데모-영상-링크)
3. [애플리케이션 이용 방법](#애플리케이션-이용-방법)
4. [API 명세](#api-명세)
    1. [사용자 회원가입](#1-사용자-회원가입)
    2. [사용자 로그인](#2-사용자-로그인)
    3. [게시글 생성](#3-게시글-생성)
    4. [게시글 목록 조회](#4-게시글-목록-조회)
    5. [게시글 상세 조회](#5-게시글-상세-조회)
    6. [게시글 수정](#6-게시글-수정)
    7. [게시글 삭제](#7-게시글-삭제)
    8. [토큰 재발급](#8-토큰-재발급)


## 애플리케이션 설명과 구현 이유
Host : `34.64.48.240:8080`

<div>
<img src="https://img.shields.io/badge/Java17-ED1D25?logo=Java&logoColor=white" />
<img src="https://img.shields.io/badge/MySql8-4479A1?style=flat&logo=MySQL&logoColor=white" />
<img src="https://img.shields.io/badge/spring boot 3.1.2-6DB33F?style=flat&logo=springboot&logoColor=white" />  
<img src="https://img.shields.io/badge/spring security 6.1.2-6DB33F?style=flat&logo=springsecurity&logoColor=white" />
<br/>
<img src="https://img.shields.io/badge/googlecloud-4285F4?style=flat&logo=google cloud&logoColor=white" /> 
<img src="https://img.shields.io/badge/github actions-2088FF?style=flat&logo=githubactions&logoColor=white" />  
<img src="https://img.shields.io/badge/Docker-2496ED?style=flat&logo=Docker&logoColor=white" />
<img src="https://img.shields.io/badge/DockerCompose-2496ED?style=flat&logo=Docker&logoColor=white" /> 
</div>

#### DB 세팅
아래 docker-compose.yml 파일이 저장된 위치에서 `docker-compose up -d` 합니다.

```yml
docker-compose.yml

version: '3'
services:
  database:
    image: mysql:latest
    container_name: mysql
    ports:
      - 3306:3306
    environment:
      MYSQL_DATABASE: wanted
      MYSQL_ROOT_PASSWORD: 1234
      TZ: Asia/Seoul
    command:
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_unicode_ci
    restart: always
    volumes:
      - ./mysql/db:/var/lib/mysql
    networks:
      - wanted-net
networks:
  wanted-net:
```
> network 나 TimeZone, 유니코드 등의 DB 설정이 있는데 평가 환경과 최대한 개발 환경을 동일하게 하기 위한 방법을 고민하다 docker compose 를 이용해 DB 환경을 구성했습니다.


### 1. 클라우드 환경 도식화

<img src="https://github.com/thdefn/wanted-pre-onboarding-backend/assets/80521474/54d6f41f-87b1-4b70-a899-76632d1fa348" width="700" height="400"/>

#### CI 과정
1. `wanted-pre-onboarding-backend` 의 main 브런치에 pull 이벤트가 일어나면 `Github Actions` 가 동작합니다.
2. `Github Actions` 가 `Dockerfile`을 이용해 도커 이미지를 빌드하고, 도커 허브에 올립니다.
3. `Github Actions` 가 GCP 서버에 접속하여, 도커 허브에서 도커 이미지를 pull 받고 배포합니다.
4. `MySQL` 도커와 `Spring jar` 도커가 도커 네트워크로 이어져 통신합니다.

> `Github Actions` 는 jar 빌드, docker 이미지 생성, docker push 등 많은 액션이 이미 구현되어 있어 쉽게 CI 하기 좋은 도구라고 생각했습니다. 앱의 규모가 크지 않아 계속 인스턴스가 띄워져 있어야 하는 Jenkins 보다 자원을 아낄 수 있을 것이라고 생각해 `Github Actions` 를 선택했습니다.  

### 2. 데이터베이스 테이블 구조
<img src="https://github.com/thdefn/wanted-pre-onboarding-backend/assets/80521474/610b9ad9-f6f4-4340-8e16-50bde9f12cf5" width="400" height="250"/>

> 테이블 구조는 주어진 요구사항을 만족하는 최소한의 규모로 설계했습니다.

### 3. 소스 코드 구조

```
 📦 wanted-pre-onboarding-backend
├─ .github
│  └─ workflows
│     └─ deploy-image.yml
├─ README.md
├─ Dockerfile
├─ docker-compose.yml
├─ build.gradle
├─ gradlew
├─ api.http
└─ src
   ├─ main
   │  ├─ java.com.example.wanted
   │  └─ resources
   │     └─ application.yml
   └─ test
      └─ java.com.example.wanted
```
©generated by [Project Tree Generator](https://woochanleee.github.io/project-tree-generator)
> `docker compose` 를 이용해 DB 기본 세팅을 했습니다. `.github/workflows/deploy-image.yml`와  `Dockerfile` 을 통해 CI를 구현했습니다.

---
```
 main
│  ├─ java.com.example.wanted
│  │ ├─ WantedApplication.java
│  │ ├─ config
│  │ │  └─ JpaAuditingConfig.java
│  │ ├─ controller
│  │ │  ├─ AuthController.java
│  │ │  └─ PostController.java
│  │ ├─ domain
│  │ │  ├─ constants
│  │ │  │  ├─ Constants.java
│  │ │  │  └─ MemberType.java
│  │ │  ├─ model
│  │ │  │  ├─ BaseEntity.java
│  │ │  │  ├─ Member.java
│  │ │  │  ├─ Post.java
│  │ │  │  └─ RefreshToken.java
│  │ │  └─ repository
│  │ │     ├─ MemberRepository.java
│  │ │     ├─ PostRepository.java
│  │ │     └─ RefreshTokenRepository.java
│  │ ├─ dto
│  │ │  ├─ PostDto.java
│  │ │  ├─ PostForm.java
│  │ │  ├─ SignInForm.java
│  │ │  ├─ SignUpForm.java
│  │ │  ├─ TokenDto.java
│  │ │  └─ TokenRefreshForm.java
│  │ ├─ exception
│  │ │  ├─ ErrorCode.java
│  │ │  ├─ ExceptionResponse.java
│  │ │  ├─ GlobalExceptionHandler.java
│  │ │  ├─ MemberException.java
│  │ │  └─ PostException.java
│  │ ├─ security
│  │ │  ├─ JwtAuthenticationFilter.java
│  │ │  ├─ SecurityConfig.java
│  │ │  ├─ TokenProvider.java
│  │ │  ├─ UserDetailsImpl.java
│  │ │  └─ UserDetailsServiceImpl.java
│  │ ├─ service
│  │ │  ├─ AuthService.java
│  │ │  └─ PostService.java
│  │ └─ util
│  │    └─ TimeUtil.java
└─ resources
│   └─ application.yml
└ test
  └─ java.com.example.wanted
     └─ WantedApplicationTests.java
        └─ controller
        │   └─ AuthControllerTest.java
        │   └─ PostControllerTest.java
        ├─ mockuser
        │   └─ WithMockCustomUser.java
        │   └─ WithMockCustomUserSecurityContextFactory.java
        ├─ security
        │   └─ TokenProviderTest.java
        ├─ service
        │   └─ AuthServiceTest.java
        │   └─ PostServiceTest.java
        └─ util
           └─ TimeUtilTest.java
```
©generated by [Project Tree Generator](https://woochanleee.github.io/project-tree-generator)
> main 코드는 config controller domain dto exception service util 로 각각의 역할이 분명하게 나눴습니다. model 과 repository 는 DB 레이어를 담당한다는 면에서 역할이 비슷하다고 생각해 domain 패키지로 함께 분류했습니다. security 패키지는 인증 및 인가와 관련된 클래스들로 이루어져 있는데, UserDetailsServiceImpl 이 UserDetailsService의 구현체이지만 Service 보다는 더 앞단에서 인증한다는 면에서 security 로 분류했습니다. UserDetailsImpl 또한 엔티티와 유사하지만 컨트롤러 외부에서 인증 및 인가 정보를 가지고 있는 기능을 하기 때문에 security 로 분리했습니다.
> 
> Controller 와 Service 뿐만 아니라 util 성 클래스에도 test 코드를 작성하여, 비즈니스 로직을 조금 더 깊이 고민하며 구현했습니다.


## 데모 영상 링크

[![시연 영상](http://img.youtube.com/vi/eU1FBa_Hx9U/0.jpg)](https://youtu.be/eU1FBa_Hx9U)

## 애플리케이션 실행 방법

`http://34.64.48.240:8080` 로 아래 [API 명세](#api-명세)에 따라 실행해주시면 됩니다.
깃허브 레포지토리 최상단의 `api.http` 파일을 인텔리제이에서 활용하실 수 있습니다.

<img width="528" alt="스크린샷 2023-08-06 오후 6 03 53" src="https://github.com/thdefn/wanted-pre-onboarding-backend/assets/80521474/72173da1-12d9-4181-93e7-664d0347fb46">


## API 명세
### 1. 사용자 회원가입
#### request
##### request syntax
```http
POST /auth/sign-up HTTP/1.1
Host: 34.64.48.240:8080
Content-Length: 62

{
    "email": "abcde@gmail.com",
    "password": "12345678"
}
```
##### request elements
| 필드       | 타입     | 필수 여부 | 설명    |
|----------|--------|----|-------|
| email    | `String` | 필수 | '@' 포함 |
| password | `String` | 필수 | 8자 이상 |

#### response
##### response syntax
```http
HTTP/1.1 200 
```
##### response status
| 상태    | 타입 | message |
|-------|----|---------|
| `200` | 정상 |         |
| `400`   | **에러** | 8자 이상의 비밀번호를 입력해주세요. |
| `400`   | **에러** | 이메일 형식에 맞지 않습니다. |
| `400`   | **에러** | 이미 존재하는 이메일입니다. |
---

### 2. 사용자 로그인
#### request
##### request syntax
```http
POST /auth/sign-in HTTP/1.1
Host: 34.64.48.240:8080
Content-Length: 62

{
    "email": "abcde@gmail.com",
    "password": "12345678"
}
```
##### request elements
| 필드       | 타입     | 필수 여부 | 설명    |
|----------|--------|----|-------|
| email    | `String` | 필수 | '@' 포함 |
| password | `String` | 필수 | 8자 이상 |

#### response
##### response syntax
```http
HTTP/1.1 200 

{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJvbmVzdW44MTRAZ21haWwuY29tIiwiaWF0IjoxNjkxMjQ0MjQzLCJleHAiOjE2OTEyNDYwNDN9.rLMwk3-MaDA8xE7kdOpEjzOgiUfhVf1ZpMNJ0TtCvOM",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2OTEyNDQyNDMsImV4cCI6MTY5MjQ1Mzg0M30.ieVuCOWGrU-iezmbsU7MDYQ-JxNwG1zjVjR_bkos5lg"
}
```
##### response elements
| 필드       | 타입     | 설명      |
|----------|--------|---------|
| accessToken    | `String` | 엑세스 토큰  |
| refreshToken | `String` | 리프레쉬 토큰 |
##### response status
| 상태    | 타입 | message |
|-------|----|---------|
| `200` | 정상 |         |
| `400`   | **에러** | 8자 이상의 비밀번호를 입력해주세요. |
| `400`   | **에러** | 이메일 형식에 맞지 않습니다. |
| `400`   | **에러** | 비밀번호가 올바르지 않습니다. |
| `400`   | **에러** | 등록되지 않은 이메일입니다. |
---

### 3. 게시글 생성
#### request
##### request syntax
```http
POST /posts HTTP/1.1
Host: 34.64.48.240:8080
Content-Length: 211
Authorization: Bearer {accessToken}

{
    "title":"출시 100일' 나이트크로우 매출 상위권...위메이드 장기 흥행 노린다",
    "content":"지난 4월 국내 출시된 나이트 크로우는 4일 서비스 100일을 넘긴 최근까지 구글 플레이, 애플 앱스토어 매출 최상위권을 유지하고 있다. 이 게임은 중세 유럽 기반의 신규 지식재산(IP)으로, 제4차 십자군 전쟁이 끝난 직후 혼돈스러운 유럽을 바탕으로 하는 세계관과 국"
}
```
##### request elements
| 필드      | 타입     | 필수 여부 | 설명      |
|---------|--------|----|---------|
| title   | `String` | 필수 | 1-150자  |
| content | `String` | 필수 | 1-1000자 |
#### response
##### response syntax
```http
HTTP/1.1 200 

{
  "postId": 12,
  "title": "출시 100일' 나이트크로우 매출 상위권...위메이드 장기 흥행 노린다",
  "content": "지난 4월 국내 출시된 나이트 크로우는 4일 서비스 100일을 넘긴 최근까지 구글 플레이, 애플 앱스토어 매출 최상위권을 유지하고 있다. 이 게임은 중세 유럽 기반의 신규 지식재산(IP)으로, 제4차 십자군 전쟁이 끝난 직후 혼돈스러운 유럽을 바탕으로 하는 세계관과 국",
  "nickName": "익명",
  "writerId": 6,
  "isReadersPost": true,
  "createdAt": "0초 전"
}
```
##### response elements
| 필드      | 타입        | 설명              |
|---------|-----------|-----------------|
| postId  | `Long`    | 게시물 아이디         |
| title   | `String`  | 게시물 제목          |
| content | `String`  | 게시물 내용          |
| nickName | `String`  | 작성자 닉네임은 익명으로 통일 |
| writerId  | `Long`    | 작성자 아이디         |
| isReadersPost  | `Boolean` | 읽는 유저의 게시물인지 여부 |
| createdAt  | `String`  | 만들어진 시간         |

##### response status
| 상태    | 타입 | message |
|-------|----|---------|
| `200` | 정상 |         |
| `400`   | **에러** | 공백일 수 없습니다 |
| `400`   | **에러** | 1-150자의 제목을 작성해주세요. |
| `400`   | **에러** | 1-1000자의 내용을 작성해주세요. |
---

### 4. 게시글 목록 조회
#### request
##### request syntax
```http
GET /posts?page=1 HTTP/1.1
Host: 34.64.48.240:8080
Authorization: Bearer {accessToken}
```
##### request elements
| 필드   | 타입        | 필수 여부 | 설명     |
|------|-----------|-------|--------|
| page | `Integer` | 선택    | 페이지 번호 |
#### response syntax
##### 정상 응답
```http
HTTP/1.1 200 

{
  "content": [
    {
      "postId": 1,
      "title": "제목입니다",
      "content": "내용입니다",
      "nickName": "익명",
      "writerId": 4,
      "createdAt": "4시간 전"
    }
  ],
  "pageable": {
    "sort": {
      "empty": true,
      "unsorted": true,
      "sorted": false
    },
    "offset": 10,
    "pageNumber": 1,
    "pageSize": 10,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalPages": 2,
  "totalElements": 11,
  "first": false,
  "size": 10,
  "number": 1,
  "sort": {
    "empty": true,
    "unsorted": true,
    "sorted": false
  },
  "numberOfElements": 1,
  "empty": false
}

```
##### response elements
| 필드                          | 타입        | 설명                  |
|-----------------------------|-----------|---------------------|
| $.content.[0].postId        | `Long`    | 게시물 아이디             |
| $.content.[0].title         | `String`  | 게시물 제목              |
| $.content.[0].content       | `String`  | 게시물 내용              |
| $.content.[0].nickName      | `String`  | 작성자 닉네임은 익명으로 통일    |
| $.content.[0].writerId      | `Long`    | 작성자 아이디             |
| $.content.[0].createdAt     | `String`  | 만들어진 시간             |
| totalPages                  | `Integer` | 전체 페이지의 개수          |
| totalElements               | `Integer` | 전체 게시물의 개수          |
| last                        | `Boolean` | 현재 페이지가 마지막 페이지인지 여부 |
| first                       | `Boolean` | 현재 페이지가 첫번째 페이지인지 여부 |
| empty                       | `Boolean` | 현재 페이지에 게시물이 있는지 여부 |
| size                        | `Integer` | 한 페이지당 게시물의 수       |
| numberOfElements            | `Integer` | 현재 페이지의 게시물의 수      |
---
### 5. 게시글 상세 조회
#### request
##### request syntax
```http
GET /posts/{postId} HTTP/1.1
Host: 34.64.48.240:8080
Authorization: Bearer {accessToken}
```
##### request elements
| 필드   | 타입        | 필수 여부 | 설명      |
|------|-----------|------|---------|
| postId | `Integer` | 필수   | 게시글 아이디 |
#### response
```http
HTTP/1.1 200 

{
  "postId": 10,
  "title": "출시 100일' 나이트크로우 매출 상위권...위메이드 장기 흥행 노린다",
  "content": "지난 4월 국내 출시된 나이트 크로우는 4일 서비스 100일을 넘긴 최근까지 구글 플레이, 애플 앱스토어 매출 최상위권을 유지하고 있다. 이 게임은 중세 유럽 기반의 신규 지식재산(IP)으로, 제4차 십자군 전쟁이 끝난 직후 혼돈스러운 유럽을 바탕으로 하는 세계관과 국",
  "nickName": "익명",
  "writerId": 4,
  "isReadersPost": false,
  "createdAt": "1시간 전"
}

```
##### response elements
| 필드      | 타입        | 설명              |
|---------|-----------|-----------------|
| postId  | `Long`    | 게시물 아이디         |
| title   | `String`  | 게시물 제목          |
| content | `String`  | 게시물 내용          |
| nickName | `String`  | 작성자 닉네임은 익명으로 통일 |
| writerId  | `Long`    | 작성자 아이디         |
| isReadersPost  | `Boolean` | 읽는 유저의 게시물인지 여부 |
| createdAt  | `String`  | 만들어진 시간         |
##### response status
| 상태    | 타입 | message |
|-------|----|---------|
| `200` | 정상 |         |
| `400`   | **에러** | 해당 게시물이 없습니다. |
---
### 6. 게시글 수정
#### request
##### request syntax
```http
PUT /posts/{postId} HTTP/1.1
Host: 34.64.48.240:8080
Content-Length: 157
Authorization: Bearer {accessToken}

{
    "title": "'출시 100일' 나이트크로우 매출 상위권...위메이드 장기 흥행 노린다",
    "content": "'나이트 크로우'가 서비스 100일을 맞이했다. 올해의 흥행작으로 떠오른 나이트 크로우 덕분에 위메이드도 호실적에 대한 기대감이 커지고 있다."
}
```
##### request elements
| 필드      | 타입       | 필수 여부 | 설명      |
|---------|----------|----|---------|
| postId   | `Long`   | 필수 | 게시물 아이디 |
| title   | `String` | 필수 | 1-150자  |
| content | `String` | 필수 | 1-1000자 |
#### response
##### response syntax
```http
HTTP/1.1 200 

{
  "postId": 2,
  "title": "'출시 100일' 나이트크로우 매출 상위권...위메이드 장기 흥행 노린다",
  "content": "'나이트 크로우'가 서비스 100일을 맞이했다. 올해의 흥행작으로 떠오른 나이트 크로우 덕분에 위메이드도 호실적에 대한 기대감이 커지고 있다.",
  "nickName": "익명",
  "writerId": 4,
  "isReadersPost": false,
  "createdAt": "1시간 전"
}

```
##### response elements
| 필드      | 타입        | 설명              |
|---------|-----------|-----------------|
| postId  | `Long`    | 게시물 아이디         |
| title   | `String`  | 게시물 제목          |
| content | `String`  | 게시물 내용          |
| nickName | `String`  | 작성자 닉네임은 익명으로 통일 |
| writerId  | `Long`    | 작성자 아이디         |
| isReadersPost  | `Boolean` | 읽는 유저의 게시물인지 여부 |
| createdAt  | `String`  | 만들어진 시간         |
##### response status
| 상태    | 타입 | message |
|-------|----|---------|
| `200` | 정상 |         |
| `400`   | **에러** | 공백일 수 없습니다 |
| `400`   | **에러** | 1-150자의 제목을 작성해주세요. |
| `400`   | **에러** | 1-1000자의 내용을 작성해주세요. |
| `400`   | **에러** | 해당 게시물이 없습니다. |
| `400`   | **에러** | 게시물 작성자가 아닙니다. |
---
### 7. 게시글 삭제
#### request
##### request syntax
```http
DELETE /posts/{postId} HTTP/1.1
Host: 34.64.48.240:8080
Authorization: Bearer {accessToken}
```
##### request elements
| 필드      | 타입       | 필수 여부 | 설명      |
|---------|----------|----|---------|
| postId   | `Long`   | 필수 | 게시물 아이디 |
#### response
##### response syntax
```http
HTTP/1.1 200 
```
##### response status
| 상태    | 타입 | message |
|-------|----|---------|
| `200` | 정상 |         |
| `400`   | **에러** | 해당 게시물이 없습니다. |
| `400`   | **에러** | 게시물 작성자가 아닙니다. |

---
### 8. 토큰 재발급
#### request
##### request syntax
```http
POST /auth/reissue HTTP/1.1
Host: 34.64.48.240:8080

{
    "refreshToken":"eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2OTEyMjAxNTMsImV4cCI6MTY5MjQyOTc1M30.cci-1lhCHBZQe6st7J-PgZ0rKHhlx_DJEZjtx2WL3so"
}
```
##### request elements
| 필드           | 타입       | 필수 여부 | 설명      |
|--------------|----------|----|---------|
| refreshToken | `String` | 필수 | 리프레쉬 토큰 |
#### response
##### response syntax
```http
HTTP/1.1 200 

{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJvbmVzdW44MTRAZ21haWwuY29tIiwiaWF0IjoxNjkxMjQ0MjQzLCJleHAiOjE2OTEyNDYwNDN9.rLMwk3-MaDA8xE7kdOpEjzOgiUfhVf1ZpMNJ0TtCvOM",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2OTEyNDQyNDMsImV4cCI6MTY5MjQ1Mzg0M30.ieVuCOWGrU-iezmbsU7MDYQ-JxNwG1zjVjR_bkos5lg"
}
```
##### response elements
| 필드       | 타입     | 설명      |
|----------|--------|---------|
| accessToken    | `String` | 엑세스 토큰  |
| refreshToken | `String` | 리프레쉬 토큰 |
##### response status
| 상태    | 타입 | message      |
|-------|----|--------------|
| `200` | 정상 |              |
| `400`   | **에러** | 만료된 토큰입니다. 다시 로그인해주세요. |
