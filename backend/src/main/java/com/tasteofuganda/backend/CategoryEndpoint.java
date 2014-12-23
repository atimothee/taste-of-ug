package com.tasteofuganda.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "categoryApi",
        version = "v1",
        resource = "category",
        namespace = @ApiNamespace(
                ownerDomain = "backend.tasteofuganda.com",
                ownerName = "backend.tasteofuganda.com",
                packagePath = ""
        )
)
public class CategoryEndpoint {

    private static final Logger logger = Logger.getLogger(CategoryEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Category.class);
    }

    /**
     * Returns the {@link Category} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Category} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "category/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Category get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting Category with ID: " + id);
        Category category = ofy().load().type(Category.class).id(id).now();
        if (category == null) {
            throw new NotFoundException("Could not find Category with ID: " + id);
        }
        return category;
    }

    /**
     * Inserts a new {@code Category}.
     */
    @ApiMethod(
            name = "insert",
            path = "category",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Category insert(Category category) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that category.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(category).now();
        logger.info("Created Category.");

        return ofy().load().entity(category).now();
    }

    /**
     * Updates an existing {@code Category}.
     *
     * @param id       the ID of the entity to be updated
     * @param category the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Category}
     */
    @ApiMethod(
            name = "update",
            path = "category/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Category update(@Named("id") Long id, Category category) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(category).now();
        logger.info("Updated Category: " + category);
        return ofy().load().entity(category).now();
    }

    /**
     * Deletes the specified {@code Category}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Category}
     */
    @ApiMethod(
            name = "remove",
            path = "category/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Category.class).id(id).now();
        logger.info("Deleted Category with ID: " + id);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "category",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Category> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Category> query = ofy().load().type(Category.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Category> queryIterator = query.iterator();
        List<Category> categoryList = new ArrayList<Category>(limit);
        while (queryIterator.hasNext()) {
            categoryList.add(queryIterator.next());
        }
        return CollectionResponse.<Category>builder().setItems(categoryList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(Category.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Category with ID: " + id);
        }
    }
}