package com.example.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entities.MyDataEntity;

@Repository
public interface MyDataRepository  extends JpaRepository<MyDataEntity, Long> {}