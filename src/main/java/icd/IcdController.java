package icd;

import jpa.Icd10Repository;
import jpa.Icd9DxRepository;
import jpa.Icd9SgRepository;
import models.Icd10;
import models.Icd9Dx;
import models.Icd9Sg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class IcdController {

    private Icd9DxRepository icd9DxRepository;
    private Icd9SgRepository icd9SgRepository;
    private Icd10Repository  icd10Repository;

    @Autowired
    public IcdController(Icd9DxRepository icd9DxRepository,
                         Icd9SgRepository icd9SgRepository,
                         Icd10Repository icd10Repository) {
        this.icd9DxRepository = icd9DxRepository;
        this.icd9SgRepository = icd9SgRepository;
        this.icd10Repository = icd10Repository;
    }

    @GetMapping("icd9-cm/{code}")
    public Icd9Dx getIcd9Dx(@PathVariable("code") String code) {
        Icd9Dx i = icd9DxRepository.findByCode(strip(code));
        if (i == null) {
            throw new CodeNotFoundException();
        }
        return i;
    }

    @GetMapping("icd9-cm-sg/{code}")
    public Icd9Sg getIcd9Sg(@PathVariable("code") String code) {
        Icd9Sg i = icd9SgRepository.findByCode(strip(code));
        if (i == null) {
            throw new CodeNotFoundException();
        }
        return i;
    }

    @GetMapping("icd10/{code}")
    public Icd10 getIcd10(@PathVariable("code") String code) {
        Icd10 i = icd10Repository.findByFullCode(strip(code));
        if (i == null) {
            throw new CodeNotFoundException();
        }
        return i;
    }

    @GetMapping("icd10/category/{code}")
    public List<Icd10> getIcd10Category(@PathVariable("code") String code) {
        List<Icd10> i = icd10Repository.findByCategoryCode(strip(code));
        if (i == null || i.isEmpty()) {
            throw new CodeNotFoundException();
        }
        return i;
    }

    private String strip(String s) {
        return s.replace(".", "");
    }
}
