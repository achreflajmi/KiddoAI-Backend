package com.example.kiddoai.Entities;

import lombok.*;
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
    private Long id;

    private String name;
    private String description;
    private String code;
    private int ScoreActivities;
    private int feedback;
    private boolean IsLocked;

    private String lessonid;


}
