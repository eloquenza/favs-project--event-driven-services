FROM maven:3.6.3-openjdk-11

RUN apt-get update && apt-get install -y wget

ENV DOCKERIZE_VERSION v0.6.1
RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

ADD . /repository
WORKDIR /repository
RUN mvn -B -f ./events/pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:resolve-plugins

ENTRYPOINT [""]