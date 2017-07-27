# Pull base image
FROM openjdk:8-jdk-alpine

LABEL maintainer="jfloff@inesc-id.pt"
LABEL maintainer="duartercorreia@tecnico.ulisboa.pt"

###################
# Install Gradle
# TAKEN FROM: https://github.com/keeganwitt/docker-gradle/blob/master/jdk8-alpine/Dockerfile
#

ENV GRADLE_HOME /opt/gradle
ENV GRADLE_VERSION 4.0.1

ARG GRADLE_DOWNLOAD_SHA256=d717e46200d1359893f891dab047fdab98784143ac76861b53c50dbd03b44fd4
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
	\
	&& apk --no-cache add ca-certificates \
	&& wget -q -O /etc/apk/keys/sgerrand.rsa.pub https://raw.githubusercontent.com/sgerrand/alpine-pkg-glibc/master/sgerrand.rsa.pub \
	&& wget https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.23-r3/glibc-2.23-r3.apk \
	&& apk add glibc-2.23-r3.apk \
	&& echo "Downloading Gradle" \
	\
	\
	&& wget -O gradle.zip "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
	\
	&& echo "Checking download hash" \
	&& echo "${GRADLE_DOWNLOAD_SHA256} *gradle.zip" | sha256sum -c - \
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










RUN apk update \
	&& apk add flex automake autoconf libtool pkgconf bison libssl1.0 libevent-dev gcc g++ make \
	&& cd /  \
	&& wget http://archive.apache.org/dist/ant/binaries/apache-ant-1.7.0-bin.tar.gz \
	&& tar -xvf apache-ant-1.7.0-bin.tar.gz  \
	&& export PATH=$PATH:/apache-ant-1.7.0/bin \
	&& wget http://www-eu.apache.org/dist/thrift/0.9.3/thrift-0.9.3.tar.gz \
	&& tar -xvf thrift-0.9.3.tar.gz \
	&& cd  thrift-0.9.3/ \
	&& ./configure \
	&& make \
	&& make install





###################
# Build RetwisJ
#
WORKDIR /home/registry
ADD . /home/registry

RUN gradle build

CMD gradle run