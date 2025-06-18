package org.example.lowcodekg.query.service.WorkFlow.model;

import java.util.List;

/**
 * 逻辑结构
 */
public class LogicStructure {
    private String type;
    private String condition;
    private String arrayVariable;
    private String iteratorName;
    private String targetVariable;
    private String assignmentExpression;
    private String targetFlowUuid;
    private List<LogicStructure> actions;
    private List<LogicStructure> elseActions;
    
    // getters and setters
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getCondition() {
        return condition;
    }
    
    public void setCondition(String condition) {
        this.condition = condition;
    }
    
    public String getArrayVariable() {
        return arrayVariable;
    }
    
    public void setArrayVariable(String arrayVariable) {
        this.arrayVariable = arrayVariable;
    }
    
    public String getIteratorName() {
        return iteratorName;
    }
    
    public void setIteratorName(String iteratorName) {
        this.iteratorName = iteratorName;
    }
    
    public String getTargetVariable() {
        return targetVariable;
    }
    
    public void setTargetVariable(String targetVariable) {
        this.targetVariable = targetVariable;
    }
    
    public String getAssignmentExpression() {
        return assignmentExpression;
    }
    
    public void setAssignmentExpression(String assignmentExpression) {
        this.assignmentExpression = assignmentExpression;
    }
    
    public String getTargetFlowUuid() {
        return targetFlowUuid;
    }
    
    public void setTargetFlowUuid(String targetFlowUuid) {
        this.targetFlowUuid = targetFlowUuid;
    }
    
    public List<LogicStructure> getActions() {
        return actions;
    }
    
    public void setActions(List<LogicStructure> actions) {
        this.actions = actions;
    }
    
    public List<LogicStructure> getElseActions() {
        return elseActions;
    }
    
    public void setElseActions(List<LogicStructure> elseActions) {
        this.elseActions = elseActions;
    }
}