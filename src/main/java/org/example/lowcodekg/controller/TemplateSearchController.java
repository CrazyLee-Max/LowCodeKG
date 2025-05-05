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
        // 1. 在 ES 中搜索匹配的模板名称
        List<String> matchedTemplateNames = elasticSearchService.searchEmbedding(query);
        
        // 2. 构建 Cypher 查询，查找匹配的根节点以及与其通过 contain 关系相关的根节点
        String cypher = """
            MATCH (a:Template)-[:contain]->(b)<-[:contain]-(c:Template)
            WHERE a.name IN $names
            RETURN DISTINCT a, c
            """;
            
        // 3. 执行查询并返回结果
        return neo4jGraphService.executeTemplateSearch(cypher, matchedTemplateNames);
    }
}