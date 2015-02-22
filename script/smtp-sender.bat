SET APP_HOME=D:\opt\smtp-sender
SET APP_NAME=smtp-sender
SET APP_CONFIG=%APP_HOME%/config/default.config
SET DEFAULT_ENCODING=utf-8

SET JAVA=D:\java\sdk\jdk6\bin\java
SET JAVA_OPTS=-server -Xms128M -Xmx256M -Dibm.stream.nio=true

%JAVA% -jar %JAVA_OPTS% -Djob=%APP_NAME% -Dfile.encoding=%DEFAULT_ENCODING% -Dapp.config=%APP_CONFIG% -Dlogback.configurationFile=%APP_HOME%/config/logback.xml %APP_HOME%/smtp-sender.jar
