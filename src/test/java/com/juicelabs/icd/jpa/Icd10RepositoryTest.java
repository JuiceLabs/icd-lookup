package com.juicelabs.icd.jpa;

import com.juicelabs.icd.models.Icd10;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@DataJpaTest
public class Icd10RepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private Icd10Repository repository;

    private Icd10 icd10;

    @Before
    public void before() {
        // given
        icd10 = new Icd10();
        icd10.setAbbreviatedDescription("foo");
        icd10.setCategoryCode("cat");
        icd10.setDiagnosisCode("cde");
        icd10.setCategoryTitle("title");
        icd10.setCategoryCode("catcode");
        icd10.setFullCode("fullcode");
        entityManager.persist(icd10);
        entityManager.flush();
    }

    @Test
    public void whenFindByFullCode_thenReturnIcd10() {

        // when
        Icd10 found = repository.findByFullCode("fullcode");

        // then
        assertThat(found.getCategoryTitle())
                .isEqualTo(icd10.getCategoryTitle());
    }

    @Test
    public void whenFindByDianosisCode_thenReturnIcd10() {
        Icd10 found = repository.findByDiagnosisCode("cde");

        assertThat(found.getAbbreviatedDescription())
                .isEqualTo(icd10.getAbbreviatedDescription());
    }

    @Test
    public void whenFindByCategoryCode_thenReturnIcd10List() {
        List<Icd10> found = repository.findByCategoryCode("catcode");

        assertThat(found.size()).isEqualTo(1);
    }
}