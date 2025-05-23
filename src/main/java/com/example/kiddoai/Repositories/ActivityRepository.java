package com.example.kiddoai.Repositories;

import com.example.kiddoai.Entities.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends MongoRepository<Activity, String> {
    @Query(value = "{ 'id' : ?0 }", fields = "{ 'code' : 1, '_id' : 0 }")
    Activity findCodeById(Long id);

    @Query("{'_id': ?0}")
    @Update("{ '$set': { 'code': ?1 } }")
    void updateActivityCodeById(Long id, String newCode);

    @Query(value = "{ 'id' : ?0 }", fields = "{ 'problems' : 1, '_id' : 0 }")
    Activity findProblemsById(Long id);

    Optional<Activity> findTopByOrderByIdDesc();

    @Query("{ 'lessonId': ?0 }")
    List<Activity> findTop5ByLessonIdOrderByCreatedAtDesc(String lessonId, org.springframework.data.domain.Sort sort);

    Optional<Activity> findTopByUserIdOrderByIdDesc(String attr0);
}