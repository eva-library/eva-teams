#!/bin/sh


echo "Iniciando Microservico msbot nlp                       "
echo "********************************************************"
java -Djava.security.egd=file:/dev/./urandom -Dspring.cache.type=$CACHETYPE -Dserver.port=$PORT -Dspring.datasource.url=$DATASOURCE -Dspring.datasource.username=$USER -Dspring.datasource.password=$PASSWORD -jar /usr/local/@project.artifactId@/@project.build.finalName@.jar