package com.example.controler;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RestController
@RequestMapping("/hello")
public class HelloController {
  @PersistenceContext
  private EntityManager entityManager;

    
  @RequestMapping("/world")
  public String world() {
    return "Hello world 2019";
  }

}