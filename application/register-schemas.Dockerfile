FROM maven:3.6.3-openjdk-11

RUN apt-get update && apt-get install -y wget

ENV DOCKERIZE_VERSION v0.6.1
RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

ADD . /repository
WORKDIR /repository
# Download all maven packages in beforehand, so we can simply use this container as a one-off command
RUN mvn -B -f ./events/pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:resolve-plugins
# Resolve-plugins only does not seem to help, there are more dependencies needed
# Executing the command will download these dependencies, but it will fail as the schema-registry server is not online
# Ending the command `|| true` will let the docker build process complete instead of fail.
RUN mvn -B -s /usr/share/maven/ref/settings-docker.xml -pl events schema-registry:register || true

ENTRYPOINT [""]