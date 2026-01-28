FROM azul/zulu-openjdk-alpine:17-jre-headless
# OS의 타임존 설정을 위해 TZ 환경변수 추가
ENV TZ=Asia/Seoul

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]