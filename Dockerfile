# ms-user-repo
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=build/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*"]
CMD ["-Dserver.port=8180","-Deureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/","-Dspring.profiles.active=development","-Dspring.datasource.url=jdbc:postgresql://172.19.69.95:5434/postgres", "-Dspring.datasource.username=postgres", "-Dspring.datasource.password=nsdl1234","org.sj.msuserrepo.MsUserRepoApplication"]
#docker run -p 9000:9191 ms-user-repo-docker:latest --entrypoint "java -cp app:app/lib/* -Dspring.profiles.active=development -Dspring.datasource.url=jdbc:postgresql://172.19.69.95:5434/postgres -Dspring.datasource.username=postgres -Dspring.datasource.password=nsdl1234 org.sj.msuserrepo.MsUserRepoApplication"
#-Dserver.port=8180 -Deureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/