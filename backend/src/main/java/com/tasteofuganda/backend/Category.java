package com.tasteofuganda.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by Timo on 12/23/14.
 */
@Entity
public class Category{
    @Id Long id;
    String name;
    String color;

}
