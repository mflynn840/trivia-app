package com.example.spring_boot.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.spring_boot.Model.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query(value = "SELECT * FROM question LIMIT 1 OFFSET :offset", nativeQuery = true)
    Question findByRowNumber(@Param("offset") int offset);
}
