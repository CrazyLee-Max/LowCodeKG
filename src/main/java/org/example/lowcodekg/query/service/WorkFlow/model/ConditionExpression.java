package org.example.lowcodekg.query.service.WorkFlow.model;

/**
 * 条件表达式类
 */
public class ConditionExpression {
    private String leftVariable;
    private String operator;
    private String rightVariable;
    private String rightValue;
    
    // getters and setters
    public String getLeftVariable() {
        return leftVariable;
    }
    
    public void setLeftVariable(String leftVariable) {
        this.leftVariable = leftVariable;
    }
    
    public String getOperator() {
        return operator;
    }
    
    public void setOperator(String operator) {
        this.operator = operator;
    }
    
    public String getRightVariable() {
        return rightVariable;
    }
    
    public void setRightVariable(String rightVariable) {
        this.rightVariable = rightVariable;
    }
    
    public String getRightValue() {
        return rightValue;
    }
    
    public void setRightValue(String rightValue) {
        this.rightValue = rightValue;
    }
}