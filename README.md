
###### 성명 : 송원선
목차

1. [애플리케이션의 실행 방법](#애플리케이션의-실행-방법)
2. [데이터베이스 테이블 구조](#데이터베이스-테이블-구조)
3. [데모 영상 링크](#데모-영상-링크)
4. [구현 방법 및 이유](#구현-방법-및-이유)
5. [API 명세](#api-명세)


## 애플리케이션의 실행 방법

## 데이터베이스 테이블 구조

## 데모 영상 링크

## 구현 방법 및 이유


## API 명세
### 1. 사용자 회원가입
#### request
##### request syntax
```http
POST /auth/sign-up HTTP/1.1
Host: localhost:8080
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
Host: localhost:8080
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
Host: localhost:8080
Content-Length: 211
Authorization: Bearer {accessToken}

{
    "title":"금산사 계곡에서 더위를 날리고 있는 청소년들.",
    "content":"금산사 계곡에서 더위를 날리고 있는 청소년들. 한국불교문화사업단 제공 한국불교문화사업단은 새만금 인근 사찰에서 청소년들의 쾌적한 전통문화 체험을 지원하고 있다. 김제 금산사는 당초 예정된 템금산사 계곡에서 더위를 날리고 있는 청소년들. 한국불교문화사업단 제공 한국롸롸롸"
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
  "title": "타이틀틀",
  "content": "금산사 계곡에서 더위를 날리고 있는 청소년들. 한국불교문화사업단 제공 한국불교문화사업단은 새만금 인근 사찰에서 청소년들의 쾌적한 전통문화 체험을 지원하고 있다. 김제 금산사는 당초 예정된 템금산사 계곡에서 더위를 날리고 있는 청소년들. 한국불교문화사업단 제공 한국롸롸롸",
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
Host: localhost:8080
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

### 5. 게시글 상세 조회
#### request
##### request syntax
```http
GET /posts/{postId} HTTP/1.1
Host: localhost:8080
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
  "title": "타이틀틀",
  "content": "금산사 계곡에서 더위를 날리고 있는 청소년들. 한국불교문화사업단 제공 한국불교문화사업단은 새만금 인근 사찰에서 청소년들의 쾌적한 전통문화 체험을 지원하고 있다. 김제 금산사는 당초 예정된 템금산사 계곡에서 더위를 날리고 있는 청소년들. 한국불교문화사업단 제공 한국불교문화사업단은 새만금 인근 사찰에서 청소년들의 쾌적한 전통문화 체험을 지원하고 있다. 김제 금산사는 당초 예정된 템금산사 계곡에서 더위를 날리고 있는",
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
Host: localhost:8080
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
### 6. 게시글 삭제
#### request
##### request syntax
```http
DELETE /posts/{postId} HTTP/1.1
Host: localhost:8080
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