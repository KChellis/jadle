package dao;

import models.Foodtype;
import models.Restaurant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.Assert.*;

public class Sql2oFoodtypeDaoTest {
    private Connection conn;
    private Sql2oFoodtypeDao foodtypeDao;
    private Sql2oRestaurantDao restaurantDao;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        foodtypeDao = new Sql2oFoodtypeDao(sql2o);
        restaurantDao = new Sql2oRestaurantDao(sql2o);
        conn = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addingFoodtypeSetsId() throws Exception {
        Foodtype testFoodtype = setupFoodtype();
        assertEquals(1, testFoodtype.getId());
    }

    @Test
    public void getAll() throws Exception {
        Foodtype foodtype1 = setupFoodtype();
        Foodtype foodtype2 = setupFoodtype();
        assertEquals(2, foodtypeDao.getAll().size());
    }

    @Test
    public void deleteById() throws Exception {
        Foodtype testFoodtype = setupFoodtype();
        Foodtype otherFoodtype = setupFoodtype();
        assertEquals(2, foodtypeDao.getAll().size());
        foodtypeDao.deleteById(testFoodtype.getId());
        assertEquals(1, foodtypeDao.getAll().size());
    }

    @Test
    public void clearAll() throws Exception {
        Foodtype testFoodtype = setupFoodtype();
        Foodtype otherFoodtype = setupFoodtype();
        foodtypeDao.clearAll();
        assertEquals(0, foodtypeDao.getAll().size());
    }

    @Test
    public void addFoodTypeToRestaurantAddsTypeCorrectly() {
        Restaurant testRestaurant = setupRestaurant();
        Restaurant altRestaurant = setupAltRestaurant();

        restaurantDao.add(testRestaurant);
        restaurantDao.add(altRestaurant);

        Foodtype testFoodtype = setupFoodtype();

        foodtypeDao.add(testFoodtype);

        foodtypeDao.addFoodtypeToRestaurant(testFoodtype, testRestaurant);
        foodtypeDao.addFoodtypeToRestaurant(testFoodtype, altRestaurant);

        assertEquals(2, foodtypeDao.getAllRestaurantsForAFoodtype(testFoodtype.getId()).size());
    }

    @Test
    public void findByIdReturnsFoodtype() {
        Foodtype foodtype1 = new Foodtype("seafood");
        Foodtype foodtype2 = new Foodtype("bar food");

        foodtypeDao.add(foodtype1);
        foodtypeDao.add(foodtype2);

        assertEquals(foodtype1.getName(), foodtypeDao.findById(1).getName());
    }

    @Test
    public void deleteingFoodtypeAlsoUpdatesJoinTable() {
        Foodtype testFoodtype = new Foodtype("Seafood");
        foodtypeDao.add(testFoodtype);

        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);

        Restaurant altRestaurant = setupAltRestaurant();
        restaurantDao.add(altRestaurant);

        foodtypeDao.addFoodtypeToRestaurant(testFoodtype, testRestaurant);
        foodtypeDao.addFoodtypeToRestaurant(testFoodtype, altRestaurant);
        foodtypeDao.deleteById(testFoodtype.getId());
        assertEquals(0, foodtypeDao.getAllRestaurantsForAFoodtype(testFoodtype.getId()).size());
    }

    public Foodtype setupFoodtype(){
        Foodtype foodtype = new Foodtype("dessert");
        foodtypeDao.add(foodtype);
        return foodtype;
    }

    public Restaurant setupRestaurant (){
        return new Restaurant("Fish Witch", "214 NE Broadway", "97232", "503-402-9874", "http://fishwitch.com", "hellofishy@fishwitch.com");

    }

    public Restaurant setupAltRestaurant (){
        return new Restaurant("Fish Witch", "214 NE Broadway", "97232", "503-402-9874");

    }

}