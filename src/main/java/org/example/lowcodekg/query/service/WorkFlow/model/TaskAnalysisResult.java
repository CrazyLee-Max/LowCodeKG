package org.example.lowcodekg.query.service.WorkFlow.model;

import java.util.List;

/**
 * 任务分析结果
 */
public class TaskAnalysisResult {
    private List<InputParameter> inputParameters;
    private LogicStructure logicStructure;
    
    // getters and setters
    public List<InputParameter> getInputParameters() {
        return inputParameters;
    }
    
    public void setInputParameters(List<InputParameter> inputParameters) {
        this.inputParameters = inputParameters;
    }
    
    public LogicStructure getLogicStructure() {
        return logicStructure;
    }
    
    public void setLogicStructure(LogicStructure logicStructure) {
        this.logicStructure = logicStructure;
    }
}