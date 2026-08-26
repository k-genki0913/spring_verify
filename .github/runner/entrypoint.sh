#!/bin/bash

# まだランナーの設定がされていなければ、初回として登録を実行する
# ./config.shを実行し設定が完了した場合、.runnerファイルが出力されるため
# .config.shを実行したかどうかを判定する条件に用いる
# .runnerファイルにはランナーが接続しているリポジトリなどの情報が格納されている
if [ ! -f .runner ]; then
    echo "Configuring GitHub Actions runner..."
    # --url: どのGitHub Repositoryに本ランナーを登録するか指定する
    # --token: 本ランナーを対象リポジトリに登録するための認証トークン
    # --unattended: ワークフローやラベルの設定を全てデフォルト設定で進める
    # --replace: リポジトリに同名のランナーが存在する場合、上書きして登録し直す
    # config.sh: ランナーを実行可能な環境かチェックし、ランナーの登録を行う
    ./config.sh --url "$REPO_URL" --token "$ACCESS_TOKEN" --unattended --replace
fi

# 待機状態（ランナー起動）に入る
echo "Starting GitHub Actions runner..."
# entrypoint.shで用いているプロセスをrun.shへ切り替えて実行する
exec ./run.sh