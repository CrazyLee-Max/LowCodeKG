package org.example.lowcodekg.query.service.WorkFlow.builder;

import org.example.lowcodekg.query.service.WorkFlow.model.LogicStructure;

import java.util.*;

/**
 * 工作流构建器
 * 负责构建复合节点和逻辑节点的路由分发
 */
public class WorkflowBuilder {
    
    /**
     * 构建逻辑节点
     */
    public static List<Map<String, Object>> buildLogicNodes(LogicStructure logic, String parentUuid) {
        List<Map<String, Object>> nodes = new ArrayList<>();
        
        if ("condition".equals(logic.getType())) {
            nodes.add(buildJudgeNode(logic, parentUuid));
        } else if ("for_loop".equals(logic.getType())) {
            nodes.add(NodeBuilder.buildForEachNode(logic, parentUuid));
        } else if ("assignment".equals(logic.getType())) {
            nodes.add(NodeBuilder.buildAssignmentNode(logic, parentUuid));
        } else if ("call_business_flow".equals(logic.getType())) {
            nodes.add(NodeBuilder.buildCallBusinessFlowNode(logic, parentUuid));
        }
        
        return nodes;
    }

    /**
     * 构建判断节点
     */
    public static Map<String, Object> buildJudgeNode(LogicStructure logic, String parentUuid) {
        Map<String, Object> judgeNode = new LinkedHashMap<>();
        String judgeUuid = UuidGenerator.generateUuid("act_judge");
        
        // 按照标准顺序设置字段
        judgeNode.put("elementUuid", judgeUuid);
        judgeNode.put("name", "判断");
        judgeNode.put("description", "");
        judgeNode.put("attrs", new HashMap<>());
        judgeNode.put("parentElementUuid", parentUuid);
        judgeNode.put("serial", 1);
        judgeNode.put("kind", "act_judge");
        judgeNode.put("identifier", UuidGenerator.generateIdentifier("judge"));
        
        List<Map<String, Object>> children = new ArrayList<>();
        
        // Condition Node
        Map<String, Object> conditionNode = NodeBuilder.buildConditionNode(logic, judgeUuid);
        children.add(conditionNode);
        
        // Else Node - 总是添加else节点，即使为空
        Map<String, Object> elseNode = NodeBuilder.buildElseNode(logic, judgeUuid);
        children.add(elseNode);
        
        judgeNode.put("children", children);
        return judgeNode;
    }
}