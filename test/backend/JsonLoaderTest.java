package backend;

import backend.Loader.JsonLoader;
import backend.Tiles.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class JsonLoaderTest {

    JsonLoader jsonLoader = new JsonLoader();
    Map<String, ArrayList<TileProperty>> neighborhood;

    @Before
    public void setUp(){
        neighborhood = new HashMap<>();


    }

    @Test
    public void testStartUpTiles() {
        Board board = jsonLoader.startUp("test/TilesTest1.Json", "test/LuckCard.Json",
                "test/OpportunityCard.Json");

        Tile[] tiles = jsonLoader.setUpTiles("test/TilesTest1.Json");
        Tile[] boardTiles = board.getTiles();
        boolean result = true;
        for (int i = 0; i < boardTiles.length; i++) {
            System.out.println(tiles[i]);
            if (tiles[i] != null){
                if (tiles[i].getName() != boardTiles[i].getName() ){
                    result = false;
                }
            }

        }
        assertTrue(result);
    }

    @Test
    public void testSetUpCard() {
        Queue<Card> luck = jsonLoader.setUpCard("test/LuckCard.Json");

        boolean sucess = true;
        for (Card each: luck) {
            String description = each.getDescription();
            Map<Integer, Properties> effects = each.effects;
            if (description == null){
                sucess = false;
            }
            if (effects.isEmpty()){
                sucess = false;
            }
        }
        assertTrue(sucess);
    }

    @Test
    public void testIterateEffectsAmount() {
        JSONArray effects = new JSONArray();
        JSONObject effect = new JSONObject();
        effect.put("effectId", 0L);
        effect.put("amount" , 100L);
        effects.add(effect);

        Map<Integer, Properties> integerPropertiesMap = jsonLoader.iterateEffects(effects);

        Map<Integer, Properties> expected = new HashMap<>();
        Properties properties = new Properties();
        properties.put("amount", 100);
        properties.put("effectId", 0);
        expected.put(0, properties);
        assertEquals(expected,integerPropertiesMap);

    }
    @Test
    public void testIterateEffectsHouseCost() {
        JSONArray effects = new JSONArray();
        JSONObject effect = new JSONObject();
        effect.put("effectId", 0L);
        effect.put("houseCost" , 100L);
        effects.add(effect);

        Map<Integer, Properties> integerPropertiesMap = jsonLoader.iterateEffects(effects);

        Map<Integer, Properties> expected = new HashMap<>();
        Properties properties = new Properties();
        properties.put("houseCost", 100);
        properties.put("effectId", 0);
        expected.put(0, properties);
        assertEquals(expected,integerPropertiesMap);

    }
    @Test
    public void testIterateEffectsHotelCost() {
        JSONArray effects = new JSONArray();
        JSONObject effect = new JSONObject();
        effect.put("effectId", 0L);
        effect.put("hotelCost" , 100L);
        effects.add(effect);

        Map<Integer, Properties> integerPropertiesMap = jsonLoader.iterateEffects(effects);

        Map<Integer, Properties> expected = new HashMap<>();
        Properties properties = new Properties();
        properties.put("hotelCost", 100);
        properties.put("effectId", 0);
        expected.put(0, properties);
        assertEquals(expected,integerPropertiesMap);

    }
    @Test
    public void testIterateEffectsLocation() {
        JSONArray effects = new JSONArray();
        JSONObject effect = new JSONObject();
        effect.put("effectId", 0L);
        effect.put("location" , "old Steine");
        effects.add(effect);

        Map<Integer, Properties> integerPropertiesMap = jsonLoader.iterateEffects(effects);

        Map<Integer, Properties> expected = new HashMap<>();
        Properties properties = new Properties();
        properties.put("location", "old Steine");
        properties.put("effectId", 0);
        expected.put(0, properties);
        assertEquals(expected,integerPropertiesMap);

    }
    @Test
    public void testIterateEffectsCardPick() {
        JSONArray effects = new JSONArray();
        JSONObject effect = new JSONObject();
        effect.put("effectId", 0L);
        effect.put("location" , "Luck");
        effects.add(effect);

        Map<Integer, Properties> integerPropertiesMap = jsonLoader.iterateEffects(effects);

        Map<Integer, Properties> expected = new HashMap<>();
        Properties properties = new Properties();
        properties.put("location", "Luck");
        properties.put("effectId", 0);
        expected.put(0, properties);
        assertEquals(expected,integerPropertiesMap);

    }

    @Test
    public void testIterateEffectsMultipleEffect() {
        JSONArray effects = new JSONArray();
        JSONObject effect1 = new JSONObject();
        effect1.put("effectId", 0L);
        effect1.put("amount" , 100L);
        effects.add(effect1);

        JSONObject effect2 = new JSONObject();
        effect2.put("effectId", 1L);
        effect2.put("location" , "Luck");
        effects.add(effect2);


        Map<Integer, Properties> integerPropertiesMap = jsonLoader.iterateEffects(effects);

        Map<Integer, Properties> expected = new HashMap<>();
        Properties properties1 = new Properties();
        properties1.put("amount", 100);
        properties1.put("effectId", 0);
        expected.put(0, properties1);

        Properties properties2 = new Properties();
        properties2.put("location", "Luck");
        properties2.put("effectId", 1);
        expected.put(1, properties2);


        assertEquals(expected,integerPropertiesMap);

    }


    @Test
    public void testSetUpTilesMultipleTiles() {
         Tile[] tiles = jsonLoader.setUpTiles("test/TilesTest1.Json");

        boolean sucess = true;
        if (!(tiles[0] instanceof TileGo)){
            sucess = false;
        }
        if (!(tiles[1] instanceof TileGo)){
            sucess = false;
        }
        for (int i = 2; i < tiles.length ; i++) {
            if (tiles[i] != null){
                sucess = false;
            }
        }


        assertTrue(sucess);
    }

    @Test
    public void testSetUpTilesBuildingTilesName() {
        Tile[] tiles = jsonLoader.setUpTiles("test/TileTest2.Json");

        if (!(tiles[0] instanceof TileBuilding)){
            fail();
        }

        assertEquals("Gangsters Paradise", tiles[0].getName());
    }
    @Test
    public void testSetUpTilesBuildingTilesPrice() {
        Tile[] tiles = jsonLoader.setUpTiles("test/TileTest2.Json");

        if (!(tiles[0] instanceof TileBuilding)){
            fail();
        }

        assertEquals(60, ((TileBuilding) tiles[0]).getPrice());
    }
    @Test
    public void testSetUpTilesBuildingTilesColour() {
        Tile[] tiles = jsonLoader.setUpTiles("test/TileTest2.Json");

        if (!(tiles[0] instanceof TileBuilding)){
            fail();
        }

        assertEquals("#8b4513", ((TileBuilding) tiles[0]).getHexColour());
    }

    @Test
    public void testSetUpTilesBuildingTilesDevelopmentCost() {
        Tile[] tiles = jsonLoader.setUpTiles("test/TileTest2.Json");

        if (!(tiles[0] instanceof TileBuilding)){
            fail();
        }

        assertEquals(50, ((TileBuilding) tiles[0]).getDevelopmentCost());
    }

    @Test
    public void testSetUpTilesBuildingTilesRent() {
        Tile[] tiles = jsonLoader.setUpTiles("test/TileTest2.Json");

        if (!(tiles[0] instanceof TileBuilding)){
            fail();
        }
        Integer[] rentArray = {4,20,60,180,320,450};
        List<Integer> rent = Arrays.asList(rentArray);
        assertEquals(rent , ((TileBuilding) tiles[0]).getRent());
    }

    @Test
    public void testSetUpTilesTaxTilesName() {
        Tile[] tiles = jsonLoader.setUpTiles("test/TileTest3.Json");

        if (!(tiles[0] instanceof TileTax)){
            fail();
        }

        assertEquals("Income Tax", tiles[0].getName());
    }


    @Test
    public void testSetUpTilesTaxTilesTax() {
        Tile[] tiles = jsonLoader.setUpTiles("test/TileTest3.Json");

        if (!(tiles[0] instanceof TileTax)){
            fail();
        }

        assertEquals(200, ((TileTax) tiles[0]).getTax());
    }


    @Test
    public void testSetUpTilesStationTilesName() {
        Tile[] tiles = jsonLoader.setUpTiles("test/TileTestStation.Json");

        if (!(tiles[0] instanceof TileStation)){
            fail();
        }

        assertEquals("Brigthon Station", tiles[0].getName());
    }
    @Test
    public void testSetUpTilesStationTilesPrice() {
        Tile[] tiles = jsonLoader.setUpTiles("test/TileTestStation.Json");

        if (!(tiles[0] instanceof TileStation)){
            fail();
        }

        assertEquals(200, ((TileStation) tiles[0]).getPrice());
    }

    @Test
    public void testSetUpTilesStationTilesRent() {
        Tile[] tiles = jsonLoader.setUpTiles("test/TileTestStation.Json");

        if (!(tiles[0] instanceof TileStation)){
            fail();
        }
        Integer[] rentArray = {25,50,100,200};
        List<Integer> rent = Arrays.asList(rentArray);
        assertEquals(rent , ((TileStation) tiles[0]).getRent());
    }

    @Test
    public void testSetUpTileUtilityTilesName() {
        Tile[] tiles = jsonLoader.setUpTiles("test/TileTestUtility.Json");

        if (!(tiles[0] instanceof TileUtility)){
            fail();
        }

        assertEquals("tesla Power Co", tiles[0].getName());
    }
    @Test
    public void testSetUpTileUtilityTilesPrice() {
        Tile[] tiles = jsonLoader.setUpTiles("test/TileTestUtility.Json");

        if (!(tiles[0] instanceof TileUtility)){
            fail();
        }

        assertEquals(150, ((TileUtility) tiles[0]).getPrice());
    }


    @Test
    public void testBuildBuildingTileName() {

        JSONObject obj = new JSONObject();
        String name = "example";
        String type = "Station";
        String colour = "#ffffff";
        Long price = 150L;
        Long developmentCost = 50L;
        JSONArray rent = new JSONArray();
        rent.add(0L);
        rent.add(5L);
        rent.add(10L);
        rent.add(15L);
        obj.put("Type",type);
        obj.put("Colour",colour);
        obj.put("Name",name);
        obj.put("Price", price);
        obj.put("rent", rent);
        obj.put("developmentCost", developmentCost);

        TileBuilding tileBuilding = jsonLoader.buildBuildingTile(obj, neighborhood);

        assertEquals(name,tileBuilding.getName());

    }
    @Test
    public void testBuildBuildingTilePrice() {

        JSONObject obj = new JSONObject();
        String name = "example";
        String type = "Station";
        String colour = "#ffffff";
        Long price = 150L;
        Long developmentCost = 50L;
        JSONArray rent = new JSONArray();
        rent.add(0L);
        rent.add(5L);
        rent.add(10L);
        rent.add(15L);
        obj.put("Type",type);
        obj.put("Colour",colour);
        obj.put("Name",name);
        obj.put("Price", price);
        obj.put("rent", rent);
        obj.put("developmentCost", developmentCost);

        TileBuilding tileBuilding = jsonLoader.buildBuildingTile(obj, neighborhood);
        int actualPrice = tileBuilding.getPrice();
        int expectedPrice = 150;
        assertEquals(expectedPrice,actualPrice);

    }

    @Test
    public void testBuildBuildingTileNeighboorhood() {

        JSONObject obj = new JSONObject();
        String name = "example";
        String type = "Station";
        String colour = "#ffffff";
        Long price = 150L;
        Long developmentCost = 50L;
        JSONArray rent = new JSONArray();
        rent.add(0L);
        rent.add(5L);
        rent.add(10L);
        rent.add(15L);
        obj.put("Type",type);
        obj.put("Colour",colour);
        obj.put("Name",name);
        obj.put("Price", price);
        obj.put("rent", rent);
        obj.put("developmentCost", developmentCost);

        TileBuilding tileBuilding = jsonLoader.buildBuildingTile(obj, neighborhood);
        ArrayList<TileProperty> neighboor = new ArrayList<>();
        neighboor.add(tileBuilding);

        assertEquals(neighboor,tileBuilding.getNeighborhood());

    }

    @Test
    public void testBuildBuildingTileRent() {

        JSONObject obj = new JSONObject();
        String name = "example";
        String type = "Station";
        String colour = "#ffffff";
        Long price = 150L;
        Long developmentCost = 50L;
        JSONArray rent = new JSONArray();
        rent.add(0L);
        rent.add(5L);
        rent.add(10L);
        rent.add(15L);
        obj.put("Type",type);
        obj.put("Colour",colour);
        obj.put("Name",name);
        obj.put("Price", price);
        obj.put("rent", rent);
        obj.put("developmentCost", developmentCost);

        TileBuilding tileBuilding = jsonLoader.buildBuildingTile(obj, neighborhood);
        ArrayList<Integer> rentArrayList = new ArrayList<Integer>();
        rentArrayList.add(0);
        rentArrayList.add(5);
        rentArrayList.add(10);
        rentArrayList.add(15);
        assertEquals(rentArrayList,tileBuilding.getRent());

    }

    @Test
    public void testBuildStationTileName() {
        JSONObject obj = new JSONObject();
        String name = "example";
        String type = "Station";
        Long price = 150L;
        JSONArray rent = new JSONArray();
        rent.add(0L);
        rent.add(5L);
        rent.add(10L);
        rent.add(15L);
        obj.put("Type",type);
        obj.put("Name",name);
        obj.put("Price", price);
        obj.put("rent", rent);

        TileStation tileStation = jsonLoader.buildStationTile(obj, neighborhood);

        assertEquals(name,tileStation.getName());
    }
    @Test
    public void testBuildStationTilePrice() {
        JSONObject obj = new JSONObject();
        String name = "example";
        String type = "Station";
        Long price = 150L;
        JSONArray rent = new JSONArray();
        rent.add(0L);
        rent.add(5L);
        rent.add(10L);
        rent.add(15L);
        obj.put("Type",type);
        obj.put("Name",name);
        obj.put("Price", price);
        obj.put("rent", rent);

        TileStation tileStation = jsonLoader.buildStationTile(obj, neighborhood);
        int actualPrice = tileStation.getPrice();
        int expectedPrice = 150;
        assertEquals(expectedPrice,actualPrice);
    }

    @Test
    public void testBuildStationTileNeighboorhood() {
        JSONObject obj = new JSONObject();
        String name = "example";
        String type = "Station";
        Long price = 150L;
        JSONArray rent = new JSONArray();
        rent.add(0L);
        rent.add(5L);
        rent.add(10L);
        rent.add(15L);
        obj.put("Type",type);
        obj.put("Name",name);
        obj.put("Price", price);
        obj.put("rent", rent);

        TileStation tileStation = jsonLoader.buildStationTile(obj, neighborhood);

        ArrayList<TileProperty> neighboor = new ArrayList<>();
        neighboor.add(tileStation);

        assertEquals(neighboor,tileStation.getNeighborhood());
    }
    @Test
    public void testBuildUtilityTileName() {
        JSONObject obj = new JSONObject();
        String name = "example";
        String type = "Utility";
        Long price = 100L;
        obj.put("Type",type);
        obj.put("Name",name);
        obj.put("Price", price);

        TileUtility tileUtility = jsonLoader.buildUtilityTile(obj, neighborhood);

        assertEquals(name,tileUtility.getName());

    }
    @Test
    public void testBuildUtilityTilePrice() {
        JSONObject obj = new JSONObject();
        String name = "example";
        String type = "Utility";
        Long price = 100L;
        obj.put("Type",type);
        obj.put("Name",name);
        obj.put("Price", price);

        TileUtility tileUtility = jsonLoader.buildUtilityTile(obj, neighborhood);
        int actualPrice = tileUtility.getPrice();
        int expectedPrice = 100;
        assertEquals(expectedPrice,actualPrice);

    }

    @Test
    public void testBuildUtilityTileNeighborhood() {
        JSONObject obj = new JSONObject();
        String name = "example";
        String type = "Utility";
        Long price = 100L;
        obj.put("Type",type);
        obj.put("Name",name);
        obj.put("Price", price);

        TileUtility tileUtility = jsonLoader.buildUtilityTile(obj, neighborhood);

        ArrayList<TileProperty> neighboor = new ArrayList<>();
        neighboor.add(tileUtility);

        assertEquals(neighboor,tileUtility.getNeighborhood());

    }
}