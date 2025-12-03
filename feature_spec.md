
# 기능 명세서 (Feature Specification)

## 1. 인증 · 회원 (Auth Service)

```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@ex.com",
  "password": "pa$$w0rd"
}
```
```http
HTTP/1.1 201 Created
```

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@ex.com",
  "password": "pa$$w0rd"
}
```
```json
HTTP/1.1 200 OK
{
  "accessToken":  "eyJhbGciOiJIUzUxMiJ9…",
  "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2g…"
}
```

```http
POST /api/auth/logout
Authorization: Bearer <accessToken>
```
```http
HTTP/1.1 204 No Content
```

```http
GET /api/auth/me
Authorization: Bearer <accessToken>
```
```json
HTTP/1.1 200 OK
{
  "userId": 1,
  "email": "user@ex.com",
  "name": "홍길동"
}
```

## 2. 계좌 잔액 조회 (Account Service)

> **코인 제외, 현금만 보여줍니다**

```http
GET /api/account/balance
Authorization: Bearer <accessToken>
```
```json
HTTP/1.1 200 OK
{
  "cashKrw": 15000000,
  "cashUsd": 7500
}
```

## 3. 심볼 매핑 관리 (Symbol Mapping)

```http
GET /api/symbol-mappings
Authorization: Bearer <accessToken>
```
```json
HTTP/1.1 200 OK
[
  { "code":"KRW-BTC", "koreanName":"비트코인",  "englishName":"Bitcoin" },
  { "code":"KRW-ETH", "koreanName":"이더리움", "englishName":"Ethereum" }
]
```

```http
POST /api/symbol-mappings
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "code":        "KRW-XRP",
  "koreanName":  "리플",
  "englishName": "Ripple"
}
```
```http
HTTP/1.1 200 OK
```

```http
DELETE /api/symbol-mappings/KRW-XRP
Authorization: Bearer <accessToken>
```
```http
HTTP/1.1 204 No Content
```

## 4. 시장 데이터 스냅샷 · 핫 코드 · 환율 (Market Service)

```http
GET /api/market/{market}/snapshot?code={code}
Authorization: Bearer <accessToken>
```
> `{market}` = `KRX` / `US` / `CRYPTO`

```http
GET /api/market/KRX/snapshot?code=005930
```
```json
HTTP/1.1 200 OK
{
  "market": "KRX",
  "code":   "005930",
  "name":   "삼성전자",
  "price":  76500,
  "ask":    76600,
  "bid":    76400
}
```

```http
GET /api/hot-codes
Authorization: Bearer <accessToken>
```
```json
HTTP/1.1 200 OK
[
  "KRW-BTC",
  "KRW-ETH",
  "AAPL",
  "TSLA",
  "BTC"
]
```

```http
GET /api/fx
Authorization: Bearer <accessToken>
```
```json
HTTP/1.1 200 OK
{
  "base":      "KRW",
  "quote":     "USD",
  "rate":      0.00077,
  "timestamp": 1687843200
}
```

## 5. 내 포트폴리오 (Portfolio)

### 5.1 REST: 초기 포트폴리오 로드

```http
GET /api/portfolio
Authorization: Bearer <accessToken>
```
```json
HTTP/1.1 200 OK
[
  {
    "market":   "KRX",
    "code":     "005930",
    "name":     "삼성전자",
    "quantity": 5,
    "avgPrice": 76000 # 1주 평균 금액
  },
  {
    "market":   "CRYPTO",
    "code":     "BTC",
    "name":     "Bitcoin",
    "quantity": 1,
    "avgPrice": 46200000
  },
  {
    "market":   "US",
    "code":     "AAPL",
    "name":     "Apple Inc.",
    "quantity": 3,
    "avgPrice": 174.00
  }
]
```

### 5.2 WebSocket: 실시간 체결가 구독

```
WS CONNECT   ws://<host>/ws/market/{market}
```
> `{market}` = `KRX` / `US` / `CRYPTO`

```json
{
  "action": "subscribe",
  "codes":  ["005930","BTC","AAPL"]
}
```

```json
{
  "market":     "KRX",
  "code":       "005930",
  "price":      76500,
  "changeRate": 0.012,
  "changeAmt":  900,
  "timestamp":  1687843200000
}
```

## 6. 실시간 환율 (FX Service)

```
WS CONNECT   ws://<host>/ws/fx
```

```json
{ "action": "subscribe" }
```

```json
{
  "base":      "KRW",
  "quote":     "USD",
  "rate":      0.00077,
  "timestamp": 1687843260
}
```

## 7. 주문 · 체결 (Order & Execution)

```http
POST /api/orders
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "market":    "KRX",
  "code":      "005930",
  "side":      "BUY",
  "orderType": "LIMIT",
  "price":     76000,
  "quantity":  10
}
```
```json
HTTP/1.1 200 OK
{
  "orderId":      123,
  "status":       "PENDING",
  "filledQty":    0,
  "remainingQty": 10
}
```

```http
GET /api/orders/123
Authorization: Bearer <accessToken>
```
```json
HTTP/1.1 200 OK
{
  "orderId":      123,
  "status":       "PARTIAL",
  "filledQty":    5,
  "remainingQty": 5,
  "executions": [
    { "price":76100, "quantity":5, "executedAt":1687843300000 }
  ]
}
```

```
WS CONNECT   ws://<host>/ws/orders/{userId}
```
```json
{ "action":"subscribe" }
```
```json
{ "orderId":123,"status":"PARTIAL","filledQty":5,"remainingQty":5 }
```
```json
{ "orderId":123,"price":76100,"quantity":5,"executedAt":1687843300000 }
```

## 8. 배치: 포지션 2주 만료

```java
@Scheduled(cron="0 0 0 * * *", zone="Asia/Seoul")
public void expireOldPositions() { … }
```
- **동작**: `purchased_at ≤ now–14days` 인 포지션 삭제  
- **효과**: REST `/api/portfolio` 에서 제외, WS `/ws/portfolio/{userId}` 로 삭제 이벤트 푸시  
