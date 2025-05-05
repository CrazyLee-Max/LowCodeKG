package org.example.lowcodekg.model.dao.neo4j.repository;

import org.example.lowcodekg.model.dao.neo4j.entity.template.TemplateElementEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateElementRepo extends Neo4jRepository<TemplateElementEntity, Long> {

    @Query("MATCH (e:TemplateElement) WHERE e.elementUuid=$elementUuid RETURN e")
    TemplateElementEntity findByElementUuid(String elementUuid);
}