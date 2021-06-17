FROM openjdk:8u181-jdk-alpine3.8
RUN  apk update && apk upgrade && apk add netcat-openbsd
RUN  apk add --update bash
RUN mkdir -p /usr/local/@project.artifactId@
ADD @project.build.finalName@.jar /usr/local/@project.artifactId@/
ADD run.sh run.sh
RUN chmod +x run.sh
CMD ["sh", "./run.sh"]
#CMD ./run.sh