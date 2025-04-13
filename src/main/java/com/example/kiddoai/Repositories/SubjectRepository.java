package com.example.kiddoai.Repositories;

import com.example.kiddoai.Entities.Subject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface SubjectRepository extends MongoRepository<Subject, String> {
    Subject findByName(String name);

    List<Subject> getSubjectsByClasseName(String classeName);
}
