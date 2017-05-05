package com.example.controler;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {
	@PersistenceContext
    private EntityManager entityManager;
    
	@RequestMapping("/world")
	public String world() {
		return "Hello world 2017";
	}

	@RequestMapping("/world2")
	public String world2() {
		Query query = entityManager.createNativeQuery("select 'name' from mydata LIMIT 1");
		Object o = query.getSingleResult();
		return "HW2 " + o.toString();
	}
}