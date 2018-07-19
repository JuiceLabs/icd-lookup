package com.juicelabs.icd;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.juicelabs.icd.jpa.Icd10Repository;
import com.juicelabs.icd.jpa.Icd9DxRepository;
import com.juicelabs.icd.jpa.Icd9SgRepository;
import com.juicelabs.icd.models.Icd10;
import com.juicelabs.icd.models.Icd9Dx;
import com.juicelabs.icd.models.Icd9Sg;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@SpringBootApplication
@EnableJpaRepositories("com.juicelabs.icd.jpa")
@EntityScan("com/juicelabs/icd/models")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
    public CommandLineRunner d(Icd9DxRepository icd9DxRepository,
                               Icd9SgRepository icd9SgRepository,
                               Icd10Repository icd10Repository) {
        return args -> {
            load(icd9DxRepository);
            load(icd9SgRepository);
            loadIcd10(icd10Repository);
        };
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .apis(RequestHandlerSelectors.basePackage("com"))
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
                                     .validatorUrl(null)
                                     .build();
    }

    private void load(Icd9DxRepository repository) throws IOException {
        File csvFile = new File("src/main/resources/data/ICD9-CMS32_DESC_LONG_SHORT_DX.csv");
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        MappingIterator<Map<String, String>> it = mapper.readerFor(Map.class)
                                                        .with(schema)
                                                        .readValues(csvFile);

        while (it.hasNext()) {
            Map<String, String> rowAsMap = it.next();
            Icd9Dx icd9 = new Icd9Dx();

            icd9.setLongName(rowAsMap.get("LONG_DESCRIPTION"));
            icd9.setShortName(rowAsMap.get("SHORT_DESCRIPTION"));
            icd9.setCode(rowAsMap.get("CODE"));
            repository.save(icd9);
        }
    }

    private void load(Icd9SgRepository repository) throws IOException {
        File csvFile = new File("src/main/resources/data/ICD9-CMS32_DESC_LONG_SHORT_SG.csv");
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        MappingIterator<Map<String, String>> it = mapper.readerFor(Map.class)
                                                        .with(schema)
                                                        .readValues(csvFile);

        while (it.hasNext()) {
            Map<String, String> rowAsMap = it.next();
            Icd9Sg icd9 = new Icd9Sg();

            icd9.setLongName(rowAsMap.get("LONG_DESCRIPTION"));
            icd9.setShortName(rowAsMap.get("SHORT_DESCRIPTION"));
            icd9.setCode(rowAsMap.get("CODE"));
            repository.save(icd9);
        }
    }


    private void loadIcd10(Icd10Repository repository) throws IOException {
        File csvFile = new File("src/main/resources/data/icd10-codes.csv");
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator('|');

        MappingIterator<Map<String, String>> it = mapper.readerFor(Map.class)
                                                        .with(schema)
                                                        .readValues(csvFile);
        it.forEachRemaining(m -> {
            Icd10 icd10 = new Icd10();
            icd10.setCategoryCode(m.get("Category Code"));
            icd10.setDiagnosisCode(m.get("Diagnosis Code"));
            icd10.setFullCode(m.get("Full Code"));
            icd10.setAbbreviatedDescription(m.get("Abbreviated Description"));
            icd10.setFullDescription(m.get("Full Description"));
            icd10.setCategoryTitle(m.get("Category Title"));
            repository.save(icd10);
        });
    }
}
