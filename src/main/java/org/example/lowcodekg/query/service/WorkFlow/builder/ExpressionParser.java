package org.example.lowcodekg.query.service.WorkFlow.builder;

import org.example.lowcodekg.query.service.WorkFlow.model.ConditionExpression;

import java.util.*;

/**
 * 表达式解析器
 */
public class ExpressionParser {

    /**
     * 解析条件表达式
     */
    public static ConditionExpression parseConditionExpression(String condition) {
        ConditionExpression expr = new ConditionExpression();
        
        // 简化的条件解析逻辑
        if (condition.contains("大于") || condition.contains("超过")) {
            expr.setOperator(">");
            String[] parts = condition.split("大于|超过");
            if (parts.length == 2) {
                expr.setLeftVariable(parts[0].trim());
                expr.setRightVariable(parts[1].trim());
            }
        } else if (condition.contains("小于")) {
            expr.setOperator("<");
            String[] parts = condition.split("小于");
            if (parts.length == 2) {
                expr.setLeftVariable(parts[0].trim());
                expr.setRightVariable(parts[1].trim());
            }
        } else if (condition.contains("等于")) {
            expr.setOperator("=");
            String[] parts = condition.split("等于");
            if (parts.length == 2) {
                expr.setLeftVariable(parts[0].trim());
                expr.setRightVariable(parts[1].trim());
            }
        } else {
            // 默认处理 - 不应该使用固定的"condition"变量名
            expr.setOperator(">");
            expr.setLeftVariable("未知变量");
            expr.setRightValue("0");
        }
        return expr;
    }
    
    /**
     * 获取操作符对应的UUID
     */
    public static String getOperatorUuid(String operator) {
        Map<String, String> operatorUuids = Map.of(
            ">", "u_3debe682e86040d0881263fbf7a1982a_u",
            "<", "u_4debe682e86040d0881263fbf7a1982b_u",
            "=", "u_5debe682e86040d0881263fbf7a1982c_u",
            ">=", "u_6debe682e86040d0881263fbf7a1982d_u",
            "<=", "u_7debe682e86040d0881263fbf7a1982e_u",
            "!=", "u_8debe682e86040d0881263fbf7a1982f_u"
        );
        return operatorUuids.getOrDefault(operator, "u_3debe682e86040d0881263fbf7a1982a_u");
    }
}