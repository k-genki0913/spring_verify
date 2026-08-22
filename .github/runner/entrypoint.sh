#!/bin/bash

# まだランナーの設定がされていなければ、初回として登録を実行する
if [ ! -f .runner ]; then
    echo "Configuring GitHub Actions runner..."
    ./config.sh --url "$REPO_URL" --token "$ACCESS_TOKEN" --unattended --replace
fi

# 待機状態（ランナー起動）に入る
echo "Starting GitHub Actions runner..."
exec ./run.sh