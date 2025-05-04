package org.example.lowcodekg.model.dao.neo4j.repository;

import org.example.lowcodekg.model.dao.neo4j.entity.template.TemplateEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepo extends Neo4jRepository<TemplateEntity, Long> {

    @Query("MATCH (s:Template) WHERE id(s)=$sid " +
            "MATCH (e:TemplateElement) WHERE e.elementUuid=$elementUuid " +
            "MERGE (s)-[:CONTAIN]->(e)")
    void createRelationOfContainedElement(Long sid, String elementUuid);
}