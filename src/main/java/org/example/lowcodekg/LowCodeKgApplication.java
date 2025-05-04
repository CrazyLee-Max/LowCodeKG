package org.example.lowcodekg;

import org.apache.commons.io.FileUtils;
import org.example.lowcodekg.service.KnowledgeExtractorService;
import org.example.lowcodekg.service.TemplateService;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.ExplicitBooleanOptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.util.Objects;

@EnableTransactionManagement
@EnableNeo4jRepositories
@SpringBootApplication
public class LowCodeKgApplication {

    public static void main(String[] args) {

        System.setProperty("file.encoding", "utf-8");
        CmdOption option = new CmdOption();
        CmdLineParser parser = new CmdLineParser(option);

        try {
            if (args.length == 0) {
                return;
            }
            parser.parseArgument(args);
            if (option.exec) {
                SpringApplication.run(LowCodeKgApplication.class, args);
            } else if (!Objects.isNull(option.configPath)) {
                ApplicationContext ctx = SpringApplication.run(LowCodeKgApplication.class, args);
                KnowledgeExtractorService extractor = ctx.getBean(KnowledgeExtractorService.class);
                extractor.execute(FileUtils.readFileToString(new File(option.configPath), "utf-8"));
                System.exit(0);
            } else if (!Objects.isNull(option.templatePath)) {
                ApplicationContext ctx = SpringApplication.run(LowCodeKgApplication.class, args);
                TemplateService templateService = ctx.getBean(TemplateService.class);
                templateService.parseTemplateAndBuildGraph(option.templatePath);
                System.exit(0);
            }
        } catch (CmdLineException cle) {
            System.out.println("Command line error: " + cle.getMessage());
        } catch (Exception e) {
            System.out.println("Error in main: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

class CmdOption {

    /**
     * construct knowledge graph
     */
    @Option(name = "-gen", usage = "Generate a knowledge graph according to the yaml configure file")
    public String configPath = null;

    /**
     * start web service
     */
    @Option(name = "-exec", usage = "Run the web application in localhost", handler = ExplicitBooleanOptionHandler.class)
    public boolean exec = false;

    /**
     * parse template and build graph
     */
    @Option(name = "-template", usage = "Parse template files and build knowledge graph")
    public String templatePath = null;
}