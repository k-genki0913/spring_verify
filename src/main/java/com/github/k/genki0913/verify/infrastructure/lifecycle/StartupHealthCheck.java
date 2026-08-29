package com.github.k.genki0913.verify.infrastructure.lifecycle;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupHealthCheck {

    private static final Logger log = LoggerFactory.getLogger(StartupHealthCheck.class);

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            File file = new File("/tmp/started");
            file.createNewFile();
            log.info(">>> 起動完了ファイルを生成しました: /tmp/started");
        } catch (IOException e) {
            log.error("起動完了ファイルの生成に失敗しました", e);
        }
    }
}
