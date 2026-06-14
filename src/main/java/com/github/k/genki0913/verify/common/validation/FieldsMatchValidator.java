package com.github.k.genki0913.verify.common.validation;

import org.springframework.beans.BeanWrapperImpl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FieldsMatchValidator implements ConstraintValidator<FieldsMatch, Object> {

    private String field;
    private String fieldToMatch;

    @Override
    public void initialize(FieldsMatch constraintAnnotation) {
        // アノテーションで指定されたフィールド名（"email" と "retryEmail"）を受け取って保持する
        this.field = constraintAnnotation.field();
        this.fieldToMatch = constraintAnnotation.fieldToMatch();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            // Springの便利クラス「BeanWrapperImpl」を使って、Formオブジェクトから値を取り出す
            Object fieldValue = new BeanWrapperImpl(value).getPropertyValue(field);
            Object fieldToMatchValud = new BeanWrapperImpl(value).getPropertyValue(fieldToMatch);

            // 2つの値がどちらもnull（未入力）の場合は、単項目チェック（@NotBlank）に任せるため、ここではtrueにする
            if (fieldValue == null && fieldToMatchValud == null) {
                return true;
            }

            // 2つのフィールドの値を比較した結果を返す（一致していれば true、違えば false）
            return fieldValue != null && fieldValue.equals(fieldToMatchValud);
        } catch (Exception e) {
            return false;
        }
    }
}
