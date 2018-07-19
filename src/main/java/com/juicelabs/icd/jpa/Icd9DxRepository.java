package com.juicelabs.icd.jpa;

import com.juicelabs.icd.models.Icd9Dx;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Icd9DxRepository extends CrudRepository<Icd9Dx, Long> {
    public Icd9Dx findByCode(String code);
}
