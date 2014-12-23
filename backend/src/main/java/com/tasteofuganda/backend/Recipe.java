package com.tasteofuganda.backend;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by Timo on 12/23/14.
 */
@Entity

public class Recipe {
    @Id
    Long id;
    String name;
    String description;
    String directions;
    String youtube_url;
    Long category_id;
}


