package org.example.lowcodekg.query.service.WorkFlow.prompt;

/**
 * 任务分析提示词构建器
 */
public class TaskAnalysisPromptBuilder {

    /**
     * 构建任务分析提示词
     */
    public static String buildTaskAnalysisPrompt(String userInput) {
        return """
        你是一个专业的工作流分析专家。请仔细分析以下用户输入，提取出工作流所需的输入参数和逻辑结构。


        用户输入: %s

        分析要求:
        1. 识别所有需要的输入参数（变量、数据等）
        2. 分析逻辑结构（条件判断、循环、赋值、业务流调用等）
        3. 理解参数之间的依赖关系
        4. 识别嵌套的逻辑结构

        请严格按照以下JSON格式返回分析结果，不要添加任何其他文字:

        {
            "inputParameters": [
                {
                    "name": "参数的中文名称",
                    "dataType": "数据类型(string/integer/boolean/array/object)",
                    "description": "参数的详细描述",
                    "required": true
                }
            ],
            "logicStructure": {
                "type": "逻辑类型(condition/for_loop/assignment/call_business_flow/sequence)",
                "condition": "条件表达式(仅当type为condition时)",
                "operator": "比较操作符(>/</=/>=/<=/!=等，仅当type为condition时)",
                "leftVariable": "左侧变量英文标识符(仅当type为condition时，如valveList)",
                "rightVariable": "右侧变量英文标识符(仅当type为condition时，如maxLevel)",
                "arrayVariable": "要遍历的数组变量英文标识符(仅当type为for_loop时，如valveList)",
                "iteratorName": "循环迭代器英文名称(仅当type为for_loop时，如valve)",
                "targetVariable": "目标变量英文标识符(仅当type为assignment时，如valve)",
                "assignmentExpression": "赋值表达式(仅当type为assignment时)",
                "targetFlowName": "目标业务流名称(仅当type为call_business_flow时)",
                "targetFlowUuid": "目标业务流UUID(仅当type为call_business_flow时，可为空)",
                "actions": [子动作列表，递归结构],
                "elseActions": [else分支动作列表，仅当type为condition时]
            }
        }

        注意事项:
        - 数据类型必须是: string, integer, boolean, array, object 中的一个
        - 条件表达式应该是自然语言描述，如"北池实际液位大于北池最高液位"
        - 操作符应该是具体的比较符号: >, <, =, >=, <=, !=
        - 变量名应该对应输入参数中的name字段
        - 如果是嵌套逻辑，actions数组中的每个元素都应该是完整的logicStructure对象
        - **重要**: 在logicStructure中的所有变量引用字段必须使用英文标识符，严禁使用中文字符
        - leftVariable、rightVariable、arrayVariable、targetVariable、iteratorName等字段必须使用英文或数字组合
        - 示例: 如果输入参数名为"阀门列表"，在logicStructure中应使用"valveList"而不是"阀门列表"
        - 示例: 如果循环变量为"阀门"，在logicStructure中应使用"valve"而不是"阀门"
      

        只返回JSON，不要其他说明文字。""".formatted(userInput);
    }
}