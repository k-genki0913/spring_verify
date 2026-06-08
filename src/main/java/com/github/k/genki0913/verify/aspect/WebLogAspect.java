package com.github.k.genki0913.verify.aspect;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class WebLogAspect {

    private static final Logger log = LoggerFactory.getLogger(WebLogAspect.class);

    // =========================================================================
    // コントローラー層を特定するポイントカット
    // =========================================================================
    /**
     * 【AOP専用】ログ出力の対象となるコントローラー層（網を張る場所）を一括定義。
     *
     * <p>
     * <strong>■ 発生（適用）契機</strong><br>
     * アプリケーション起動時、Springコンテキストが管理するBeanのうち、クラスの頭に
     * {@code @Controller} または {@code @RestController} アノテーションが付与されたすべてのクラスを自動検知する。
     * </p>
     *
     * <p>
     * <strong>■ 制御（設計）概要</strong><br>
     * 記述方法にパッケージ指定（{@code execution}）ではなく、アノテーション指定（{@code within}）を採用。<br>
     * 機能別（ドメイン別）にパッケージを細かく縦割りに切る設計（Spring PetClinic風）において、
     * 将来的なリファクタリング（フォルダ移動等）が発生しても、ログがサイレントで出力されなくなるバグ（壊れやすいコード）を100%防止する。
     * </p>
     *
     * <p>
     * <strong>■ 技術的特記事項（文法解説）</strong>
     * <ul>
     * <li><b>末尾の「 *」:</b> クラス名に対するワイルドカード。指定アノテーションが付与されていれば、あらゆるクラス名を対象とする。</li>
     * <li><b>空のメソッド本体:</b> マーカーインターフェースと同様の思想。中身を実行するためではなく、上の複雑な条件式に対して
     * {@code controllerPointcut()} という「識別子（名前）」を付与するための言語仕様上の定義お作法。</li>
     * </ul>
     * </p>
     */
    @Pointcut("within(@org.springframework.stereotype.Controller *)" +
            "|| within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object logControllerExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String args = Arrays.toString(joinPoint.getArgs());

        log.info("[START] {}.{}() - Args: {}", className, methodName, args);

        long startTime = System.currentTimeMillis();
        Object result;

        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.warn("[ERROR END] {}.{}() - 異常終了 [Time: {}ms] - Cause: {}", className, methodName, executionTime,
                    throwable.getCause().getClass().getName());
            throw throwable;
        }

        long executionTime = System.currentTimeMillis() - startTime;
        log.info("[END] {}.{}() - 正常終了 [Time: {}ms]", className, methodName, executionTime);

        return result;
    }
}
