FROM eclipse-temurin:21-jdk

RUN apt update \
    && apt install -y --no-install-recommends git \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app