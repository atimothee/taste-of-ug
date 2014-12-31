
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    %>


<html>
<head>
    <title>Add a recipe</title>
    <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css">
    <link rel="stylesheet"
          href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap-theme.min.css">
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <script src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container">
    <div class="col-md-6">
<h3>Add Recipe</h3>
<form action="<%= blobstoreService.createUploadUrl("/upload") %>" method="post" enctype="multipart/form-data">
<div class="form-group">
<label>Name</label><input class="form-control" type="text" name="name"/>
</div>
<div class="form-group">
<label>Description</label><textarea class="form-control" name="description"></textarea>
</div>
        <div class="form-group">
            <label>Ingredients</label><textarea class="form-control" name="ingredients"></textarea>
        </div>
<div class="form-group">
<label>Directions</label><textarea class="form-control" name="directions"></textarea>
</div>
<div class="form-group">
<label>Category</label><input class="form-control" type="text" name="categoryId"/>
    </div>
<div class="form-group">
    <label>Image</label>
<input class="form-control" type="file" name="image"/>
</div>
<div class="form-group">
<input class="btn btn-primary" type="submit" value="Submit"/>
    </div>
        <div class="form-group">
            <label>Nutrition Facts</label><textarea class="form-control" name="nutritionFacts"></textarea>
        </div>
</form>
</div>
</div>
</body>
</html>