package com.juicelabs.icd.jpa;

import com.juicelabs.icd.models.Icd10;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Icd10Repository extends CrudRepository<Icd10, Long> {
    public Icd10 findByDiagnosisCode(String code);
    public Icd10 findByFullCode(String code);
    public List<Icd10> findByCategoryCode(String code);
}
