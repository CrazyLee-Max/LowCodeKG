package org.example.lowcodekg.query.service.WorkFlow.model;

/**
 * 输入参数
 */
public class InputParameter {
    private String name;
    private String dataType;
    private String description;
    
    // getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDataType() {
        return dataType;
    }
    
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}