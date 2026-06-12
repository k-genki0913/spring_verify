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
    private String queryParamItemName = "?item.name=";

    public String getQueryParamItemName() {
        return queryParamItemName;
    }

    /** プロパティファイル側で項目名を置換するための変数名テンプレート */
    private String placeholder = "{item.name}";

    public String getPlaceholder() {
        return this.placeholder;
    }

    /** エラーメッセージをmodelに設定するためのattributeName */
    private String errorAttributeName = "errors";

    public String getErrorAttributeName() {
        return this.errorAttributeName;
    }
}
