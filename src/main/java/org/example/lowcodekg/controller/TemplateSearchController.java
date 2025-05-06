package org.example.lowcodekg.controller;

import org.example.lowcodekg.model.dto.Neo4jSubGraph;
import org.example.lowcodekg.service.ElasticSearchService;
import org.example.lowcodekg.service.Neo4jGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/template/search")
public class TemplateSearchController {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private Neo4jGraphService neo4jGraphService;

    /**
     * 根据自然语言查询模板
     * @param query 查询语句
     * @return 相关模板及其关联模板
     */
    @GetMapping("/by-description")
    public Neo4jSubGraph searchTemplateByDescription(@RequestParam String query) throws IOException {
        try {
            System.out.println("收到的查询语句: " + query); // 添加日志
            // 1. 在 ES 中搜索匹配的模板名称
            List<String> matchedTemplateNames = elasticSearchService.searchEmbedding(query);
            System.out.println("ES搜索结果: " + matchedTemplateNames); // 添加日志
            
            if(matchedTemplateNames.isEmpty()){
                return new Neo4jSubGraph();
            }
            // 2. 构建 Cypher 查询
            String cypher = """
                MATCH (a:Template)
                WHERE a.name IN $names
                WITH a
                OPTIONAL MATCH (a)-[:CONTAIN]->(b:TemplateElement)<-[:CONTAIN]-(c:Template)
                WITH a, c
                WHERE c IS NULL OR a.name <> c.name
                RETURN DISTINCT a, c
                """;
                
            // 3. 执行查询并返回结果
            return neo4jGraphService.executeTemplateSearch(cypher, matchedTemplateNames);
        } catch (Exception e) {
            e.printStackTrace(); // 打印详细错误信息
            throw e;
        }
    }
}