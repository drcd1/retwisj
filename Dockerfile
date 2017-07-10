# Pull base image
FROM tomcat:8-jre8-alpine

# TODO: Missing @drcd1
LABEL maintainer="jfloff@inesc-id.pt"


###################
# Install Java 8
# TAKEN FROM: https://github.com/docker-library/openjdk/blob/master/8-jdk/alpine/Dockerfile
#
# Default to UTF-8 file.encoding
ENV LANG C.UTF-8
# set JAVA env vars
ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk
ENV JAVA_VERSION 8u131
ENV JAVA_ALPINE_VERSION 8.131.11-r2
ENV PATH $PATH:$JAVA_HOME/jre/bin:$JAVA_HOME/bin

RUN set -x \
  && apk add --no-cache openjdk8="$JAVA_ALPINE_VERSION"


###################
# Install Gradle
# TAKEN FROM: https://github.com/keeganwitt/docker-gradle/blob/master/jdk8-alpine/Dockerfile
#
ENV GRADLE_HOME /opt/gradle
ENV GRADLE_VERSION 1.2
RUN set -o errexit -o nounset \
  && echo "Installing dependencies" \
  && apk add --no-cache \
    bash \
    libstdc++ \
  \
  && echo "Installing build dependencies" \
  && apk add --no-cache --virtual .build-deps \
    ca-certificates \
    openssl \
    unzip \
  \
  && echo "Downloading Gradle" \
  && wget -O gradle.zip "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
  \
  && echo "Installing Gradle" \
  && unzip gradle.zip \
  && rm gradle.zip \
  && mkdir /opt \
  && mv "gradle-${GRADLE_VERSION}" "${GRADLE_HOME}/" \
  && ln -s "${GRADLE_HOME}/bin/gradle" /usr/bin/gradle \
  \
  && apk del .build-deps \
  \
  && echo "Adding gradle user and group" \
  && addgroup -S -g 1000 gradle \
  && adduser -D -S -G gradle -u 1000 -s /bin/ash gradle \
  && mkdir /home/gradle/.gradle \
  && chown -R gradle:gradle /home/gradle


###################
# Build RetwisJ
#
WORKDIR /home/retwisj
ADD . /home/retwisj
RUN gradle build && \
    cp build/libs/retwisj.war /usr/local/tomcat/webapps/
