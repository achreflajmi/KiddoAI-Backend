package com.example.kiddoai.Entities;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "classes")
public class Classe {
    @Id
    private ObjectId id;
    private String name; // e.g. "1st Grade"
    private List<String> subjectIds;
}
