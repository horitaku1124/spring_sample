package com.example.demo.entities

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table


@Entity
@Table(name="mydata") class MyDataEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column
  private var id: Long = 0

  @Column
  private var name: String = ""
}