package org.example.lowcodekg.controller;

import org.example.lowcodekg.model.result.Result;
import org.example.lowcodekg.model.result.ResultCodeEnum;
import org.example.lowcodekg.query.service.WorkFlow.WorkflowGenerateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description 工作流生成控制器
 * @Author AI Assistant
 * @Date 2024/12/19
 */
@RestController
@RequestMapping("/workflow")
public class WorkflowController {

    @Autowired
    private WorkflowGenerateService workflowGenerateService;

    /**
     * 根据自然语言输入生成工作流JSON
     * @param userInput 用户的自然语言输入
     * @return 生成的工作流JSON
     */
    @PostMapping("/generate")
    public Result<String> generateWorkflow(@RequestParam String userInput) {
        try {
            if (userInput == null || userInput.trim().isEmpty()) {
                return Result.build(null, ResultCodeEnum.FAIL);
            }
            
            Result<String> result = workflowGenerateService.generateWorkflow(userInput.trim());
            return result;
        } catch (Exception e) {
            System.err.println("Error in generateWorkflow controller: " + e.getMessage());
            return Result.build(null, ResultCodeEnum.FAIL);
        }
    }
}