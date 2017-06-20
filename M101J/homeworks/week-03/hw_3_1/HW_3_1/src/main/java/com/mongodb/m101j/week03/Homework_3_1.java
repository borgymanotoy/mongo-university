package com.mongodb.m101j.week03;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.ascending;

/**
 * Created by borgymanotoy on 6/19/17.
 */
public class Homework_3_1 {
    public static void main(String[] args) {
        useLists();
    }

    private static void useLists(){
        MongoClient client = new MongoClient();
        MongoDatabase database = client.getDatabase("school");
        MongoCollection<Document> cStudents = database.getCollection("students");

        Bson sort = ascending("_id");

        List<Document> listStudents = cStudents.find().sort(sort).into(new ArrayList<Document>());

        for(Document student : listStudents){
            //Get Lowest Homework Score
            double homeworkGrade = 0;
            List<Document> scores = (List<Document>) student.get("scores");
            for(Document score : scores){
                if("homework".equalsIgnoreCase(score.getString("type"))){
                    if(0 == homeworkGrade)
                        homeworkGrade = score.getDouble("score");
                    else if(homeworkGrade > score.getDouble("score"))
                        homeworkGrade = score.getDouble("score");
                }
            }

            //Create new list of scores without the lowest homework
            List<Document> newScores = new ArrayList<Document>();
            for(Document score : (List<Document>) student.get("scores")){
                if(!score.getString("type").equalsIgnoreCase("homework") || homeworkGrade != score.getDouble("score")){
                    newScores.add(score);
                }
            }

            //Create filter and new array of scores Document objects
            Bson filter = eq("_id", student.getInteger("_id"));
            Document dNewScores = new Document("$set", new Document("scores", newScores));

            //Update the collection using the filter and set the new list of scores
            cStudents.updateOne(filter, dNewScores);
        }

        client.close();
    }

    private static void useCursors(){
        MongoClient client = new MongoClient();
        MongoDatabase database = client.getDatabase("school");
        MongoCollection<Document> cStudents = database.getCollection("students");

        Bson sort = ascending("_id");

        MongoCursor<Document> cursor = cStudents.find().sort(sort).iterator();
        List<Document> listStudents = cStudents.find().sort(sort).into(new ArrayList<Document>());

        try {
            while(cursor.hasNext()){
                Document student = cursor.next();

                //Get Lowest Homework Score
                double homeworkGrade = 0;
                for(Document score : (List<Document>) student.get("scores")){
                    if(score.getString("type").equalsIgnoreCase("homework")){
                        if(0 == homeworkGrade)
                            homeworkGrade = score.getDouble("score");
                        else if(homeworkGrade > score.getDouble("score"))
                            homeworkGrade = score.getDouble("score");
                    }
                }

                //Create new list of scores without the lowest homework
                List<Document> newScores = new ArrayList<Document>();
                for(Document score : (List<Document>) student.get("scores")){
                    if(!score.getString("type").equalsIgnoreCase("homework") || homeworkGrade != score.getDouble("score")){
                        newScores.add(score);
                    }
                }

                //Create filter and new array of scores Document objects
                Bson filter = eq("_id", student.getInteger("_id"));
                Document dNewScores = new Document("$set", new Document("scores", newScores));

                //Update the collection using the filter and set the new list of scores
                cStudents.updateOne(filter, dNewScores);
            }
        }
        finally {
            cursor.close();
        }

        client.close();
    }
}
