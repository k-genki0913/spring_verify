package com.github.k.genki0913.verify.common.aspect;

import java.util.Arrays;
import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class WebLogAspect {

    private static final Logger log = LoggerFactory.getLogger(WebLogAspect.class);
    private static final String MDC_STATUS_KEY = "status";
    private static final String MDC_TRACE_ID_KEY = "traceId";

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

    // =========================================================================
    // コントローラー層のログ出力・パフォーマンス計測・MDC（識別子・ステータス）制御
    // =========================================================================
    /**
     * 【AOP中核】各コントローラーの処理開始・終了（または異常終了）のログを一元的に出力し、
     * 同時にリクエストごとのトレーサビリティとログの美観（等幅整列）を担保するためのMDC制御を行います。
     *
     * <p>
     * <strong>■ 処理フローと設計思想（MDCによる2軸制御）</strong>
     * <ol>
     * <li><b>入り口（Pre-processing）:</b>
     * リクエストごとに一意のトレースID（UUIDの先頭8文字）を発行してMDCに格納（{@code traceId}）。
     * 同時に、処理開始を示すステータス（{@code START}）をMDCに格納（{@code status}）します。<br>
     * これにより、ログフォーマット側で設定された固定幅の枠内にステータスが綺麗に収まり、
     * 縦のラインが1ミリも狂わない「構造化ログ」の土台を作ります。</li>
     * <li><b>本編実行（Execution）:</b>
     * ターゲットとなるコントローラーメソッド（{@code joinPoint.proceed()}）を実行。
     * 実行前後の差分から、ミリ秒単位の正確な処理時間を算出します。</li>
     * <li><b>出口（Post-processing - 正常系）:</b>
     * 正常終了時は、MDCのステータスを {@code END} に上書き更新し、算出された処理時間とともにログを出力。<br>
     * メッセージの文章から状態を表す文言（[START]等）を排除し、ステータス枠に処理を委ねることで、
     * メッセージ自体の開始位置（メソッド名など）を完全に一直線に揃えます。</li>
     * <li><b>出口（Post-processing - 異常系）:</b>
     * ビジネスロジック等で例外が発生した場合は {@code catch} ブロックが迎撃。<br>
     * ログの一貫性（最大5文字幅）を維持するため、ステータスを {@code FAIL} に上書き更新したのち、
     * 根本原因（{@code Cause}）のクラス名とともに<b>WARNレベル</b>で異常終了ログを出力し、例外を上位レイヤーへ再スローします。</li>
     * <li><b>最終クリーンアップ（Cleanup）:</b>
     * {@code finally} ブロックにて、MDCから {@code traceId} と {@code status}
     * の双方を確実に削除（{@code remove}）します。<br>
     * Webサーバーのスレッドプール（スレッドの使い回し）の仕様による、<b>「別リクエストへのIDおよびステータスの意図せぬ汚染（リソースリーク）」</b>を100%防止する極めて重要な防衛処理です。</li>
     * </ol>
     * </p>
     *
     * @param joinPoint
     *                      AOPが割り込んだ対象メソッドの実行コンテキスト（クラス名、メソッド名、引数の情報を含む）
     * @return 対象コントローラーメソッドが返却するオブジェクト（ModelAndView、String、ResponseEntity等）
     * @throws Throwable
     *                       対象コントローラーメソッドの内部、または処理実行中に発生したすべての例外（上位のWebExceptionHandlerへそのまま伝播されます）
     */
    @Around("controllerPointcut()")
    public Object logControllerExecution(ProceedingJoinPoint joinPoint) throws Throwable {

        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put(MDC_TRACE_ID_KEY, traceId);
        MDC.put(MDC_STATUS_KEY, "START");

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String args = Arrays.toString(joinPoint.getArgs());

        log.info("{}.{}() - Args: {}", className, methodName, args);

        long startTime = System.currentTimeMillis();
        Object result;

        try {
            result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;
            MDC.put(MDC_STATUS_KEY, "END");
            log.info("{}.{}() - 正常終了 [Time: {}ms]", className, methodName, executionTime);
        } catch (Exception ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            MDC.put(MDC_STATUS_KEY, "FAIL");
            log.warn("{}.{}() - 異常終了 [Time: {}ms]", className, methodName, executionTime, ex);
            throw ex;
        } finally {
            MDC.remove(MDC_TRACE_ID_KEY);
            MDC.remove(MDC_STATUS_KEY);
        }

        return result;
    }
}
