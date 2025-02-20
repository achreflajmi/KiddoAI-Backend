package com.example.kiddoai.Repositories;

import com.example.kiddoai.Entities.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActivityRepository extends MongoRepository<Activity, String> {
}
