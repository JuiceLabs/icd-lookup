package com.juicelabs.icd;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
public class IcdControllerIT {

    @LocalServerPort
    private int port;

    private String base;

    @Autowired
    private TestRestTemplate template;

    @Before
    public void setUp() {
        base = "http://localhost:" + port + "/api/v1/";
    }

    @Test
    public void testIcd9Sg() {
        String json = "{\"code\":\"0056\",\"longName\":\"Insertion or replacement of implantable pressure sensor with lead for intracardiac or great vessel hemodynamic monitoring\",\"shortName\":\"Ins/rep sens-crd/vsl mtr\"}";
        ResponseEntity<String> response = template.getForEntity(
                base + "icd9-cm-sg/0056",
                String.class);
        assertThat(response.getBody(), equalTo(json));
    }
}
