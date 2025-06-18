package org.example.lowcodekg.query.service.WorkFlow.model;

/**
 * 工作流描述
 */
public class WorkflowDescription {
    private String name;
    private String identifier;
    private String description;
    
    // getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}