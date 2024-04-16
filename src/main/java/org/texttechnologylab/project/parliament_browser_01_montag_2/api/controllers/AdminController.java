package org.texttechnologylab.project.parliament_browser_01_montag_2.api.controllers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import freemarker.template.Configuration;
import org.bson.Document;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory.InsightBundestagFactory;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory.InsightBundestagFactory_Impl;
import org.texttechnologylab.project.parliament_browser_01_montag_2.database.MongoDBHandler;
import spark.ModelAndView;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for starting the app's user processes
 * @author Mihai Paun
 */
public class AdminController {
    private static InsightBundestagFactory bundestagFactory;
    private static MongoDBHandler dbHandler;

    /**
     * Constructor for the AdminController class
     * @throws Exception
     */
    public AdminController() throws Exception{
        this.bundestagFactory = new InsightBundestagFactory_Impl();
        this.dbHandler = new MongoDBHandler();
    }

    /**
     * Method for initializing all routes associated with the user management system
     * @param configuration
     */
    public void init(Configuration configuration){
        // route that renders the login page
        Spark.get("login", (request, response) -> {
            return new ModelAndView(null, "login.ftl");
        }, new FreeMarkerEngine(configuration));

        // route that logs the user in
        Spark.post("/login", (request, response) -> {
            // creating a Map for storing the error message
            Map<String, Object> attributes = new HashMap<>();

            // accessing the inputted date
            String username = request.queryParams("username");
            String password = request.queryParams("password");

            // hashing the pw so that we can compare it to the hashed pw stored in the DB
            String hashedPW = hashPW(password);

            // accessing the user's collection
            MongoCollection<Document> usersCollection = dbHandler.getCollection("users");
            // finding the document with _id = username
            Document user = usersCollection.find(Filters.eq("_id", username)).first();

            // checking if the inputted data matches with the DB
            if (user != null && user.getString("password").equals(hashedPW)){
                request.session().attribute("loggedIn", true);
                // getting the previous page and redirecting the user to it
                String previousPage = request.session().attribute("previousPage");
                response.redirect(previousPage);
            } else {
                String errorMsg = "Ungültiger Username oder Passwort! ";
                attributes.put("errorMsg", errorMsg);
                return new ModelAndView(attributes, "login.ftl");
            }
            return null;
        }, new FreeMarkerEngine(configuration));

        // route that renders the logout page
        Spark.get("/logout", (request, response) -> {
            // removing the loggedIn check from the session
            request.session().removeAttribute("loggedIn");
            return new ModelAndView(null, "logout.ftl");
        }, new FreeMarkerEngine(configuration));

        // route that renders the user creation page
        Spark.get("/createUser", (request, response) -> {
            return new ModelAndView(null, "createUser.ftl");
        }, new FreeMarkerEngine(configuration));

        // route that creates the user
        Spark.post("/createUser", (request, response) -> {

            Map<String, Object> attributes = new HashMap<>();

            // access the data inputted by the user
            String username = request.queryParams("username");
            String password = request.queryParams("password");
            String role = request.queryParams("role");


            MongoCollection<Document> userCollection = dbHandler.getCollection("users");
            Document checkUserExists = userCollection.find(Filters.eq("_id", username)).first();


            // if the username is taken, inform the user
            if (checkUserExists != null){
                String message = "Benutzername ist bereits vergeben. ";
                attributes.put("message", message);
                return new ModelAndView(attributes, "createUser.ftl");
            } else {
                // create the user if the username isn't taken
                String hashedPW = hashPW(password);

                Document newUser = new Document("_id", username).append("password", hashedPW).append("role", role);
                userCollection.insertOne(newUser);

                // storing the user data in the session
                request.session().attribute("loggedIn", true);
                request.session().attribute("username", username);
                request.session().attribute("role", role);

                // return the user to the page they last accessed before /createUser
                String previousPage = request.session().attribute("previousPage");
                response.redirect(previousPage);
                return null;
            }
        }, new FreeMarkerEngine(configuration));
    }

    /**
     * Method used for hashing the passwords befor uploading them to the db
     * @param password
     * @return
     * @throws NoSuchAlgorithmException
     */
    public String  hashPW(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes("UTF-16"));
        byte[] digest = md.digest();
        String hashedPW = DatatypeConverter.printHexBinary(digest).toLowerCase();

        return hashedPW;
    }
}
