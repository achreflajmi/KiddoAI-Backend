package com.example.kiddoai.Repositories;

import com.example.kiddoai.Entities.Lesson;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LessonRepository extends MongoRepository<Lesson, String> {
}
