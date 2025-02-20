package com.example.kiddoai.Repositories;

import com.example.kiddoai.Entities.Subject;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubjectRepository extends MongoRepository<Subject, String> {
}
