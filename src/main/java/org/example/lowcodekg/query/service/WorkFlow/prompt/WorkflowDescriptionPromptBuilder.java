package org.example.lowcodekg.query.service.WorkFlow.prompt;

import com.alibaba.fastjson.JSON;
import org.example.lowcodekg.query.service.WorkFlow.model.TaskAnalysisResult;

/**
 * 工作流描述提示词构建器
 */
public class WorkflowDescriptionPromptBuilder {

    /**
     * 构建工作流描述提示词
     */
    public static String buildWorkflowDescriptionPrompt(TaskAnalysisResult analysisResult) {
        return """
        你是一个工作流命名专家。根据以下任务分析结果，生成合适的工作流基本信息。

        任务分析结果：%s

        要求：
        1. 工作流名称应该简洁明了，体现主要功能
        2. 标识符应该是英文，使用驼峰命名法，不超过50个字符
        3. 描述应该准确概括工作流的作用和流程

        请严格按照以下JSON格式返回，不要添加任何其他文字：

        {
            "name": "工作流的中文名称",
            "identifier": "workflowIdentifier",
            "description": "工作流的详细描述，说明其功能和用途"
        }

        命名规范:
        - 名称: 使用中文，简洁明了，如"液位监控流程"、"设备状态检查"等
        - 标识符: 使用英文驼峰命名，如"liquidLevelMonitoring"、"deviceStatusCheck"等
        - 描述: 详细说明工作流的功能、输入、处理逻辑和输出

        只返回JSON，不要其他说明文字。""".formatted(JSON.toJSONString(analysisResult));
    }
}