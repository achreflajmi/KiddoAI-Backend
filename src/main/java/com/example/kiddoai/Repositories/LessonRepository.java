package com.example.kiddoai.Repositories;

import com.example.kiddoai.Entities.Lesson;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LessonRepository extends MongoRepository<Lesson, String> {
    List<Lesson> findAllBySubject(String subject);
}
