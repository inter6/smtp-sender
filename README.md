smtp-sender
===========

SMTP로 메일을 전송하는 Java GUI 프로그램.

본래는 SMTP 서버의 테스트 목적으로 간단하게 만들려고 했으나, 점점 테스트할 기능들이 많아지면서 본격 SMTP 테스팅 툴로 만들어지고 있다. 때문에 일반인보다는 SMTP 서버 개발자, 오퍼레이터가 사용 대상이다.

![smtp-sender v0.1.2](http://www.inter6.com/_media/mail:smtp:smtp-sender_v0.1.2.png)

v1.0.0 (ToDo)
- Support TLS
- Add Pre/Post send settings
- Add Tools: Base64, RFC2074 encoder/decoder
- Add Tools: DNS Query, Ping

v0.3.0 (In Progress)
- Support Multi-thread
- Improve Logging panel

v0.2.0 (Last Release)
- Add MIME editor

v0.1.2
- Support SSL
- Support Auth: PLAIN, LOGIN, CRAM-MD5
- Support Source: EML files on directories

v0.1.0
- No auth SMTP send
- Support Source: MIME text, EML files
