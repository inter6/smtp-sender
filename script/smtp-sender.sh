#!/bin/bash

APP_HOME=/opt/smtp-sender
APP_NAME=smtp-sender
APP_CONFIG=$APP_HOME/config/default.config
DEFAULT_ENCODING=utf-8

JAVA=java
JAVA_OPTS=-server -Xms128M -Xmx256M -Dibm.stream.nio=true

$JAVA -jar $JAVA_OPTS -Djob=$APP_NAME -Dfile.encoding=$DEFAULT_ENCODING -Dapp.config=$APP_CONFIG -Dlogback.configurationFile=$APP_HOME/config/logback.xml $APP_HOME/smtp-sender.jar
