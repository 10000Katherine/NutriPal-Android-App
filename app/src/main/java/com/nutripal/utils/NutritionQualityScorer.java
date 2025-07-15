package com.nutripal.utils;

import android.graphics.Color;

/**
 * 根据食物的卡路里计算营养质量得分。
 * 这是一个独立的工具类，可以在应用的任何地方使用。
 */
public class NutritionQualityScorer {

    /**
     * 定义营养评分的枚举类型，方便管理和扩展。
     * 每个评分都包含一个等级（字符串）和一个建议的颜色（十六进制字符串）。
     */
    public enum Score {
        A("A", "#4CAF50"), // 优 (绿色)
        B("B", "#FFC107"), // 良 (黄色)
        C("C", "#FF9800"), // 中 (橙色)
        D("D", "#F44336"); // 差 (红色)

        private final String grade;
        private final String color;

        Score(String grade, String color) {
            this.grade = grade;
            this.color = color;
        }

        public String getGrade() {
            return grade;
        }

        public String getColor() {
            return color;
        }

        public int getParsedColor() {
            return Color.parseColor(this.color);
        }
    }

    /**
     * 根据卡路里值获取营养评分。
     *
     * @param calories 食物的卡路里值（每100克）
     * @return Score枚举，包含了评分等级和建议颜色
     */
    public static Score getScore(double calories) {
        if (calories <= 100) {
            return Score.A;
        } else if (calories <= 300) {
            return Score.B;
        } else if (calories <= 500) {
            return Score.C;
        } else {
            return Score.D;
        }
    }
}
