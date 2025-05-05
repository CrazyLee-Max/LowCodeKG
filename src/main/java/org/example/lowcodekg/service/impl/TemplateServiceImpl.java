package org.example.lowcodekg.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.example.lowcodekg.model.dao.neo4j.entity.template.TemplateEntity;
import org.example.lowcodekg.model.dao.neo4j.entity.template.TemplateElementEntity;
import org.example.lowcodekg.model.dao.neo4j.repository.TemplateElementRepo;
import org.example.lowcodekg.model.dao.neo4j.repository.TemplateRepo;
import org.example.lowcodekg.service.LLMGenerateService;
import org.example.lowcodekg.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 模板服务实现类
 */
@Service
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private TemplateRepo templateRepo;

    @Autowired
    private TemplateElementRepo templateElementRepo;

    @Autowired
    private LLMGenerateService llmGenerateService;

    @Override
    public void parseTemplateAndBuildGraph(String templatePath) {
        File templateDir = new File(templatePath);
        if (!templateDir.exists() || !templateDir.isDirectory()) {
            System.out.println("Template directory not found: " + templateDir.getAbsolutePath());
            return;
        }

        // 读取并解析 template.manifest.json 文件
        File manifestFile = new File(templateDir, "template.manifest.json");
        if (!manifestFile.exists()) {
            System.out.println("Manifest file not found: " + manifestFile.getAbsolutePath());
            return;
        }

        try {
            // 解析 manifest 文件
            String manifestContent = FileUtils.readFileToString(manifestFile, StandardCharsets.UTF_8);
            JSONObject manifest = JSON.parseObject(manifestContent);

            // 创建模板根节点
            TemplateEntity templateEntity = new TemplateEntity();
            String name = manifest.getString("name");
            String appKind = manifest.getString("appKind");
            String editorKind = manifest.getString("editorKind");
            String tags = manifest.getJSONArray("tags").toJSONString();
            String description = manifest.getString("description");

            templateEntity.setName(name);
            templateEntity.setIdentifier(manifest.getString("identifier"));
            templateEntity.setAppKind(appKind);
            templateEntity.setTags(tags);
            templateEntity.setCreator(manifest.getString("creator"));
            templateEntity.setPrice(manifest.getInteger("price"));
            templateEntity.setEditorKind(editorKind);

            // 如果描述为空，使用 LLM 生成描述
            if (description == null || description.trim().isEmpty()) {
                description = llmGenerateService.generateTemplateDescription(name, tags, appKind, editorKind);
            }
            templateEntity.setDescription(description);
            
            templateEntity = templateRepo.save(templateEntity);

            // 解析 elements 数组
            JSONArray elements = manifest.getJSONArray("elements");
            for (int i = 0; i < elements.size(); i++) {
                JSONObject element = elements.getJSONObject(i);
                String elementUuid = element.getString("elementUuid");

                // 读取并解析对应的 element 文件
                File elementFile = new File(templateDir + "/elements/" + elementUuid + ".sde");
                if (!elementFile.exists()) {
                    System.out.println("Element file not found: " + elementFile.getAbsolutePath());
                    continue;
                }

                String elementContent = FileUtils.readFileToString(elementFile, StandardCharsets.UTF_8);
                JSONObject elementJson = JSON.parseObject(elementContent);

                // 检查节点是否已存在
                TemplateElementEntity elementEntity = templateElementRepo.findByElementUuid(elementUuid);
                if (elementEntity == null) {
                    // 创建新的 element 节点
                    elementEntity = new TemplateElementEntity();
                    elementEntity.setElementUuid(elementUuid);
                    elementEntity.setKind(element.getString("kind"));
                    elementEntity.setName(elementJson.getString("name"));
                    elementEntity.setTag(elementJson.getString("tag"));
                    elementEntity.setType(elementJson.getString("type"));
                    elementEntity.setGranularity(element.getString("granularity"));
                    elementEntity.setBelongToFileUuid(element.getString("belongToFileUuid"));
                    elementEntity.setContent(elementContent);
                    elementEntity = templateElementRepo.save(elementEntity);
                }

                // 建立与模板的关系
                templateRepo.createRelationOfContainedElement(templateEntity.getId(), elementUuid);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading template files: " + e.getMessage());
        }
    }
}