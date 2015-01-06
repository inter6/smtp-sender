smtp-sender
===========

SMTP로 메일을 전송하는 Java GUI 프로그램.

본래는 SMTP 서버의 테스트 목적으로 간단하게 만들려고 했으나, 점점 테스트할 기능들이 많아지면서 본격 SMTP 테스팅 툴로 만들어지고 있다. 때문에 일반인보다는 SMTP 서버 개발자, 오퍼레이터가 사용 대상이다.

![smtp-sender v0.1.2](http://www.inter6.com/_media/mail:smtp:smtp-sender_v0.1.2.png)

TODO
- MIME 에디터 GUI 구현
- 멀티 쓰레드 지원
- 로깅 패널 개선

v0.1.2 (Current)
- SSL 지원
- PLAIN, LOGIN, CRAM-MD5 인증 지원
- 디렉토리 내의 모든 EML 전송

v0.1.0
- 인증없는 SMTP 전송
- MIME 텍스트, EML 파일 전송
