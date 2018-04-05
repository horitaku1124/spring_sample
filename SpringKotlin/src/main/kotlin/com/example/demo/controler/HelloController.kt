package com.example.demo.controler


import com.example.demo.entities.MyDataEntity
import com.example.demo.entities.SampleTableEntity
import com.example.demo.repositories.MyDataRepository
import com.example.demo.repositories.SampleTableRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@RestController
@RequestMapping("/hello")
public class HelloController {
  @PersistenceContext
  lateinit private var entityManager:EntityManager

  @Autowired
  lateinit private var sampleTable:SampleTableRepository

  @Autowired
  lateinit private var myData: MyDataRepository

  @RequestMapping("/world")
  fun world() :String{
    return "Hello Kotlin 2017";
  }

  @RequestMapping("/world2")
  fun world2():String {
    val l1: MutableList<SampleTableEntity>? = sampleTable.findAll()
    val l2: kotlin.collections.List<MyDataEntity> = myData.findAll()
    return "HW2 table1=" + l1?.size + " table2=" + l2.size
  }
}