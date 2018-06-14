package dao;

import models.Foodtype;
import models.Restaurant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.Arrays;

import static org.junit.Assert.*;

public class Sql2oRestaurantDaoTest {
    private Connection conn;
    private Sql2oRestaurantDao restaurantDao;
    private  Sql2oFoodtypeDao foodtypeDao;
    
    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        restaurantDao = new Sql2oRestaurantDao(sql2o);
        foodtypeDao = new Sql2oFoodtypeDao(sql2o);
        conn = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addingRestaurantSetsId() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        assertEquals(1, testRestaurant.getId());
    }

    @Test
    public void getAll() throws Exception {
        Restaurant restaurant1 = setupRestaurant();
        Restaurant restaurant2 = setupAltRestaurant();
        assertEquals(2, restaurantDao.getAll().size());
    }

    @Test
    public void findByIdReturnsRestaurant() {
        Restaurant restaurant1 = setupRestaurant();
        Restaurant restaurant2 = setupRestaurant();

        assertEquals(restaurant1.getName(), restaurantDao.findById(1).getName());
    }

    @Test
    public void updateCorrectlyUpdatesAllFields() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.update(testRestaurant.getId(), "a", "b", "c", "d", "e", "f");
        Restaurant foundRestaurant = restaurantDao.findById(testRestaurant.getId());
        assertEquals("a", foundRestaurant.getName());
        assertEquals("b", foundRestaurant.getAddress());
        assertEquals("c", foundRestaurant.getZipcode());
        assertEquals("d", foundRestaurant.getPhone());
        assertEquals("e", foundRestaurant.getWebsite());
        assertEquals("f", foundRestaurant.getEmail());
    }

    @Test
    public void deleteById() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        Restaurant otherRestaurant = setupAltRestaurant();
        assertEquals(2, restaurantDao.getAll().size());
        restaurantDao.deleteById(testRestaurant.getId());
        assertEquals(1, restaurantDao.getAll().size());
    }

    @Test
    public void clearAll() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        Restaurant otherRestaurant = setupRestaurant();
        restaurantDao.clearAll();
        assertEquals(0, restaurantDao.getAll().size());
    }

    @Test
    public void deleteingRestaurantAlsoUpdatesJoinTable() {
        Foodtype testFoodtype = new Foodtype("Seafood");
        foodtypeDao.add(testFoodtype);

        Foodtype altFoodtype = new Foodtype("Dessert");
        foodtypeDao.add(testFoodtype);

        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);

        Restaurant altRestaurant = setupAltRestaurant();
        restaurantDao.add(altRestaurant);

        foodtypeDao.addFoodtypeToRestaurant(testFoodtype, testRestaurant);
        foodtypeDao.addFoodtypeToRestaurant(altFoodtype, testRestaurant);

        restaurantDao.deleteById(testRestaurant.getId());
        assertEquals(0, restaurantDao.getAllFoodtypesForARestaurant(testRestaurant.getId()).size());
    }

    public Restaurant setupRestaurant (){
        Restaurant restaurant = new Restaurant("Fish Witch", "214 NE Broadway", "97232", "503-402-9874", "http://fishwitch.com", "hellofishy@fishwitch.com");
        restaurantDao.add(restaurant);
        return restaurant;
    }

    public Restaurant setupAltRestaurant (){
        Restaurant restaurant = new Restaurant("Fish Witch", "214 NE Broadway", "97232", "503-402-9874");
        restaurantDao.add(restaurant);
        return restaurant;
    }

}