package org.example.lowcodekg.model.dao.neo4j.entity.template;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Neo4j 实体节点：模板
 */
@Node("Template")
@Data
@NoArgsConstructor
public class TemplateEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Property("name")
    private String name;

    @Property("identifier")
    private String identifier;

    @Property("appKind")
    private String appKind;

    @Property("description")
    private String description;

    @Property("tags")
    private String tags;

    @Property("creator")
    private String creator;

    @Property("price")
    private Integer price;

    @Property("editorKind")
    private String editorKind;

    @Property("elementsStr")
    private String elementsStr;

    @Relationship(type = "CONTAIN", direction = Relationship.Direction.OUTGOING)
    private List<TemplateElementEntity> elements = new ArrayList<>();
}