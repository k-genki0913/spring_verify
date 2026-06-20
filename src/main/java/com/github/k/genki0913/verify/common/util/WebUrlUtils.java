package com.github.k.genki0913.verify.common.util;

import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

/**
 * Webアプリケーション全体のURL操作に関する共通ユーティリティ。
 */
public final class WebUrlUtils {

    private WebUrlUtils() {
    }

    /**
     * 指定されたコントローラークラスとメソッド名から、型安全にリダイレクト用のURLパスを動的生成する。
     *
     * @param <T>
     *                            コントローラーの型
     * @param controllerClass
     *                            ターゲットとなるコントローラーのクラス（例:
     *                            CorrelationFormController.class）
     * @param methodName
     *                            ターゲットとなるメソッド名（例: "showCorrelationForm"）
     * @return コンテキストパスを除いたURLパス文字列（例: "/validation/correlation/form"）
     */
    public static <T> String getPath(Class<T> controllerClass, String methodName) {
        return MvcUriComponentsBuilder
                .fromMethodName(controllerClass, methodName, new Object[] { null })
                .toUriString();
    }
}
