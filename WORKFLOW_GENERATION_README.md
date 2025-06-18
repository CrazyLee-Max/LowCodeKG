# 工作流生成功能使用指南

## 功能概述

本项目新增了基于自然语言输入生成工作流JSON的功能。用户可以通过简单的中文描述，自动生成符合系统schema的完整工作流定义。

## 核心组件

### 1. WorkflowGenerateService
- **位置**: `src/main/java/org/example/lowcodekg/query/service/llm/WorkflowGenerateService.java`
- **功能**: 核心服务类，负责任务分析、工作流描述生成和JSON组装
- **主要方法**:
  - `generateWorkflow(String userInput)`: 主入口方法
  - `analyzeTask(String userInput)`: 任务分析
  - `generateWorkflowDescription(TaskAnalysisResult)`: 生成工作流描述
  - `assembleWorkflow(WorkflowDescription, TaskAnalysisResult)`: 组装最终JSON

### 2. WorkflowController
- **位置**: `src/main/java/org/example/lowcodekg/controller/WorkflowController.java`
- **功能**: REST API控制器，提供HTTP接口
- **接口**:
  - `POST /workflow/generate`: 通过URL参数接收用户输入
  - `POST /workflow/generate-json`: 通过JSON请求体接收用户输入

### 3. 测试页面
- **位置**: `src/main/resources/static/workflow-test.html`
- **功能**: 可视化测试界面，方便开发和测试
- **访问**: 启动项目后访问 `http://localhost:8080/workflow-test.html`

## 支持的工作流类型

### 1. 条件判断 (condition)
- **描述**: 支持if-else逻辑
- **示例**: "如果北池实际液位大于北池最高液位，则调用排水业务流"
- **支持的操作符**: >, <, =, >=, <=, !=
- **支持嵌套**: 是

### 2. 循环处理 (for_loop)
- **描述**: 支持数组遍历
- **示例**: "遍历设备状态列表，对每个设备进行状态检查"
- **支持的循环类型**: for_each
- **支持嵌套**: 是

### 3. 赋值操作 (assignment)
- **描述**: 支持变量赋值和计算
- **示例**: "计算总金额并赋值给结果变量"
- **支持的表达式**: 简单赋值、计算表达式

### 4. 业务流调用 (call_business_flow)
- **描述**: 调用其他业务流
- **示例**: "调用排水业务流"、"执行告警流程"
- **参数传递**: 支持

## 使用方法

### 1. API调用

#### 方式一：URL参数
```bash
curl -X POST "http://localhost:8080/workflow/generate" \
     -d "userInput=如果北池实际液位大于北池最高液位，则调用排水业务流"
```

#### 方式二：JSON请求体
```bash
curl -X POST "http://localhost:8080/workflow/generate-json" \
     -H "Content-Type: application/json" \
     -d '{
       "userInput": "如果北池实际液位大于北池最高液位，则调用排水业务流",
       "workflowName": "液位监控流程",
       "description": "监控液位并自动处理"
     }'
```

### 2. 测试页面使用

1. 启动Spring Boot应用
2. 浏览器访问 `http://localhost:8080/workflow-test.html`
3. 在输入框中输入自然语言描述
4. 点击"生成工作流"按钮
5. 查看生成的JSON结果

### 3. 示例输入

#### 简单条件判断
```
如果温度大于30度，则启动空调系统
```

#### 复杂嵌套逻辑
```
遍历设备状态列表，对每个设备进行检查，如果设备状态异常则发送告警，否则记录正常状态
```

#### 多条件判断
```
根据用户权限级别，如果是管理员则允许访问所有功能，如果是普通用户则只允许访问基础功能，否则拒绝访问
```

## 生成的JSON结构

生成的工作流JSON严格遵循系统schema，包含以下主要部分：

```json
{
  "parentElementUuid": "",
  "kind": "biz_flow",
  "name": "工作流名称",
  "identifier": "workflowIdentifier",
  "elementUuid": "u_biz_flow_U_随机UUID_u",
  "inputs": {
    "params": [
      {
        "name": "参数名称",
        "kind": "input_param",
        "dataType": {
          "type": "数据类型",
          "uuid": "随机UUID"
        },
        "parentElementUuid": "父元素UUID",
        "elementUuid": "u_input_param_U_随机UUID_u",
        "identifier": "参数标识符",
        "serial": 1
      }
    ]
  },
  "outputs": {},
  "localVariables": {},
  "children": [
    // 逻辑节点数组
  ]
}
```

## UUID生成规则

系统使用特定的UUID格式：`kind + "_U_" + 32位随机字符 + "_u"`

示例：
- 业务流: `u_biz_flow_U_671c82dc2fa949f88f1542a78b5db551_u`
- 输入参数: `u_input_param_U_81711d5a7f2c42bd869690eb41be5183_u`
- 判断节点: `u_act_judge_U_6d83f90e40154f02943579895f8558b0_u`

## 配置说明

### LLM配置
服务依赖现有的LLMService，支持多种LLM提供商：
- Zhipu (智谱)
- Deepseek
- Ollama (本地部署)

### Prompt模板
详细的prompt模板位于：`src/main/resources/prompts/workflow-generation-prompts.md`

## 扩展和定制

### 1. 添加新的逻辑类型
1. 在`LogicStructure`类中添加新的字段
2. 在`buildLogicNodes`方法中添加新的处理分支
3. 实现对应的构建方法
4. 更新prompt模板

### 2. 改进条件解析
1. 增强`parseConditionExpression`方法
2. 支持更复杂的条件表达式
3. 添加更多操作符支持

### 3. 优化Prompt
1. 修改`buildTaskAnalysisPrompt`方法
2. 提供更多示例和约束
3. 改进错误处理

## 注意事项

1. **LLM依赖**: 功能依赖LLM服务，确保LLM配置正确
2. **JSON格式**: 生成的JSON必须严格符合系统schema
3. **UUID唯一性**: 每次生成的UUID都是唯一的
4. **错误处理**: 包含完整的错误处理和日志记录
5. **性能考虑**: LLM调用可能较慢，建议添加缓存机制

## 故障排除

### 常见问题

1. **LLM服务不可用**
   - 检查LLM配置
   - 确认网络连接
   - 查看日志错误信息

2. **JSON解析失败**
   - 检查LLM返回格式
   - 优化prompt模板
   - 添加更多示例

3. **生成结果不符合预期**
   - 调整prompt描述
   - 增加更多约束条件
   - 提供更详细的示例

### 日志查看
```bash
# 查看应用日志
tail -f logs/application.log

# 查看错误日志
grep "Error in" logs/application.log
```

## 未来改进

1. **智能缓存**: 缓存常用的工作流模式
2. **模板库**: 建立预定义的工作流模板
3. **可视化编辑**: 提供图形化的工作流编辑器
4. **版本管理**: 支持工作流版本控制
5. **测试框架**: 自动化测试生成的工作流

## 联系支持

如有问题或建议，请联系开发团队或提交Issue。