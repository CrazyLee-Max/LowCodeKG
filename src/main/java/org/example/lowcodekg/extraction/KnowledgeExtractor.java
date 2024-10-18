package org.example.lowcodekg.extraction;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 定义知识挖掘的抽象类
 */
public abstract class KnowledgeExtractor {

    @Getter
    @Setter
    private String graphDir;

    @Getter
    @Setter
    private String dataDir;

    @Getter
    private GraphDatabaseService db = null;

    public static void execute(List<ExtractorConfig> extractorConfigList) {
        for (ExtractorConfig config : extractorConfigList) {
            System.out.println(config.getClassName() + " start ...");
            KnowledgeExtractor extractor = null;
            try {
                extractor = (KnowledgeExtractor) Class.forName(config.getClassName()).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            extractor.setGraphDir(config.getGraphDir());
            extractor.setDataDir(config.getDataDir());
            try {
                extractor.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(config.getClassName() + " finished.");
        }
    }

    public static void executeFromYaml(String yamlStr) {
        Yaml yaml = new Yaml();
        Map<String, Object> ret = yaml.load(yamlStr);
        String graphDir = (String) ret.get("graphDir");
        ret.remove("graphDir");
        boolean increment = false;
        if (ret.containsKey("increment") && (boolean)ret.get("increment")){
            increment = true;
            ret.remove("increment");
        }
        List<ExtractorConfig> configs = new ArrayList<>();
        for (String key : ret.keySet()) {
            configs.add(new ExtractorConfig(key, graphDir, (String) ret.get(key)));
        }
        if (new File(graphDir).exists() && !increment){
            try {
                FileUtils.deleteDirectory(new File(graphDir));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        execute(configs);
    }

    public static void main(String[] args) {
        try {
            KnowledgeExtractor.executeFromYaml(FileUtils.readFileToString(new File(args[0]), "utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isBatchInsert() {
        return false;
    }

    public abstract void extraction();

    public void execute() throws IOException {
        //TODO: neo4j config
        DatabaseManagementService dbms = new DatabaseManagementServiceBuilder(new File(graphDir).toPath()).build();
        db = dbms.database("neo4j");
        this.extraction();
        dbms.shutdown();
    }
}
