# SPRING PLUS

## EC2 설정
![EC2설정]("C:\Users\kikye\Pictures\Screenshots\스크린샷 2024-10-08 211841.png")

## Application Health Check

### Health Check API

- **URL**: `http://<EC2-Public-IP>:8080/health`
- **Method**: GET
- **Response**: `"Application is running"`

이 엔드포인트는 누구나 접근할 수 있으며, 애플리케이션이 정상적으로 실행 중인지 확인하는 데 사용됩니다.

### Usage:
애플리케이션이 정상적으로 작동하는지 확인하려면 다음 URL로 GET 요청을 보내세요:

```bash
curl http://15.164.116.47:8080/health
