package com.juicelabs.icd;

import com.juicelabs.icd.jpa.Icd10Repository;
import com.juicelabs.icd.jpa.Icd9DxRepository;
import com.juicelabs.icd.jpa.Icd9SgRepository;
import com.juicelabs.icd.models.Icd10;
import com.juicelabs.icd.models.Icd10Search;
import com.juicelabs.icd.models.Icd9Dx;
import com.juicelabs.icd.models.Icd9Sg;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1",
        produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
        consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class IcdController {

    @Autowired
    private Icd10Search icd10Search;

    private Icd9DxRepository icd9DxRepository;
    private Icd9SgRepository icd9SgRepository;
    private Icd10Repository icd10Repository;

    @Autowired
    public IcdController(Icd9DxRepository icd9DxRepository,
                         Icd9SgRepository icd9SgRepository,
                         Icd10Repository icd10Repository) {
        this.icd9DxRepository = icd9DxRepository;
        this.icd9SgRepository = icd9SgRepository;
        this.icd10Repository = icd10Repository;
    }

    @GetMapping("icd9-cm/{code}")
    @ApiOperation(value = "Search for ICD9-CM diagnosis code {code}",
            notes = "code format can be either with or without . notation. IE 011.00 or 01100")
    public Icd9Dx getIcd9Dx(@PathVariable("code") String code) {
        Icd9Dx i = icd9DxRepository.findByCode(strip(code));
        if (i == null) {
            throw new CodeNotFoundException();
        }
        return i;
    }

    @GetMapping("icd9-cm-sg/{code}")
    @ApiOperation(value = "Search for ICD9-CM procedure code {code}")
    public Icd9Sg getIcd9Sg(@PathVariable("code") String code) {
        Icd9Sg i = icd9SgRepository.findByCode(strip(code));
        if (i == null) {
            throw new CodeNotFoundException();
        }
        return i;
    }

    @GetMapping("icd10/{code}")
    @ApiOperation(value = "Search for ICD10 full code")
    public Icd10 getIcd10(@PathVariable("code") String code) {
        Icd10 i = icd10Repository.findByFullCode(strip(code));
        if (i == null) {
            throw new CodeNotFoundException();
        }
        return i;
    }

    @GetMapping("icd10/category/{code}")
    @ApiOperation(value = "Search for all ICD10 codes in category")
    public List<Icd10> getIcd10Category(@PathVariable("code") String code) {
        List<Icd10> i = icd10Repository.findByCategoryCode(strip(code));
        if (i == null || i.isEmpty()) {
            throw new CodeNotFoundException();
        }
        return i;
    }

    @GetMapping("icd10/search/{text}")
    public List<Icd10> searchIcd10Descroption(@PathVariable("text") String text) {
        return icd10Search.search(text);
    }

    private String strip(String s) {
        return s.replace(".", "");
    }
}
