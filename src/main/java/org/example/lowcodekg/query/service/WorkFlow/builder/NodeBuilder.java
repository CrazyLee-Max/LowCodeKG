package org.example.lowcodekg.query.service.WorkFlow.builder;

import org.example.lowcodekg.query.service.WorkFlow.model.ConditionExpression;
import org.example.lowcodekg.query.service.WorkFlow.model.LogicStructure;

import java.util.*;

/**
 * 节点构建器
 */
public class NodeBuilder {
    
    private static Map<String, String> paramNameToUuidMap = new HashMap<>();
    
    /**
     * 设置参数名称到UUID的映射
     */
    public static void setParamNameToUuidMap(Map<String, String> paramMap) {
        paramNameToUuidMap = paramMap;
    }
    
    /**
     * Build Condition Node
     */
    public static Map<String, Object> buildConditionNode(LogicStructure logic, String parentUuid) {
        Map<String, Object> conditionNode = new LinkedHashMap<>();
        String conditionUuid = UuidGenerator.generateUuid("act_condition");
        
        // Set fields
        conditionNode.put("elementUuid", conditionUuid);
        conditionNode.put("name", logic.getCondition());
        conditionNode.put("description", "");
        conditionNode.put("parentElementUuid", parentUuid);
        conditionNode.put("serial", 1);
        conditionNode.put("kind", "act_condition");
        conditionNode.put("identifier", UuidGenerator.generateIdentifier("condition"));
        
        // Build Expressions
        Map<String, Object> attrs = buildConditionAttrs(logic.getCondition());
        conditionNode.put("attrs", attrs);
        
        // children
        List<Map<String, Object>> children = new ArrayList<>();
        if (logic.getActions() != null) {
            for (LogicStructure action : logic.getActions()) {
                children.addAll(buildLogicNodes(action, conditionUuid));
            }
        }
        conditionNode.put("children", children);
        
        return conditionNode;
    }
    
    /**
     * 构建else节点
     */
    public static Map<String, Object> buildElseNode(LogicStructure logic, String parentUuid) {
        Map<String, Object> elseNode = new LinkedHashMap<>();
        String elseUuid = UuidGenerator.generateUuid("act_else");
        
        // 按照标准顺序设置字段
        elseNode.put("elementUuid", elseUuid);
        elseNode.put("name", "条件判断else");
        elseNode.put("description", "");
        elseNode.put("attrs", null);
        elseNode.put("parentElementUuid", parentUuid);
        elseNode.put("serial", 2);
        elseNode.put("kind", "act_else");
        elseNode.put("identifier", UuidGenerator.generateIdentifier("conditionalJudgmentElse"));
        
        List<Map<String, Object>> children = new ArrayList<>();
        if (logic.getElseActions() != null) {
            for (LogicStructure action : logic.getElseActions()) {
                children.addAll(buildLogicNodes(action, elseUuid));
            }
        }
        elseNode.put("children", children);
        
        return elseNode;
    }
    
    /**
     * 构建for循环节点
     */
    public static Map<String, Object> buildForEachNode(LogicStructure logic, String parentUuid) {
        Map<String, Object> forEachNode = new LinkedHashMap<>();
        String forEachUuid = UuidGenerator.generateUuid("act_for_each");
        
        // 按照标准顺序设置字段
        forEachNode.put("elementUuid", forEachUuid);
        forEachNode.put("name", "for循环");
        forEachNode.put("description", "");
        forEachNode.put("attrs", buildForEachAttrs(logic));
        forEachNode.put("parentElementUuid", parentUuid);
        forEachNode.put("serial", 1);
        forEachNode.put("kind", "act_for_each");
        
        // 为循环迭代器变量创建映射
        if (logic.getIteratorName() != null) {
            String iteratorUuid = forEachUuid + "$Item";
            paramNameToUuidMap.put(logic.getIteratorName(), iteratorUuid);
        }
        
        // 子动作
        List<Map<String, Object>> children = new ArrayList<>();
        if (logic.getActions() != null) {
            for (LogicStructure action : logic.getActions()) {
                children.addAll(buildLogicNodes(action, forEachUuid));
            }
        }
        forEachNode.put("children", children);
        forEachNode.put("identifier", UuidGenerator.generateIdentifier("forEach"));
        
        return forEachNode;
    }
    
    /**
     * 构建赋值节点
     */
    public static Map<String, Object> buildAssignmentNode(LogicStructure logic, String parentUuid) {
        Map<String, Object> assignmentNode = new LinkedHashMap<>();
        String assignmentUuid = UuidGenerator.generateUuid("act_assignment");
        
        // 按照标准顺序设置字段
        assignmentNode.put("elementUuid", assignmentUuid);
        assignmentNode.put("name", "赋值");
        assignmentNode.put("description", "");
        assignmentNode.put("attrs", buildAssignmentAttrs(logic));
        assignmentNode.put("parentElementUuid", parentUuid);
        assignmentNode.put("serial", 1);
        assignmentNode.put("kind", "act_assignment");
        assignmentNode.put("children", new ArrayList<>());
        assignmentNode.put("identifier", UuidGenerator.generateIdentifier("assignment"));
        
        return assignmentNode;
    }
    
    /**
     * 构建调用业务流节点
     */
    public static Map<String, Object> buildCallBusinessFlowNode(LogicStructure logic, String parentUuid) {
        Map<String, Object> callNode = new LinkedHashMap<>();
        String callUuid = UuidGenerator.generateUuid("biz_call_biz_flow");
        
        // 按照标准顺序设置字段
        callNode.put("elementUuid", callUuid);
        callNode.put("name", "调用业务流");
        callNode.put("description", "");
        callNode.put("attrs", buildCallBusinessFlowAttrs(logic));
        callNode.put("parentElementUuid", parentUuid);
        callNode.put("serial", 1);
        callNode.put("kind", "biz_call_biz_flow");
        callNode.put("children", new ArrayList<>());
        callNode.put("identifier", UuidGenerator.generateIdentifier("callBusinessFlow"));
        
        return callNode;
    }
    
    /**
     * 构建条件属性
     */
    private static Map<String, Object> buildConditionAttrs(String condition) {
        Map<String, Object> attrs = new HashMap<>();
        List<Map<String, Object>> conditions = new ArrayList<>();
        
        Map<String, Object> conditionGroup = new HashMap<>();
        List<Map<String, Object>> orConditions = new ArrayList<>();
        
        Map<String, Object> singleCondition = new HashMap<>();
        
        // 解析条件表达式（简化版本）
        ConditionExpression expr = ExpressionParser.parseConditionExpression(condition);
        
        // 构建左侧表达式
        Map<String, Object> left = new HashMap<>();
        left.put("type", "simple_select");
        left.put("value", "");
        
        // 获取左侧变量的UUID
        String leftVariableUuid = paramNameToUuidMap.get(expr.getLeftVariable());
        if (leftVariableUuid == null) {
            // 如果找不到映射，生成一个identifier而不是使用中文名称
            leftVariableUuid = UuidGenerator.generateIdentifier(expr.getLeftVariable());
        }
        
        left.put("expression", "{{" + leftVariableUuid + "}}");
        
        // 添加elementObj（根据schema要求）
        Map<String, Object> leftElementObj = new HashMap<>();
        leftElementObj.put(leftVariableUuid, Arrays.asList(
            Map.of("uuid", "current_scope"),
            Map.of("uuid", leftVariableUuid)
        ));
        left.put("elementObj", leftElementObj);
        
        // 添加expressionList（虽然已弃用但保持兼容）
        left.put("expressionList", Arrays.asList(
            Map.of(
                "elementType", "expression_variable",
                "uuid", leftVariableUuid,
                "elementList", Arrays.asList(
                    Map.of("uuid", "current_scope"),
                    Map.of("uuid", leftVariableUuid)
                )
            )
        ));
        singleCondition.put("left", left);
        
        // 构建右侧表达式
        Map<String, Object> right = new HashMap<>();
        right.put("type", "simple_select");
        right.put("value", "");
        
        if (expr.getRightVariable() != null) {
            // 获取右侧变量的UUID
            String rightVariableUuid = paramNameToUuidMap.get(expr.getRightVariable());
            if (rightVariableUuid == null) {
                // 如果找不到映射，生成一个identifier而不是使用中文名称
                rightVariableUuid = UuidGenerator.generateIdentifier(expr.getRightVariable());
            }
            
            right.put("expression", "{{" + rightVariableUuid + "}}");
            
            // 添加elementObj
            Map<String, Object> rightElementObj = new HashMap<>();
            rightElementObj.put(rightVariableUuid, Arrays.asList(
                Map.of("uuid", "current_scope"),
                Map.of("uuid", rightVariableUuid)
            ));
            right.put("elementObj", rightElementObj);
            
            // 添加expressionList
            right.put("expressionList", Arrays.asList(
                Map.of(
                    "elementType", "expression_variable",
                    "uuid", rightVariableUuid,
                    "elementList", Arrays.asList(
                        Map.of("uuid", "current_scope"),
                        Map.of("uuid", rightVariableUuid)
                    )
                )
            ));
        } else {
            right.put("value", expr.getRightValue());
            right.put("expression", "");
            right.put("elementObj", new HashMap<>());
            right.put("expressionList", new ArrayList<>());
        }
        singleCondition.put("right", right);
        
        // 构建条件操作符
        Map<String, Object> conditionOp = new HashMap<>();
        conditionOp.put("type", "uuid_to_identifier");
        conditionOp.put("elementList", Arrays.asList(Map.of("uuid", ExpressionParser.getOperatorUuid(expr.getOperator()))));
        singleCondition.put("condition", conditionOp);
        
        singleCondition.put("customUuid", UUID.randomUUID().toString().replace("-", ""));
        orConditions.add(singleCondition);
        
        conditionGroup.put("or", orConditions);
        conditions.add(conditionGroup);
        
        attrs.put("conditions", conditions);
        return attrs;
    }
    
    /**
     * 构建for循环属性
     */
    private static Map<String, Object> buildForEachAttrs(LogicStructure logic) {
        Map<String, Object> attrs = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        content.put("type", "simple_select");
        
        // 获取数组变量的UUID或identifier
        String arrayVariableUuid = paramNameToUuidMap.get(logic.getArrayVariable());
        if (arrayVariableUuid == null) {
            // 如果找不到映射，生成一个identifier而不是使用中文名称
            arrayVariableUuid = UuidGenerator.generateIdentifier(logic.getArrayVariable());
            // 将新生成的映射添加到map中
            paramNameToUuidMap.put(logic.getArrayVariable(), arrayVariableUuid);
        }
        
        content.put("expression", "{{" + arrayVariableUuid + "}}");
        content.put("elementList", new ArrayList<>());
        
        // 添加expressionList（根据标准schema要求）
        List<Map<String, Object>> expressionList = new ArrayList<>();
        Map<String, Object> expressionItem = new HashMap<>();
        expressionItem.put("elementType", "expression_variable");
        expressionItem.put("uuid", arrayVariableUuid);
        expressionItem.put("elementList", Arrays.asList(
            Map.of("uuid", "current_scope"),
            Map.of("uuid", arrayVariableUuid)
        ));
        expressionList.add(expressionItem);
        content.put("expressionList", expressionList);
        
        // 添加elementObj（根据schema要求）
        Map<String, Object> elementObj = new HashMap<>();
        elementObj.put(arrayVariableUuid, Arrays.asList(
            Map.of("uuid", "current_scope"),
            Map.of("uuid", arrayVariableUuid)
        ));
        content.put("elementObj", elementObj);
        
        attrs.put("content", content);
        return attrs;
    }
    
    /**
     * 构建赋值属性
     */
    private static Map<String, Object> buildAssignmentAttrs(LogicStructure logic) {
        Map<String, Object> attrs = new HashMap<>();
        List<Map<String, Object>> params = new ArrayList<>();
        
        Map<String, Object> param = new HashMap<>();
        param.put("assignmentUuid", UUID.randomUUID().toString().replace("-", ""));
        
        Map<String, Object> field = new HashMap<>();
        field.put("type", "simple_select");
        field.put("value", null);
        field.put("elementList", new ArrayList<>());
        
        // 获取目标变量的UUID或identifier
        String targetVariableUuid = paramNameToUuidMap.get(logic.getTargetVariable());
        if (targetVariableUuid == null) {
            // 如果找不到映射，生成一个identifier而不是使用中文名称
            targetVariableUuid = UuidGenerator.generateIdentifier(logic.getTargetVariable());
            // 将新生成的映射添加到map中
            paramNameToUuidMap.put(logic.getTargetVariable(), targetVariableUuid);
        }
        
        field.put("expression", "{{" + targetVariableUuid + "}}");
        
        // 添加expressionList（根据标准schema要求）
        List<Map<String, Object>> expressionList = new ArrayList<>();
        Map<String, Object> expressionItem = new HashMap<>();
        expressionItem.put("elementType", "expression_variable");
        expressionItem.put("uuid", targetVariableUuid);
        expressionItem.put("elementList", Arrays.asList(
            Map.of("uuid", "current_scope"),
            Map.of("uuid", targetVariableUuid)
        ));
        expressionList.add(expressionItem);
        field.put("expressionList", expressionList);
        
        // 添加elementObj（根据标准schema要求）
        Map<String, Object> elementObj = new HashMap<>();
        elementObj.put(targetVariableUuid, Arrays.asList(
            Map.of("uuid", "current_scope"),
            Map.of("uuid", targetVariableUuid)
        ));
        field.put("elementObj", elementObj);
        
        param.put("field", field);
       // 添加value字段
        Map<String, Object> value = new HashMap<>();
        value.put("type", "simple_select");
        value.put("value", null);
        value.put("elementList", new ArrayList<>());
        value.put("expression", logic.getAssignmentExpression());
        
        // 添加expressionList（根据标准schema要求）
        List<Map<String, Object>> valueExpressionList = new ArrayList<>();
        Map<String, Object> valueExpressionItem = new HashMap<>();
        valueExpressionItem.put("elementType", "string_literal");
        valueExpressionItem.put("value", logic.getAssignmentExpression());
        valueExpressionList.add(valueExpressionItem);
        value.put("expressionList", valueExpressionList);
        
        // 添加elementObj（根据标准schema要求）
        value.put("elementObj", new HashMap<>());
        
        param.put("value", value);
        
        params.add(param);
        
        attrs.put("params", params);
        return attrs;
    }
    
    /**
     * 构建调用业务流属性
     */
    private static Map<String, Object> buildCallBusinessFlowAttrs(LogicStructure logic) {
        Map<String, Object> attrs = new HashMap<>();
        Map<String, Object> flow = new HashMap<>();
        flow.put("type", "uuid_to_identifier");
        flow.put("value", logic.getTargetFlowUuid());
        flow.put("elementList", new ArrayList<>());
        attrs.put("flow", flow);
        attrs.put("inputParams", new ArrayList<>());
        return attrs;
    }
    
    /**
     * 构建逻辑节点（递归方法）
     */
    private static List<Map<String, Object>> buildLogicNodes(LogicStructure logic, String parentUuid) {
        List<Map<String, Object>> nodes = new ArrayList<>();
        
        switch (logic.getType()) {
            case "condition":
                nodes.add(buildConditionNode(logic, parentUuid));
                break;
            case "for_loop":
                nodes.add(buildForEachNode(logic, parentUuid));
                break;
            case "assignment":
                nodes.add(buildAssignmentNode(logic, parentUuid));
                break;
            case "call_business_flow":
                nodes.add(buildCallBusinessFlowNode(logic, parentUuid));
                break;
            case "sequence":
                if (logic.getActions() != null) {
                    for (LogicStructure action : logic.getActions()) {
                        nodes.addAll(buildLogicNodes(action, parentUuid));
                    }
                }
                break;
        }
        
        return nodes;
    }
}