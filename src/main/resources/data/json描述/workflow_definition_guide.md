# 工作流定义指南

## 1. 基本结构

工作流定义是一个JSON对象，包含以下主要部分：

```json
{
  "parentElementUuid": "父元素UUID",
  "kind": "biz_flow",
  "name": "工作流名称",
  "identifier": "工作流标识符",
  "elementUuid": "元素UUID",
  "inputs": { ... },
  "outputs": { ... },
  "localVariables": { ... },
  "children": [ ... ]
}
```

## 2. 输入参数（inputs）

输入参数定义了工作流的输入数据：

```json
{
  "inputs": {
    "params": [
      {
        "name": "参数名称",
        "kind": "input_param",
        "dataType": {
          "type": "string|integer|array",
          "items": {}, // 当type为array时使用
          "uuid": "类型UUID"
        },
        "parentElementUuid": "父元素UUID",
        "elementUuid": "元素UUID",
        "identifier": "参数标识符",
        "serial": 1
      }
    ]
  }
}
```

### 数据类型支持
- `string`: 字符串类型
- `integer`: 整数类型
- `array`: 数组类型（需要定义items）

## 3. 输出参数（outputs）

输出参数定义了工作流的输出数据：

```json
{
  "outputs": {
    "params": [
      {
        "name": "输出参数名称",
        "kind": "output_param",
        "dataType": {
          "type": "array",
          "items": {
            "type": "string|data_model",
            "refUuid": "引用模型UUID",
            "uuid": "类型UUID"
          }
        },
        "parentElementUuid": "父元素UUID",
        "elementUuid": "元素UUID",
        "identifier": "输出参数标识符",
        "serial": 1
      }
    ]
  }
}
```

## 4. 本地变量（localVariables）

本地变量用于工作流内部数据处理：

```json
{
  "localVariables": {
    "params": [
      {
        "name": "变量名称",
        "kind": "local_variable",
        "dataType": {
          "type": "data_model",
          "refUuid": "引用模型UUID",
          "uuid": "类型UUID"
        },
        "parentElementUuid": "父元素UUID",
        "elementUuid": "元素UUID",
        "identifier": "变量标识符",
        "serial": 1,
        "defaultValue": {}
      }
    ]
  }
}
```

## 5. 子节点（children）

子节点定义了工作流的具体执行步骤：

### 5.1 节点类型

- `act_assignment`: 赋值操作
- `act_judge`: 条件判断
- `act_condition`: 条件分支
- `act_else`: else分支
- `act_for_each`: 循环操作
- `biz_call_biz_flow`: 调用其他工作流

### 5.2 赋值操作示例

```json
{
  "elementUuid": "节点UUID",
  "name": "赋值操作名称",
  "description": "操作描述",
  "attrs": {
    "params": [
      {
        "assignmentUuid": "赋值UUID",
        "field": {
          "type": "simple_select",
          "expression": "{{变量引用}}"
        },
        "value": {
          "type": "simple_select",
          "expression": "赋值表达式"
        }
      }
    ]
  },
  "kind": "act_assignment",
  "children": [],
  "identifier": "节点标识符"
}
```

### 5.3 条件判断示例

```json
{
  "elementUuid": "节点UUID",
  "name": "条件判断名称",
  "attrs": {
    "children": [
      "conditions": [
        {
          "or": [
            {
              "left": {
                "type": "simple_select",
               "expression": "{{左值表达式}}"
              },
              "right": {
                "type": "simple_select",
                "expression": "{{右值表达式}}"
              },
              "condition": {
                "type": "uuid_to_identifier"
              }
            }
          ]
        }
      ]
    ] 
  },
  "kind": "act_judge",
  "identifier": "节点标识符"
}
```

### 5.4 循环操作示例

```json
{
  "elementUuid": "节点UUID",
  "name": "循环操作名称",
  "attrs": {
    "content": {
      "type": "simple_select",
      "expression": "{{循环数组表达式}}"
    }
  },
  "kind": "act_for_each",
  "children": [],
  "identifier": "节点标识符"
}
```

## 6. 表达式系统

### 6.1 基本语法
- 使用 `{{...}}` 进行变量引用
- 支持多层级访问
- 支持方法调用

### 6.2 表达式类型
- `simple_select`: 简单选择表达式
- `uuid_to_identifier`: UUID到标识符的转换

### 6.3 元素引用
- 通过UUID引用其他元素
- 支持当前作用域访问（current_scope）
- 支持常量组引用（constant_group）

## 7. 最佳实践

1. **命名规范**
   - 使用有意义的名称
   - 保持标识符的唯一性
   - 使用驼峰命名法

2. **结构组织**
   - 合理划分子节点
   - 避免过深的嵌套
   - 保持逻辑清晰

3. **数据流转**
   - 明确定义输入输出
   - 合理使用本地变量
   - 注意数据类型匹配

4. **错误处理**
   - 添加适当的条件判断
   - 处理异常情况
   - 保证数据完整性