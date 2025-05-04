package org.example.lowcodekg.model.dao.neo4j.entity.template;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Neo4j 实体节点：模板元素
 */
@Node("TemplateElement")
@Data
@NoArgsConstructor
public class TemplateElementEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Property("elementUuid")
    private String elementUuid;

    @Property("kind")
    private String kind;

    @Property("name")
    private String name;

    @Property("tag")
    private String tag;

    @Property("type")
    private String type;

    @Property("granularity")
    private String granularity;

    @Property("belongToFileUuid")
    private String belongToFileUuid;

    @Property("content")
    private String content;

    @Relationship(type = "DEPEND_ON", direction = Relationship.Direction.OUTGOING)
    private List<TemplateElementEntity> dependencies = new ArrayList<>();
}