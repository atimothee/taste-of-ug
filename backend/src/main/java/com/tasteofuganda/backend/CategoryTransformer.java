package com.tasteofuganda.backend;

import com.google.api.server.spi.config.Transformer;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by Timo on 12/23/14.
 */
//public class CategoryTransformer implements Transformer<Category, Long> {
//    static {
//        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
//        ObjectifyService.register(Category.class);
//    }
//
//    public Long transformTo(Category in) {
//        return in.getId();
//    }
//
//    public Category transformFrom(Long in) {
//        //get by id
//
//        return ofy().load().type(Category.class).id(in).now();
//    }
//}

