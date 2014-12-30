package com.tasteofuganda.backend;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.images.Transform;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Timo on 12/23/14.
 */
public class Serve extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
        ImagesService imagesService = ImagesServiceFactory.getImagesService();

//        Image oldImage = ImagesServiceFactory.makeImageFromBlob(blobKey);
//        Transform resize = ImagesServiceFactory.makeResize(200, 300);
//
//        Image newImage = imagesService.applyTransform(resize, oldImage);
//
//        byte[] newImageData = newImage.getImageData();
        blobstoreService.serve(blobKey, res);
        ServingUrlOptions sevOptions = ServingUrlOptions.Builder.withBlobKey(blobKey);
        res.sendRedirect(imagesService.getServingUrl(sevOptions));
    }
}

