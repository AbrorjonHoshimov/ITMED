package com.example.test.repository;

import com.example.test.entity.DocumentCSV;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocementCSVRepository extends JpaRepository<DocumentCSV,String> {
    List<DocumentCSV> findByOrderByIdAsc();
}
