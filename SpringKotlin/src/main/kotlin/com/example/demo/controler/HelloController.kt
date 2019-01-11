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
  private lateinit var entityManager:EntityManager

  @Autowired
  private lateinit var sampleTable:SampleTableRepository

  @Autowired
  private lateinit var myData: MyDataRepository

  @RequestMapping("/world")
  fun world() :String{
    return "Hello Kotlin 2019";
  }

  @RequestMapping("/world2")
  fun world2():String {
    val l1: MutableList<SampleTableEntity>? = sampleTable.findAll()
    val l2: kotlin.collections.List<MyDataEntity> = myData.findAll()
    return "HW2 table1=" + l1?.size + " table2=" + l2.size
  }
}