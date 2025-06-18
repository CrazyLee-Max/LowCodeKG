package org.example.lowcodekg.query.service.WorkFlow.builder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.example.lowcodekg.query.service.WorkFlow.model.*;
import org.example.lowcodekg.query.service.WorkFlow.prompt.*;
import org.example.lowcodekg.query.service.llm.LLMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 工作流编排器 - 负责协调工作流生成的三个主要步骤
 */
@Component
public class WorkflowOrchestrator {

    @Autowired
    private LLMService llmService;

    // 参数名到UUID的映射
    private Map<String, String> paramNameToUuidMap = new HashMap<>();

    /**
     * 第一步：分析任务
     */
    public TaskAnalysisResult analyzeTask(String userInput) {
        System.out.println("=== 第一步：任务分析开始 ===");
        System.out.println("用户输入: " + userInput);
        
        String prompt = TaskAnalysisPromptBuilder.buildTaskAnalysisPrompt(userInput);
        System.out.println("生成的提示词: " + prompt);
        
        String llmResponse = llmService.chat("deepseek", prompt);
        System.out.println("LLM原始响应: " + llmResponse);
        
        if (llmResponse == null || llmResponse.trim().isEmpty()) {
            System.err.println("LLM响应为空或null");
            return null;
        }
        
        try {
            JSONObject jsonResponse = JSON.parseObject(llmResponse);
            System.out.println("JSON解析成功: " + jsonResponse.toJSONString());
            
            TaskAnalysisResult result = new TaskAnalysisResult();
            
            // 解析输入参数
            if (jsonResponse.containsKey("inputParameters")) {
                result.setInputParameters(jsonResponse.getJSONArray("inputParameters").toJavaList(InputParameter.class));
                System.out.println("解析到输入参数数量: " + result.getInputParameters().size());
            } else {
                System.out.println("未找到inputParameters字段");
            }
            
            // 解析逻辑结构
            if (jsonResponse.containsKey("logicStructure")) {
                result.setLogicStructure(jsonResponse.getJSONObject("logicStructure").toJavaObject(LogicStructure.class));
                System.out.println("解析到逻辑结构类型: " + result.getLogicStructure().getType());
            } else {
                System.out.println("未找到logicStructure字段");
            }
            
            System.out.println("=== 第一步：任务分析完成 ===");
            return result;
        } catch (Exception e) {
            System.err.println("JSON解析失败: " + e.getMessage());
            System.err.println("原始响应内容: " + llmResponse);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 第二步：生成工作流描述
     */
    public WorkflowDescription generateWorkflowDescription(TaskAnalysisResult analysisResult) {
        System.out.println("=== 第二步：工作流描述生成开始 ===");
        System.out.println("分析结果输入参数数量: " + (analysisResult.getInputParameters() != null ? analysisResult.getInputParameters().size() : 0));
        System.out.println("分析结果逻辑结构: " + (analysisResult.getLogicStructure() != null ? analysisResult.getLogicStructure().getType() : "null"));
        
        String prompt = WorkflowDescriptionPromptBuilder.buildWorkflowDescriptionPrompt(analysisResult);
        System.out.println("生成的提示词: " + prompt);
        
        String llmResponse = llmService.chat("deepseek", prompt);
        System.out.println("LLM原始响应: " + llmResponse);
        
        if (llmResponse == null || llmResponse.trim().isEmpty()) {
            System.err.println("LLM响应为空或null");
            return null;
        }
        
        try {
            JSONObject jsonResponse = JSON.parseObject(llmResponse);
            System.out.println("JSON解析成功: " + jsonResponse.toJSONString());
            
            WorkflowDescription description = new WorkflowDescription();
            description.setName(jsonResponse.getString("name"));
            description.setIdentifier(jsonResponse.getString("identifier"));
            description.setDescription(jsonResponse.getString("description"));
            
            System.out.println("工作流名称: " + description.getName());
            System.out.println("工作流标识符: " + description.getIdentifier());
            System.out.println("=== 第二步：工作流描述生成完成 ===");
            
            return description;
        } catch (Exception e) {
            System.err.println("JSON解析失败: " + e.getMessage());
            System.err.println("原始响应内容: " + llmResponse);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 第三步：组装工作流
     */
    public String assembleWorkflow(WorkflowDescription description, TaskAnalysisResult analysisResult) {
        Map<String, Object> workflow = new LinkedHashMap<>();
        
        // 设置工作流字段
        String workflowUuid = UuidGenerator.generateUuid("biz_flow");
        workflow.put("parentElementUuid", "");
        workflow.put("kind", "biz_flow");
        workflow.put("name", description.getName());
        workflow.put("identifier", description.getIdentifier());
        workflow.put("elementUuid", workflowUuid);
        
        // 输入参数
        Map<String, Object> inputs = new HashMap<>();
        List<Map<String, Object>> params = new ArrayList<>();
        
        // 清空映射
        paramNameToUuidMap.clear();
        
        int serial = 1;
        for (InputParameter param : analysisResult.getInputParameters()) {
            Map<String, Object> paramMap = new LinkedHashMap<>();
            String paramUuid = UuidGenerator.generateUuid("input_param");
            String paramIdentifier = UuidGenerator.generateIdentifier(param.getName());
            
            // 设置参数
            paramMap.put("name", param.getName());
            paramMap.put("kind", "input_param");
            paramMap.put("dataType", UuidGenerator.createDataType(param.getDataType()));
            paramMap.put("parentElementUuid", workflowUuid);
            paramMap.put("elementUuid", paramUuid);
            paramMap.put("identifier", paramIdentifier);
            paramMap.put("serial", serial++);
            
            // 构建映射 - 同时使用中文名称和identifier作为key，值都指向paramUuid
            paramNameToUuidMap.put(param.getName(), paramUuid);
            paramNameToUuidMap.put(paramIdentifier, paramUuid);
            
            params.add(paramMap);
        }
        
        inputs.put("params", params);
        workflow.put("inputs", inputs);
        workflow.put("outputs", new HashMap<>());
        workflow.put("localVariables", new HashMap<>());
        
        // 设置参数映射到NodeBuilder
        NodeBuilder.setParamNameToUuidMap(paramNameToUuidMap);
        
        // 子节点（逻辑结构）
        List<Map<String, Object>> children = new ArrayList<>();
        if (analysisResult.getLogicStructure() != null) {
            children.addAll(WorkflowBuilder.buildLogicNodes(analysisResult.getLogicStructure(), workflowUuid));
        }
        workflow.put("children", children);
        
        // 后处理验证：检查并替换任何残留的中文字符
        workflow = validateAndFixChineseCharacters(workflow);
        
        return JSON.toJSONString(workflow, true);
    }
    
    /**
     * 验证并修复JSON中的中文字符
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> validateAndFixChineseCharacters(Map<String, Object> workflow) {
        String jsonString = JSON.toJSONString(workflow);
        
        // 检查是否包含中文字符在expression中
        if (jsonString.contains("{{阀门列表}}")) {
            jsonString = jsonString.replace("{{阀门列表}}", "{{valveList}}");
            System.out.println("发现并修复了expression中的中文字符: 阀门列表 -> valveList");
        }
        
        if (jsonString.contains("{{阀门}}")) {
            jsonString = jsonString.replace("{{阀门}}", "{{valve}}");
            System.out.println("发现并修复了expression中的中文字符: 阀门 -> valve");
        }
        
        // 检查elementObj中的中文key
        if (jsonString.contains("\"阀门列表\":")) {
            jsonString = jsonString.replace("\"阀门列表\":", "\"valveList\":");
            System.out.println("发现并修复了elementObj中的中文key: 阀门列表 -> valveList");
        }
        
        if (jsonString.contains("\"阀门\":")) {
            jsonString = jsonString.replace("\"阀门\":", "\"valve\":");
            System.out.println("发现并修复了elementObj中的中文key: 阀门 -> valve");
        }
        
        // 通用中文字符检查
        if (jsonString.matches(".*\\{\\{[\\u4e00-\\u9fa5]+\\}\\}.*")) {
            System.err.println("警告：JSON中仍然包含中文字符在expression中，需要进一步检查");
        }
        
        try {
            return JSON.parseObject(jsonString, Map.class);
        } catch (Exception e) {
            System.err.println("JSON解析失败，返回原始workflow: " + e.getMessage());
            return workflow;
        }
    }
}