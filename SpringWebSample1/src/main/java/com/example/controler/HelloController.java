package com.example.controler;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entities.MyDataEntity;
import com.example.entities.SampleTableEntity;
import com.example.repositories.MyDataRepository;
import com.example.repositories.SampleTableRepository;

@RestController
@RequestMapping("/hello")
public class HelloController {
  @PersistenceContext
    private EntityManager entityManager;
  
  @Autowired
  private SampleTableRepository sampleTable;

  @Autowired
  private MyDataRepository myData;
    
  @RequestMapping("/world")
  public String world() {
    return "Hello world 2017";
  }

  @RequestMapping("/world2")
  public String world2() {
    List<SampleTableEntity> l1 = sampleTable.findAll();
    List<MyDataEntity> l2 = myData.findAll();
    return "HW2 table1=" + l1.size() + " table2=" + l2.size();
  }
}