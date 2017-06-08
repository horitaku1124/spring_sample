package com.example.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entities.SampleTableEntity;

@Repository
public interface SampleTableRepository  extends JpaRepository<SampleTableEntity, Long> {}