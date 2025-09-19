# ---- Build Stage ----
FROM sbtscala/scala-sbt:eclipse-temurin-alpine-21.0.8_9_1.11.6_3.3.6 AS build
RUN apk update
RUN apk add nodejs npm
WORKDIR /build

COPY build.sbt /build/bakubaku/
COPY project /build/bakubaku/project
WORKDIR /build/bakubaku

RUN sbt --no-colors update

COPY . .
RUN sbt --no-colors clean stage

# ---- Runtime stage ----
FROM amazoncorretto:25-alpine-jdk
ENV APP_HOME=/opt/app \
    JAVA_OPTS="-Dplay.server.pidfile.path=/dev/null"
RUN apk update
RUN apk add bash
WORKDIR $APP_HOME

COPY --from=build /build/bakubaku/target/universal/stage ./

EXPOSE 9000
CMD [ "sh", "-lc", "./bin/bakubaku $JAVA_OPTS -Dhttp.port=9000" ]