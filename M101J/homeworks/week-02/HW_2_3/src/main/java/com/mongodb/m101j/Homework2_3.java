package com.mongodb.m101j;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.ascending;

/**
 * Created by borgymanotoy on 6/20/17.
 */
public class Homework2_3 {
    public static void main(String[] args) {
        MongoClient client = new MongoClient();
        MongoDatabase database = client.getDatabase("students");
        MongoCollection<Document> cGrades = database.getCollection("grades");

        Bson filter = eq("type", "homework");
        //Bson sort = ascending("student_id", "score");

        Bson sort = ascending("student_id", "score");

        MongoCursor<Document> cursor = cGrades.find(filter).sort(sort).iterator();

        try {
            int studentId = -1;
            while(cursor.hasNext()){
                Document d = cursor.next();
                if(studentId != d.getInteger("student_id")){
                    studentId = d.getInteger("student_id");
                    System.out.println("[STUDENT-ID]: " + studentId + "\t[DELETE-SCORE]: " + d.getDouble("score"));
                    cGrades.deleteOne(d);
                }
            }
        }
        finally {
            cursor.close();
        }

        client.close();
    }
}
