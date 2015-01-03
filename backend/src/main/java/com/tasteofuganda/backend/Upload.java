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
import com.googlecode.objectify.ObjectifyService;

import java.io.IOException;
import java.util.ArrayList;
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
            r.category  = Key.create(Category.class, Long.valueOf(req.getParameter("categoryId")));
            r.description = req.getParameter("description");
            r.directions = new Text(req.getParameter("directions"));
            r.ingredients = new Text(req.getParameter("ingredients"));
            r.nutritionFacts = new Text(req.getParameter("nutritionFacts"));
            r.name = req.getParameter("name");
            ImagesService imagesService = ImagesServiceFactory.getImagesService();
            ServingUrlOptions sevOptions = ServingUrlOptions.Builder.withBlobKey(blobKeys.get(0));
            //r.image = blobKeys.get(0);
            r.image_url = imagesService.getServingUrl(sevOptions);
            res.sendRedirect(r.image_url);
        }
    }

}
