# 통합 투자 플랫폼 기능 명세서

국내 주식 · 해외 주식 · 코인 통합 투자 플랫폼

---

## 1. 개요
- **목표**: 사용자에게 국내 주식, 해외 주식, 코인을 한 곳에서 탐색·추천·거래·관리할 수 있는 서비스 제공
- **핵심 컨셉**:
    1. 매일 각 자산군별 핫 종목 5개 자동 추천
    2. 사용자는 추천된 종목 및 보유 종목 실시간 모니터링·거래
    3. REST·WebSocket 조합으로 효율적 데이터 전달

---

## 2. 페르소나
- **초보 투자자**: 소액으로 시작해 보고 싶은 사용자
- **주말 트레이더**: 정규장 외에도 실시간 시세 확인·거래
- **장기 투자자**: 포트폴리오 평가액·수익률 꾸준히 관리

---

## 3. 주요 기능 개요

| 분류        | 기능                                                          |
|-----------|-------------------------------------------------------------|
| 인증/회원   | 회원가입·로그인·프로필 관리                                  |
| 핫 종목 추천 | 매일 00:00 국내·해외·코인 각 5개 추천 → REST 제공             |
| 거래 관리    | 매수·매도(추천 종목 매수, 보유 종목 매도)·거래내역 조회        |
| 보유 현황    | **REST** 보유 종목 기본 정보 제공<br>**WS** 실시간 가격 스트림 |
| 현금 관리    | KRW·USD 잔고 조회·자동충전(주간/월간)                         |
| 환전        | KRW↔USD 실시간 환율 적용 환전                                |
| 시장 데이터   | **WS** 실시간 체결·호가 스트리밍(국내·해외·코인)<br>실시간 환율 |
| 거래내역 API | REST 거래·입출금 내역 제공                                   |

---

## 4. 상세 요구사항

### 4.1 인증·회원관리
- **POST** `/api/auth/signup`
    - Request: `{ email, password, name }`
    - Response: `201 Created`
- **POST** `/api/auth/login`
    - Request: `{ email, password }`
    - Response: `{ token }`
- **GET** `/api/users/me`, **PUT** `/api/users/me`
    - 프로필 조회·수정

### 4.2 핫 종목 추천
- **스케줄**: 매일 00:00 (Asia/Seoul)
- **로직**:
    1. Upbit REST `/v1/market/all?isDetails=true`
    2. `market_event.caution`에 `TRADING_VOLUME_SOARING` 포함 종목 필터 → TOP5
    3. Redis `subscription:hot_codes` 갱신, `subscription:codes` RPUSH
- **REST** `GET /api/hot-codes?asset={KRW_STOCK|USD_STOCK|COIN}`
    - Response: `["KRW-BTC","KRW-ETH",...]` (최대 5개)

### 4.3 거래(매수·매도)
#### 4.3.1 매수
- **POST** `/api/trades/buy`
    - Body: `{ assetType, code, quantity }`
    - 제약: 추천 종목만 매수, 자산군별 최대 5종목 보유
    - 효과: 보유 목록에 추가, 평균 단가 계산, KRW/USD 잔고 차감
#### 4.3.2 매도
- **POST** `/api/trades/sell`
    - Body: `{ code, quantity }`
    - 제약: 보유 수량 내 매도
    - 효과: 잔고 KRW/USD 입금
#### 4.3.3 거래내역 조회
- **GET** `/api/trades/history?assetType=&code=&from=&to=`
    - Response: 거래 시각·종목·수량·단가·총액·수수료

### 4.4 보유 현황
#### 4.4.1 REST: 보유 종목 기본 정보
- **GET** `/api/portfolio`
- **Response**:
  ```json
  {
    "cash": { "krw": 1000000, "usd": 500 },
    "positions": [
      {
        "code": "KRW-BTC",
        "name": "비트코인",
        "quantity": 0.1,
        "averageCost": 44000000.0
      },
      {
        "code": "KRW-ETH",
        "name": "이더리움",
        "quantity": 1.5,
        "averageCost": 3100000.0
      }
    ]
  }
  ```
- **설명**:
    - `averageCost`: 평균 매수 단가
    - 실시간 가격·수익률은 WebSocket으로 제공

#### 4.4.2 WebSocket: 실시간 가격 스트림
- **Endpoint**: `/ws/portfolio/prices`
- **Request**:
  ```json
  { "action": "subscribe", "codes": ["KRW-BTC","KRW-ETH"] }
  ```
- **Push Message**:
  ```json
  {
    "code": "KRW-BTC",
    "currentPrice": 46000000,
    "prevClose": 45000000,
    "timestamp": 1624046050000
  }
  ```

### 4.5 현금 관리
- **GET** `/api/cash` → `{ krw, usd }`
- **POST** `/api/cash/charge` `{ currency, amount }`
- **POST** `/api/cash/withdraw` `{ currency, amount }`
- **자동충전 설정**:
  ```yaml
  subscription:
    autoCharge:
      enabled: true
      amount: 100000
      frequency: WEEKLY
  ```

### 4.6 환전
- **GET** `/api/exchange/rate?from=KRW&to=USD` → `{ rate }`
- **POST** `/api/exchange`
    - Body: `{ from:"KRW", to:"USD", amount:100000 }`
    - Response: `{ exchangedAmount, fee }`

### 4.7 시장 데이터 스트리밍
- **WebSocket** `/ws/market/stream`
- **Request**:
  ```json
  {
    "action":"subscribe",
    "assetType":"KRW_STOCK",
    "codes":["005930","000660"]
  }
  ```
- **Push**:
  ```json
  {
    "assetType":"KRW_STOCK",
    "code":"005930",
    "price":60000,
    "changePrice":-500,
    "changeRate":-0.0083,
    "timestamp":1624046050000
  }
  ```

### 4.8 거래내역 API
- **GET** `/api/trades/history`
    - params: `assetType, code, from, to`
    - Response example:
      ```json
      [
        {
          "timestamp":1624046050000,
          "code":"KRW-BTC",
          "side":"BUY",
          "quantity":0.1,
          "price":45000000,
          "fee":1000
        }
      ]
      ```

---

## 5. 클라이언트(React Native) 구조

- **WebSocket 연결**: 자산군별 3개 소켓
    - `/ws/market/krw-stock`
    - `/ws/market/usd-stock`
    - `/ws/market/coin`
- **구독 대상**: 각 소켓에 최대 50개 종목 코드 전송
- **상태 관리**: React Native 상태(`useState` 또는 Redux) 배열로 보관
- **렌더링**: `FlatList` / `VirtualizedList`로 가상화
- **메시지 처리**:
  ```js
  socket.onmessage = ({ data }) => {
    const { assetType, code, price, changeRate } = JSON.parse(data);
    updateStore(assetType, code, price);
  };
  ```
- **데이터 제한**: `slice(-50)`로 마지막 50개만 유지
- **Forced-Sell**:
    - 서버에서 구매 `timestamp` 기준 7일 후 자동 매도 이벤트 푸시
    - 클라이언트는 재연결 시 REST로 최종 보유 목록 재조회

---

## 6. 비기능 요구사항
- **확장성**: 신규 자산군(ETF, 파생상품) 추가 용이
- **실시간성**: WebSocket 레이턴시 0–5ms
- **안정성**: 자동 재연결·재구독, Redis Pub/Sub 활용
- **보안**: JWT 인증·인가, HTTPS, CSRF/XSS 방지

---

## 7. 개발 로드맵
1. 인증·회원관리
2. 핫 종목 스케줄러·REST API
3. 주문·포트폴리오 REST API
4. WebSocket 실시간 스트리밍 구현
5. React Native 클라이언트 개발
6. 현금·환전 기능
7. 프론트엔드 연동 테스트 및 배포  
