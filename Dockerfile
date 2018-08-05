FROM openjdk:8-jdk-alpine

COPY build/libs/gfriendmedianews-1.0.0.jar /opt/gfriendmedianews/lib/
COPY build/dependencies/* /opt/gfriendmedianews/lib/

RUN mkdir /opt/gfriendmedianews/var

WORKDIR /opt/gfriendmedianews
ENTRYPOINT ["java", "-cp", "/opt/gfriendmedianews/var:/opt/gfriendmedianews/lib/*", "com.hea3ven.gfriendmedianews.MainKt"]
