package com.example;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.repositories.SampleTableRepository;


@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/db-schema.sql")
@Sql("/db-test-data.sql")
public class SpringTests1 {
  @Autowired
  private SampleTableRepository sampleTableRepo;

  @Before
  public void setup() {
  }
  
  @Test
  public void contextLoads() {
    System.out.println(sampleTableRepo.findAll().size());
    assertEquals(1, sampleTableRepo.findAll().size());
  }
}
