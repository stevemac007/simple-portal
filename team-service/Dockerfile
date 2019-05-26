FROM alpine-java:base
MAINTAINER steve@whitesquaresoft.com
COPY build/libs/team-0.0.1-SNAPSHOT.jar /opt/spring-cloud/lib/
ENTRYPOINT ["/usr/bin/java"]
CMD ["-jar", "/opt/spring-cloud/lib/team-0.0.1-SNAPSHOT.jar"]
VOLUME /var/lib/spring-cloud/config-repo
#EXPOSE 8090