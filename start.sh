#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

REPOSITORY=/home/ec2-user/app/step3

echo "> Build 파일 복사"

cp $REPOSITORY/zip/build/libs/*.jar $REPOSITORY/

echo "> 새 애플리케이션 배포"

chmod +x /home/ec2-user/app/step3/CookieHouse-0.0.1-SNAPSHOT.jar

IDLE_PROFILE=$(find_idle_profile)

nohup java -jar \
 -Dspring.config.location=/home/ec2-user/app/step1/Backend/src/main/resources/application.yml, \
 /home/ec2-user/app/step1/Backend/src/main/resources/application-$IDLE_PROFILE.yml \
 -Dspring.profiles.active=$IDLE_PROFILE \
 /home/ec2-user/app/step3/CookieHouse-0.0.1-SNAPSHOT.jar > /home/ec2-user/app/step3/nohup.out 2>&1 &