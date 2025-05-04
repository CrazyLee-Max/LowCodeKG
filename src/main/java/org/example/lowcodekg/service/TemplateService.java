package org.example.lowcodekg.service;

/**
 * 模板服务接口
 * 负责解析模板文件并构建知识图谱
 */
public interface TemplateService {
    
    /**
     * 解析模板文件并构建知识图谱
     * @param templatePath 模板文件路径
     */
    void parseTemplateAndBuildGraph(String templatePath);
}