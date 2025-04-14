package com.example.kiddoai.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "lesson")  // Collection name in MongoDB
public class Lesson {
    @Id
    private Long id;

    private String name;
    private String description;
    private int level;
    private boolean IsLocked;

    private List<String> activitieIds;

    private String subject;

    public Long getId() {
        return id;
    }
}