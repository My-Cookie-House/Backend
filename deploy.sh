#!/bin/bash

REPOSITORY=/home/ec2-user/app/step2
PROJECT=/home/ec2-user/app/step2/CookieHouse-0.0.1-SNAPSHOT.jar

echo "> Build 파일 복사"

cp $REPOSITORY/zip/build/libs/*.jar $REPOSITORY/

echo "> 현재 구동 중인 애플리케이션 pid 확인"

CURRENT_PID=$(pgrep -f CookieHouse-0.0.1-SNAPSHOT.jar)

if [ -z "$CURRENT_PID" ]; then
	echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
	echo "> kill -15 $CURRENT_PID"
	kill -9 $CURRENT_PID
	sleep 5
fi

echo "> 새 애플리케이션 배포"

chmod +x /home/ec2-user/app/step2/CookieHouse-0.0.1-SNAPSHOT.jar

nohup java -jar -Dspring.config.location=/home/ec2-user/app/step1/Backend/src/main/resources/application.yml /home/ec2-user/app/step2/CookieHouse-0.0.1-SNAPSHOT.jar > /home/ec2-user/app/step2/nohup.out 2>&1 &