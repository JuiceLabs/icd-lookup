package icd;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jpa.Icd10Repository;
import jpa.Icd9DxRepository;
import jpa.Icd9SgRepository;
import models.Icd10;
import models.Icd9Dx;
import models.Icd9Sg;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.TagsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

@SpringBootApplication
@EnableJpaRepositories("jpa")
@EntityScan("models")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    int count = 0;


    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {


//            System.out.println("Let's inspect the beans provided by Spring Boot:");
//
//            String[] beanNames = ctx.getBeanDefinitionNames();
//            Arrays.sort(beanNames);
//            for (String beanName : beanNames) {
//        //        System.out.println(beanName);
//            }
        };
    }

    @Bean
    public CommandLineRunner d(Icd9DxRepository icd9DxRepository,
                               Icd9SgRepository icd9SgRepository,
                               Icd10Repository icd10Repository) {
        return args -> {
            load("src/main/resources/ICD9-CMS32_DESC_LONG_SHORT_DX.csv", icd9DxRepository);
            load("src/main/resources/ICD9-CMS32_DESC_LONG_SHORT_SG.csv", icd9SgRepository);
            loadIcd10(icd10Repository);
        };
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("icd"))
//                .paths(PathSelectors.ant("/foos/*"))

                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Juice Labs ICD Code Lookup API",
                "ICD-9 CM and ICD-10 Code Loockup",
                "API TOS",
                "Terms of service",
                new Contact("Tracy Snell", "www.juicelabs.com", "tjs@juicelabs.com"),
                "License of API", "API license URL", Collections.emptyList());
    }


    @Bean
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                                     .deepLinking(true)
                                     .displayOperationId(false)
                                     .defaultModelsExpandDepth(1)
                                     .defaultModelExpandDepth(1)
                                     .defaultModelRendering(ModelRendering.EXAMPLE)
                                     .displayRequestDuration(false)
                                     .docExpansion(DocExpansion.LIST)
                                     .filter(false)
                                     .maxDisplayedTags(null)
                                     .operationsSorter(OperationsSorter.ALPHA)
                                     .showExtensions(false)
                                     .tagsSorter(TagsSorter.ALPHA)
//                                     .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
                                     .validatorUrl(null)
                                     .build();
    }

    private void load(String fileName, Icd9DxRepository repository) throws IOException {
        File csvFile = new File(fileName);
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader(); // use first row as header; otherwise defaults are fine
        MappingIterator<Map<String,String>> it = mapper.readerFor(Map.class)
                                                       .with(schema)
                                                       .readValues(csvFile);

        count = 0;
        int rows = 0;
        while (it.hasNext()) {
            Map<String,String> rowAsMap = it.next();
            Icd9Dx icd9 = new Icd9Dx();

            icd9.setLongName(rowAsMap.get("LONG_DESCRIPTION"));
            icd9.setShortName(rowAsMap.get("SHORT_DESCRIPTION"));
            icd9.setCode(rowAsMap.get("CODE"));
            repository.save(icd9);

            rows++;
        }
        repository.findAll().forEach(i -> add());
        System.out.println("Count: " + count);
    }

    private void load(String fileName, Icd9SgRepository repository) throws IOException {
        File csvFile = new File(fileName);
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader(); // use first row as header; otherwise defaults are fine
        MappingIterator<Map<String,String>> it = mapper.readerFor(Map.class)
                                                       .with(schema)
                                                       .readValues(csvFile);

        count = 0;
        int rows = 0;
        while (it.hasNext()) {
            Map<String,String> rowAsMap = it.next();
            Icd9Sg icd9 = new Icd9Sg();

            icd9.setLongName(rowAsMap.get("LONG_DESCRIPTION"));
            icd9.setShortName(rowAsMap.get("SHORT_DESCRIPTION"));
            icd9.setCode(rowAsMap.get("CODE"));
            repository.save(icd9);

            rows++;
        }
        System.out.println("rows: " + rows);
    }


    private void loadIcd10(Icd10Repository repository) throws IOException {
        File csvFile = new File("src/main/resources/icd10-codes.csv");
        CsvMapper mapper = new CsvMapper();
//        CsvSchema schema = mapper.schemaFor(Icd10.class);
        CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator('|'); // use first row as header; otherwise defaults are fine

        MappingIterator<Map<String,String>> it = mapper.readerFor(Map.class)
                                                       .with(schema)
                                                       .readValues(csvFile);
        count = 0;
        it.forEachRemaining(m -> {
            Icd10 icd10 = new Icd10();
            icd10.setCategoryCode(m.get("Category Code"));
            icd10.setDiagnosisCode(m.get("Diagnosis Code"));
            icd10.setFullCode(m.get("Full Code"));
            icd10.setAbbreviatedDescription(m.get("Abbreviated Description"));
            icd10.setFullDescription(m.get("Full Description"));
            icd10.setCategoryTitle(m.get("Category Title"));
            repository.save(icd10);
            add();
        });

        System.out.println("10 count = " + count);
    }

    private void add() {
        count++;
    }

}
