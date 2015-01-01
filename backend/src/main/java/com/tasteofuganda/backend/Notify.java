package com.tasteofuganda.backend;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.tasteofuganda.backend.OfyService.ofy;

/**
 * Created by Timo on 1/1/15.
 */
public class Notify extends HttpServlet{
    private static final String API_KEY = System.getProperty("gcm.api.key");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Collection<Recipe> recipes = new RecipeEndpoint().list(null, null).getItems();

        Random random = new Random();
        if(!recipes.isEmpty()){
            Recipe r = (Recipe)recipes.toArray()[random.nextInt(recipes.size()-1)];
            Message.Builder builder = new Message.Builder();
            builder.addData("id", r.id.toString());
            builder.addData("type", "notification");
            builder.addData("resource_type", "recipe");
            builder.addData("message", "Your Recipe for the week - "+r.getName());
            Message message = builder.build();
            Sender sender = new Sender(API_KEY);
            List<RegistrationRecord> records = ofy().load().type(RegistrationRecord.class).list();
            List<String> regIds = new ArrayList<String>();
            for (RegistrationRecord record : records) {
                regIds.add(record.getRegId());
            }
            sender.send(message, regIds, 10);
            resp.setStatus(200);
        }
        else {
            //do nothing
            resp.setStatus(200);
        }


    }
}
