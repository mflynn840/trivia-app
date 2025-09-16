package com.example.spring_boot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.spring_boot.Model.Question;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query(value = "SELECT * FROM question LIMIT 1 OFFSET :offset", nativeQuery = true)
    Question findByRowNumber(@Param("offset") int offset);
    
    List<Question> findByDifficulty(String difficulty);
    
    List<Question> findByCategory(String category);
    
    List<Question> findByDifficultyAndCategory(String difficulty, String category);
    
    @Query(value = "SELECT * FROM question ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Question findRandomQuestion();
    
    @Query(value = "SELECT * FROM question WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Question findRandomQuestionByDifficulty(@Param("difficulty") String difficulty);
    
    @Query(value = "SELECT * FROM question WHERE category = :category ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Question findRandomQuestionByCategory(@Param("category") String category);
    
    @Query(value = "SELECT * FROM question WHERE difficulty = :difficulty AND category = :category ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Question findRandomQuestionByDifficultyAndCategory(@Param("difficulty") String difficulty, @Param("category") String category);

    @Query(value = "SELECT * FROM question q WHERE (:category IS NULL OR q.category = :category) AND (:difficulty IS NULL OR q.difficulty = :difficulty) ORDER BY RANDOM() LIMIT :count", nativeQuery = true)
    List<Question> findRandomQuestions(
            @Param("count") int count,
            @Param("category") String category,
            @Param("difficulty") String difficulty
    );

    // Count number of questions per category and difficulty
    @Query("SELECT q.category, q.difficulty, COUNT(q) FROM Question q GROUP BY q.category, q.difficulty")
    List<Object[]> countQuestionsByCategoryAndDifficulty();
}
