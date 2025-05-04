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
@Document(collection = "activity")  // Collection name in MongoDB
public class Activity {

    @Id
    private ObjectId id;

    private String name;
    private String description;
    private double accuracy;
    private int ScoreActivities;
    private int feedback;
    private boolean IsLocked;
    private String problems;
    private Long lessonid;
    private String userId;


}
