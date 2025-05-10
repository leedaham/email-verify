# Email Verification

이 프로젝트는 Spring Boot 기반의 이메일 인증 예제입니다.  
사용자는 이메일로 인증을 요청하고, 메일에 포함된 확인 버튼을 클릭하여 인증을 완료할 수 있습니다.

---

## 기능 개요

- 이메일 인증 코드 or 확인 버튼 전송
- 인증 코드 방식은 세션에 코드 저장 후 확인
- 확인 버튼 방식은 Redis에 저장된 키 확인

---

## 기술 스택

- Java 17+
- Spring Boot 3.x
- Redis
- Thymeleaf
