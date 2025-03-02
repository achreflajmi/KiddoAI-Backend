package com.example.kiddoai.Entities;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "subject")  // Collection name in MongoDB
public class Subject {
    @Id
    private ObjectId id;
    private String name;
    private int AdvancementLevel;

    private List<String> lessonIds;

}