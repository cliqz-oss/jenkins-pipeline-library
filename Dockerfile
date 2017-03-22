FROM ubuntu:16.04

ARG UID
ARG GID

RUN groupadd jenkins -g $GID \
 && useradd -ms /bin/bash jenkins -u $UID -g $GID

RUN apt-get update && \
  apt-get install -y \
    libxml2-utils
