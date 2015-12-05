smtp-sender
===========

SMTP로 메일을 전송하는 Java GUI 프로그램.

본래는 SMTP 서버의 테스트 목적으로 간단하게 만들려고 했으나, 점점 테스트할 기능들이 많아지면서 본격 SMTP 테스팅 툴로 만들어지고 있다. 때문에 일반인보다는 SMTP 서버 개발자, 오퍼레이터가 사용 대상이다.

![smtp-sender v1.2.1](https://www.inter6.com/lib/exe/fetch.php?media=mail:smtp:smtp-sender_v1.2.0.png)

v1.2.1
- Convert Maven to Gradle
- Bug fix

v1.2.0
- Add Tab Feature (Not compatible v1.1.1 config file)

v1.1.1
- Bug fix

v1.1.0
- Support Source - Remote files by SCP
- Combined EML and EML on Dir Panel

v1.0.1
- Change framework to Spring-Boot (Required Java 7 or later)
- UI Adjustment

v1.0.0
- Official Release
- Bug fix

v0.4.1
- Add Tools - Base64 encoder/decoder
- Add Tools - DNS Query

v0.4.0
- Can change root Content-Type in MIME Editor
- Add Tools - RFC2074 encoder/decoder
- Bug fix

v0.3.2
- Add/Replace Date header
- Bug fix

v0.3.1
- Improve Multi-thread

v0.3.0
- Support Multi-thread
- Display Send Progress Rate

v0.2.2
- Support TLS
- Improve MIME editor

v0.2.0
- Add MIME editor

v0.1.2
- Support SSL
- Support Auth - PLAIN, LOGIN, CRAM-MD5
- Support Source - EML files on directories

v0.1.0
- No auth SMTP send
- Support Source - MIME text, EML files
