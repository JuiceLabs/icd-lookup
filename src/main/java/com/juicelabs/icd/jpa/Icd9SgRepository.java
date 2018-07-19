package com.juicelabs.icd.jpa;

import com.juicelabs.icd.models.Icd9Sg;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Icd9SgRepository extends CrudRepository<Icd9Sg, Long> {
    public Icd9Sg findByCode(String code);
}
