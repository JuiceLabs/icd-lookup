package jpa;

import models.Icd9;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Icd9Repository extends CrudRepository<Icd9, Long> {
    public Icd9 findByCode(String code);
}
