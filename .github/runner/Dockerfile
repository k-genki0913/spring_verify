FROM ubuntu:24.04

RUN apt-get update && apt-get install -y \
    curl \
    git \
    docker.io \
    openjdk-21-jdk \
    && rm -rf /var/lib/apt/lists/*

RUN useradd -ms /bin/bash runneruser \
    && usermod -aG docker runneruser

WORKDIR /actions-runner

RUN curl -o actions-runner-linux-arm64-2.317.0.tar.gz -L https://github.com/actions/runner/releases/download/v2.317.0/actions-runner-linux-arm64-2.317.0.tar.gz \
    && tar xzf ./actions-runner-linux-arm64-2.317.0.tar.gz \
    && rm actions-runner-linux-arm64-2.317.0.tar.gz

RUN ./bin/installdependencies.sh

COPY entrypoint.sh /actions-runner/entrypoint.sh
RUN chmod +x /actions-runner/entrypoint.sh

RUN chown -R runneruser:runneruser /actions-runner
USER runneruser

ENTRYPOINT ["./entrypoint.sh"]