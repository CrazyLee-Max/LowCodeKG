# 工作流生成提示词模板

## 任务分析提示词

```
你是一个专业的工作流分析专家。请仔细分析以下用户输入，提取出工作流所需的输入参数和逻辑结构。

用户输入：{USER_INPUT}

分析要求：
1. 识别所有需要的输入参数（变量、数据等）
2. 分析逻辑结构（条件判断、循环、赋值、业务流调用等）
3. 理解参数之间的依赖关系
4. 识别嵌套的逻辑结构

请严格按照以下JSON格式返回分析结果，不要添加任何其他文字：

{
  "inputParameters": [
    {
      "name": "参数的中文名称",
      "dataType": "数据类型(string/integer/boolean/array/object)",
      "description": "参数的详细描述",
      "required": true/false
    }
  ],
  "logicStructure": {
    "type": "逻辑类型(condition/for_loop/assignment/call_business_flow/sequence)",
    "condition": "条件表达式(仅当type为condition时)",
    "operator": "比较操作符(>/</=/>=/<=/!=等，仅当type为condition时)",
    "leftVariable": "左侧变量名(仅当type为condition时)",
    "rightVariable": "右侧变量名(仅当type为condition时)",
    "arrayVariable": "要遍历的数组变量名(仅当type为for_loop时)",
    "iteratorName": "循环迭代器名称(仅当type为for_loop时)",
    "targetVariable": "目标变量名(仅当type为assignment时)",
    "assignmentExpression": "赋值表达式(仅当type为assignment时)",
    "targetFlowName": "目标业务流名称(仅当type为call_business_flow时)",
    "targetFlowUuid": "目标业务流UUID(仅当type为call_business_flow时，可为空)",
    "actions": [子动作列表，递归结构],
    "elseActions": [else分支动作列表，仅当type为condition时]
  }
}

注意事项：
- 数据类型必须是：string, integer, boolean, array, object 中的一个
- 条件表达式应该是自然语言描述，如"北池实际液位大于北池最高液位"
- 操作符应该是具体的比较符号：>, <, =, >=, <=, !=
- 变量名应该对应输入参数中的name字段
- 如果是嵌套逻辑，actions数组中的每个元素都应该是完整的logicStructure对象
```

## 工作流描述生成提示词

```
你是一个工作流命名专家。根据以下任务分析结果，生成合适的工作流基本信息。

任务分析结果：{ANALYSIS_RESULT}

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

命名规范：
- 名称：使用中文，简洁明了，如"液位监控流程"、"设备状态检查"等
- 标识符：使用英文驼峰命名，如"liquidLevelMonitoring"、"deviceStatusCheck"等
- 描述：详细说明工作流的功能、输入、处理逻辑和输出
```

## 条件表达式解析提示词

```
你是一个条件表达式解析专家。请将以下自然语言条件转换为结构化的条件表达式。

条件描述：{CONDITION_TEXT}
可用变量：{AVAILABLE_VARIABLES}

请分析条件中的：
1. 左侧操作数（变量名）
2. 比较操作符
3. 右侧操作数（变量名或常量值）
4. 逻辑连接符（如果有多个条件）

返回JSON格式：
{
  "leftVariable": "左侧变量名",
  "operator": "比较操作符(>/</=/>=/<=/!=)",
  "rightVariable": "右侧变量名（如果是变量）",
  "rightValue": "右侧常量值（如果是常量）",
  "logicOperator": "逻辑操作符(and/or，如果有多个条件)",
  "subConditions": [子条件列表，如果是复合条件]
}

示例：
- "温度大于30" -> {"leftVariable": "温度", "operator": ">", "rightValue": "30"}
- "用户年龄大于等于18且小于65" -> 包含subConditions的复合条件
```

## 业务流调用解析提示词

```
你是一个业务流调用分析专家。请分析以下文本中提到的业务流调用需求。

文本内容：{TEXT_CONTENT}

请识别：
1. 要调用的业务流名称
2. 调用的触发条件
3. 需要传递的参数
4. 调用的上下文

返回JSON格式：
{
  "flowName": "业务流名称",
  "triggerCondition": "触发调用的条件",
  "inputParameters": [
    {
      "name": "参数名",
      "source": "参数来源（变量名或常量值）",
      "type": "参数类型"
    }
  ],
  "description": "调用描述"
}

常见的业务流调用关键词：
- "调用"、"执行"、"启动"、"触发"
- "业务流"、"流程"、"服务"、"功能"
- "排水"、"告警"、"通知"、"处理"等具体业务动作
```

## 循环结构解析提示词

```
你是一个循环结构分析专家。请分析以下文本中的循环逻辑。

文本内容：{TEXT_CONTENT}
可用变量：{AVAILABLE_VARIABLES}

请识别：
1. 循环类型（for循环、while循环等）
2. 要遍历的数据源
3. 循环变量名
4. 循环体内的操作

返回JSON格式：
{
  "loopType": "循环类型(for_each/while/for)",
  "dataSource": "数据源变量名",
  "iteratorName": "循环迭代器名称",
  "loopBody": [
    {
      "type": "操作类型",
      "description": "操作描述",
      "details": "具体操作内容"
    }
  ],
  "condition": "循环条件（如果是while循环）"
}

循环关键词识别：
- "遍历"、"循环"、"对每个"、"逐一"、"批量处理"
- "列表"、"数组"、"集合"、"队列"
- "直到"、"当...时"、"重复"
```

## 赋值操作解析提示词

```
你是一个赋值操作分析专家。请分析以下文本中的赋值逻辑。

文本内容：{TEXT_CONTENT}
可用变量：{AVAILABLE_VARIABLES}

请识别：
1. 目标变量
2. 赋值表达式
3. 数据来源
4. 计算逻辑

返回JSON格式：
{
  "targetVariable": "目标变量名",
  "sourceType": "数据来源类型(variable/constant/expression/function)",
  "sourceValue": "源数据值或表达式",
  "operation": "操作类型(assign/calculate/transform)",
  "expression": "完整的赋值表达式",
  "description": "赋值操作描述"
}

赋值关键词识别：
- "设置"、"赋值"、"计算"、"更新"
- "等于"、"设为"、"变为"
- "累加"、"求和"、"平均值"、"最大值"、"最小值"
- "转换"、"格式化"、"处理"
```

## 错误处理和验证提示词

```
你是一个工作流验证专家。请检查以下工作流结构的合理性和完整性。

工作流结构：{WORKFLOW_STRUCTURE}

请检查：
1. 参数定义是否完整
2. 逻辑结构是否合理
3. 变量引用是否正确
4. 嵌套结构是否平衡
5. 数据类型是否匹配

返回验证结果：
{
  "isValid": true/false,
  "errors": [
    {
      "type": "错误类型",
      "message": "错误描述",
      "location": "错误位置",
      "suggestion": "修复建议"
    }
  ],
  "warnings": [
    {
      "type": "警告类型",
      "message": "警告描述",
      "suggestion": "优化建议"
    }
  ],
  "suggestions": [
    "改进建议1",
    "改进建议2"
  ]
}

常见错误类型：
- "MISSING_PARAMETER": 缺少必要参数
- "INVALID_REFERENCE": 无效的变量引用
- "TYPE_MISMATCH": 数据类型不匹配
- "LOGIC_ERROR": 逻辑错误
- "STRUCTURE_ERROR": 结构错误
```