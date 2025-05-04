package org.example.lowcodekg.controller;

import org.example.lowcodekg.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模板解析控制器
 */
@RestController
@RequestMapping("/api/template")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    /**
     * 解析模板文件并构建知识图谱
     * @param templatePath 模板文件路径
     * @return 处理结果
     */
    @PostMapping("/parse")
    public ResponseEntity<String> parseTemplateAndBuildGraph(@RequestParam String templatePath) {
        try {
            templateService.parseTemplateAndBuildGraph(templatePath);
            return ResponseEntity.ok("模板解析成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("模板解析失败：" + e.getMessage());
        }
    }
}