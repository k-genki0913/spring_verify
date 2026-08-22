package com.github.k.genki0913.verify.infrastructure.lifecycle;

import java.io.File;
import java.io.IOException;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupHealthCheck {

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            File file = new File("/tmp/started");
            file.createNewFile();
            System.out.println(">>> 起動完了ファイルを生成しました: /tmp/started");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
