package com.tasteofuganda.backend;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobstoreServicePb;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.googlecode.objectify.Key;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.tasteofuganda.backend.OfyService.ofy;

/**
 * Created by Timo on 12/23/14.
 */
public class Upload extends HttpServlet{
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    private static final String API_KEY = System.getProperty("gcm.api.key");
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
        List<BlobKey> blobKeys = blobs.get("image");

        if (blobKeys == null || blobKeys.isEmpty()) {
            res.sendRedirect("/");
        } else {
            Recipe r = new Recipe();
            r.category = Key.create(Category.class, req.getParameter("categoryId"));
            r.description = req.getParameter("description");
            r.directions = new Text(req.getParameter("directions"));
            r.ingredients = new Text(req.getParameter("ingredients"));
            r.nutritionFacts = new Text(req.getParameter("nutritionFacts"));
            r.name = req.getParameter("name");
            ImagesService imagesService = ImagesServiceFactory.getImagesService();
            ServingUrlOptions sevOptions = ServingUrlOptions.Builder.withBlobKey(blobKeys.get(0));
            res.sendRedirect(imagesService.getServingUrl(sevOptions));
            r.image = blobKeys.get(0);
            //r.thumbnail = imagesService.getServingUrl(sevOptions);
            //r.large_image = imagesService.getServingUrl(sevOptions);
            //new RecipeEndpoint().insert(r);
            Message message = new Message.Builder().addData("id", new RecipeEndpoint().insert(r).id.toString()).build();
            Sender sender = new Sender(API_KEY);
            List<RegistrationRecord> records = ofy().load().type(RegistrationRecord.class).list();
            for (RegistrationRecord record : records) {
                sender.send(message, record.getRegId(), 5);
            }
            res.sendRedirect("/serve?blob-key=" + blobKeys.get(0).getKeyString());
        }
    }

}
