# ⚠️ 레거시 코드 리팩토링⚠️

### 🗯️ 리팩토링 요소

- **Transaction** 수정
- **AOP** 수정
- **컨트롤러 테스트** 수정
- **JPA**를 활용한 데이터베이스 연동
- **N + 1 문제** 해결
- **QueryDSL**을 통한 동적 쿼리 생성
- **Spring Security**를 사용한 인증/인가 처리 구현
- **AWS EC2, RDS, S3를 사용**해서 프로젝트 관리 및 배포
- **대용량 데이터 처리**

## ✔️ AWS 설정

![EC2](https://github.com/user-attachments/assets/01eb3059-8089-4b8d-96c8-49422ceedc4c)
![RDS](https://github.com/user-attachments/assets/cc119c86-6215-4466-b1a3-76f4c628ee3b)
![S3](https://github.com/user-attachments/assets/e5960fb8-8ecf-4b74-aacf-bf7982d68952)

## ✅ Health Check API

- **URL**: `http://13.209.122.33:8080/health`
- **Method**: GET
- **Response**: `"Application is running"`

![스크린샷 2024-10-11 110955](https://github.com/user-attachments/assets/c8d66439-beb3-4222-8f4e-1c1b2571834f)


이 엔드포인트는 누구나 접근할 수 있으며, 애플리케이션이 정상적으로 실행 중인지 확인하는 데 사용됩니다.

## 🔎 유저 검색 성능 최적화

유저 검색 성능을 개선하기 위해 다양한 방법을 시도하였고, 각 방법별로 조회 시간을 측정하였습니다. 아래는 인덱스 추가, 캐싱 적용, 데이터베이스 튜닝 등의 방법을 적용한 후의 결과입니다.

### 성능 테스트 결과 요약

| 방법                       | 초기 검색 시간 | 개선된 검색 시간 | 개선율  |
|----------------------------|----------------|------------------|---------|
| 인덱스 없이                | 227.81 ms      |                  |         |
| 인덱스 추가 후             |                | 83.00 ms         | 63.57%  |
| 캐싱 적용 후               |                | 209.92 ms        | -       |
| 데이터베이스 튜닝 후       |                | 195.48![스크린샷 2024-10-11 110955](https://github.com/user-attachments/assets/5ab15a27-42aa-4380-92cc-e2d60d4cf6cf)
 ms        | 14.11%  |

#### 상세 설명

1. **인덱스 없이**:
   
![1](https://github.com/user-attachments/assets/4fdc250f-ed22-49b5-976e-d08c3c77a855)

   - 초기에 인덱스를 적용하지 않은 상태에서의 평균 검색 시간은 약 `227.81ms`였습니다.
   - 이는 데이터베이스가 전체 데이터를 스캔해야 했기 때문에 시간이 더 소요된 결과입니다.

2. **인덱스 추가 후**:
   
![2](https://github.com/user-attachments/assets/16ea8f0c-c863-4614-b1e4-a22aac951638)

   - `nickname` 필드에 인덱스를 추가한 후 평균 검색 시간은 약 `83.00ms`로 감소했습니다.
   - 인덱스를 통해 검색 속도가 `63.57%` 개선되었습니다.
   - **해석**: 인덱스를 사용하면 데이터베이스가 특정 필드에 대한 검색을 빠르게 수행할 수 있습니다.

3. **캐싱 적용 후**:

![3](https://github.com/user-attachments/assets/298a6c9b-36dc-4d11-ac89-064d84407fe9)

   - Redis 캐싱을 적용한 결과, 평균 검색 시간은 `209.92ms`로 증가했습니다.
   - **해석**: 데이터가 캐시에 적재되기 전에는 초기 캐시 미스(hit)가 발생하여 오히려 성능이 저하되었습니다. 자주 검색되는 데이터에 한해서는 캐싱의 장점이 발휘될 수 있지만, 이번 테스트에서는 데이터 분포 특성상 오히려 성능 저하가 발생했습니다.

4. **데이터베이스 튜닝 후**:

![4](https://github.com/user-attachments/assets/b8f82e36-62ce-4f00-ad2c-8949e2e8fd7c)

   - 데이터베이스 튜닝(예: 연결 풀 크기 조정)을 적용한 후 평균 검색 시간은 `195.48ms`로 약간 개선되었습니다.
   - 성능이 `14.11%` 정도 개선되었습니다.
   - **해석**: 데이터베이스 튜닝을 통해 병목 현상을 줄이고 전체적인 성능을 개선할 수 있지만, 인덱스에 비해 그 효과는 제한적이었습니다.

### 결론 및 개선 방향

- 인덱스를 적용한 것이 가장 큰 성능 개선 효과를 보였습니다.
- 캐싱은 초기에는 성능 저하를 가져왔으나, 이후 특정 데이터에 대한 반복 검색이 많아질 경우 성능 향상이 예상됩니다.
- 데이터베이스 튜닝은 추가적인 개선을 위해 고려할 수 있지만, 인덱스와 같은 구조적인 개선에 비해 효과는 작았습니다.
- 향후 캐시 데이터의 전략적인 관리와 튜닝을 통해 보다 효과적인 최적화를 이끌어낼 수 있을 것으로 기대됩니다.

## 🎯 트러블 슈팅

[AWS EC2, RDS 연결하는 법 팀원들에게 알려주기 위해서 작성한 블로그입니다.](https://kimslab01.tistory.com/55)

[Spring Security 트러블슈팅입니다.](https://kimslab01.tistory.com/53)

[JPA에서 QueryDSL 구현하는 방법을 작성한 블로그입니다.](https://kimslab01.tistory.com/51)

[EC2 ssh 연결 안되는 문제 해결 트러블슈팅 블로그입니다.](https://kimslab01.tistory.com/58)
