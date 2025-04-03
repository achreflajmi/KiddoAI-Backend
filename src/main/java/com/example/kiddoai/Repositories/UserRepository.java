package com.example.kiddoai.Repositories;

import com.example.kiddoai.Entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // Trouver un utilisateur par email
    Optional<User> findByEmail(String email);
    User findByThreadId(String threadId);

    // Trouver un utilisateur par rôle
}
