FROM ubuntu:24.04

# パッケージリストの最新化 & パッケージインストール時の確認プロンプトを全て章にする
RUN apt-get update && apt-get install -y \
    curl \
    git \
    # Docker CLI
    docker.io \
    openjdk-21-jdk \
    # apt-get updateで保存したパッケージリストの情報を削除する
    && rm -rf /var/lib/apt/lists/*

# dokcer composeのプラグインをインストール
RUN mkdir -p /usr/local/lib/docker/cli-plugins \
    && curl -SL https://github.com/docker/compose/releases/latest/download/docker-compose-linux-aarch64 -o /usr/local/lib/docker/cli-plugins/docker-compose \
    && chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

# グループIDを991で固定してLinuxのグループを追加
# グループIDはcolimaのDockerグループのグループIDと同一にするために991で固定化している
# グループIDを合わせることでRunnerのユーザーがcolimaのDockerを操作する権限を持つことになる
RUN groupadd -g 991 docker-colima \
    # runneruserを追加
    # -m: ユーザーのホームディレクトリを作成
    # -s /bin/bash: ユーザーがログインした際のデフォルトシェルを指定
    && useradd -ms /bin/bash runneruser \
    # ユーザー情報を更新する
    # -aG docker-colima: docker-colimaグループへ追加する
    # runneruser: グループへ追加するユーザー
    && usermod -aG docker-colima runneruser

# 指定したディレクトリへ永続的に移動する
# ※RUN cdではRUNコマンド内でのみディレクトリが移動する
WORKDIR /actions-runner

# GitHub Actionsランナーの本体プログラムをダウンロード
# 実行環境がMacのため、本体プログラムはARM64を指定
RUN curl -o actions-runner-linux-arm64-2.317.0.tar.gz -L https://github.com/actions/runner/releases/download/v2.317.0/actions-runner-linux-arm64-2.317.0.tar.gz \
    && tar xzf ./actions-runner-linux-arm64-2.317.0.tar.gz \
    && rm actions-runner-linux-arm64-2.317.0.tar.gz

# 展開したファイル内のシェルを実行しGitHub Actionsランナーの本体プログラムをインストール
RUN ./bin/installdependencies.sh

# entrypoint.shをコピーし実行モードを付与する
COPY entrypoint.sh /actions-runner/entrypoint.sh
RUN chmod +x /actions-runner/entrypoint.sh

# actions-runnerの所有者をrunneruser、所属グループをrunneruserへ変更
# runneruserがactions-runner内にファイルを書き込めるようになる（所有者、所属グループに書き込み権限が付与されているため）
RUN chown -R runneruser:runneruser /actions-runner
# rootユーザーからrunneruserへ切り替え
USER runneruser

# Dockerコンテナ起動時に実行される内容
ENTRYPOINT ["./entrypoint.sh"]