package org.example.lowcodekg.query.service.WorkFlow;

import org.example.lowcodekg.model.result.Result;
import org.example.lowcodekg.model.result.ResultCodeEnum;
import org.example.lowcodekg.query.service.WorkFlow.builder.WorkflowOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.lowcodekg.query.service.WorkFlow.model.*;
@Service
public class WorkflowGenerateService {

    @Autowired
    private WorkflowOrchestrator workflowOrchestrator;

    /**
     * Generate Workflow
     * @param userInput nature language input
     * @return Json String
     */
    public Result<String> generateWorkflow(String userInput) {
        try {
            // FirstStep：AnalysisTask
            TaskAnalysisResult analysisResult = workflowOrchestrator.analyzeTask(userInput);
            if (analysisResult == null) {
                return Result.build(null, ResultCodeEnum.FAIL);
            }

            // SecondStep：GenerateWorkflowDescription
            WorkflowDescription description = workflowOrchestrator.generateWorkflowDescription(analysisResult);
            if (description == null) {
                return Result.build(null, ResultCodeEnum.FAIL);
            }

            // ThirdStep：AssembleWorkflow
            String workflowJson = workflowOrchestrator.assembleWorkflow(description, analysisResult);
            
            return Result.build(workflowJson, ResultCodeEnum.SUCCESS);
        } catch (Exception e) {
            System.err.println("Error in generateWorkflow: " + e.getMessage());
            return Result.build(null, ResultCodeEnum.FAIL);
        }
    }
}