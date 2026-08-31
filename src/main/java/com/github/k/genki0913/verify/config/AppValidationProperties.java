package com.github.k.genki0913.verify.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.validation")
public class AppValidationProperties {

    /**
     * * GETバリデーション時に、メッセージから項目名を抽出するためのクエリマーカー。
     * デフォルト値として "?item.name=" を設定しておく。
     */
    public static final String QUERY_PARAM_ITEM_NAME = "?item.name=";

    /** プロパティファイル側で項目名を置換するための変数名テンプレート */
    public static final String PLACEHOLDER = "{item.name}";

    /** エラーメッセージをmodelに設定するためのattributeName */
    public static final String ERROR_ATTRIBUTE_NAME = "errors";

    public String getErrorAttributeName() {
        return ERROR_ATTRIBUTE_NAME;
    }
}
