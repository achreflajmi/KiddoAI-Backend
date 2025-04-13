package com.example.kiddoai.Repositories;

import com.example.kiddoai.Entities.Classe;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClasseRepository extends MongoRepository<Classe, ObjectId> {}

