package backend.Loader;

import backend.Board;
import backend.Card;
import backend.Tiles.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class  JsonLoader {



    public Board startUp(String jsonLocationTiles, String jsonLocationOpportunity, String jsonLocationLuck){
        Board board = new Board();
        Tile[] tiles = setUpTiles(jsonLocationTiles);
        Queue<Card> cardsOpportunity = setUpCard(jsonLocationOpportunity);
        Queue<Card> cardsLuck = setUpCard(jsonLocationLuck);
        board.setTiles(tiles);
        board.setOpportunityCard(cardsOpportunity);
        board.setPotLuckCard(cardsLuck);
        return board;
    }



    /**
     * @param jsonLocation
     */
    public Queue<Card> setUpCard(String jsonLocation){
        JSONParser jsonParser = new JSONParser();
        JSONObject cardData = null;
        Queue<Card> cards = new LinkedList<Card>();

        try {
            FileReader fileReader = new FileReader(jsonLocation);
            Object obj = jsonParser.parse(fileReader);
            cardData = (JSONObject) obj;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        int counter = 1;
        while (cardData.containsKey(String.valueOf(counter))){
            JSONObject current = (JSONObject) cardData.get(String.valueOf(counter));
            String description = (String) current.get("description");

            JSONArray effects = (JSONArray) current.get("effects");

            Map<Integer, Properties> map = itterateEffects(effects);

            Card card = new Card(description, map );
            cards.add(card);
            counter++;
        }

        return cards;
    }

    public Map<Integer, Properties> itterateEffects(JSONArray effects){
        Map<Integer, Properties> map = new HashMap<>();

        for (Object effect : effects) {
            Properties properties = new Properties();
            JSONObject current = (JSONObject) effect;
            int effectId = Math.toIntExact((Long) current.get("effectId"));

            properties.put("effectId",effectId);
            if (current.containsKey("amount")){
                properties.put("amount", Math.toIntExact((Long) current.get("amount")));
            }
            if (current.containsKey("houseCost")){
                properties.put("houseCost", Math.toIntExact((Long) current.get("houseCost")));
            }
            if (current.containsKey("hotelCost")){
                properties.put("hotelCost", Math.toIntExact((Long) current.get("hotelCost")));
            }
            if (current.containsKey("location")){
                if(current.get("location") instanceof String) properties.put("location", (String) current.get("location"));
                else if (current.get("location") instanceof Long) properties.put("location",Math.toIntExact((Long) current.get("location")));
            }
            if (current.containsKey("cardPick")){
                properties.put("cardPick", (String) current.get("cardPick"));
            }

            map.put(effectId,properties);

        }
        return map;
    }

    /**
     * @param jsonLocation the string path to the Json file
     * @return an Array of tile build from the data in the file
     */
    public Tile[] setUpTiles(String jsonLocation){
        JSONParser jsonParser = new JSONParser();
        JSONObject boardData = null;
        Tile[] tiles = new Tile[41];
        Map<String, ArrayList<TileProperty>> neighborhoods = new HashMap<>();
        try {
            FileReader fileReader = new FileReader(jsonLocation);
            Object obj = jsonParser.parse(fileReader);
            boardData = (JSONObject) obj;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 41; i++) {
            JSONObject current = (JSONObject) boardData.get(Integer.toString(i + 1));
            if (current != null) {
                String type = (String) current.get("Type");
                switch (type) {
                    case "PotLuck":
                        tiles[i] = new TileCard(TileCard.Type.Luck);
                        break;
                    case "Opportunity":
                        tiles[i] = new TileCard(TileCard.Type.Opportunity);
                        break;

                    case "Tax":
                        int tax = Math.toIntExact((Long) current.get("Tax"));
                        tiles[i] = new TileTax(tax);
                        break;

                    case "JailVisit":
                        tiles[i] = new TileJail();
                        break;

                    case "FreeParking":
                        tiles[i] = new TileFreeParking();
                        break;

                    case "Go":
                        tiles[i] = new TileGo();
                        break;

                    case "GoToJail":
                        tiles[i] = new TileGoToJail();
                        break;

                    case "building":
                        tiles[i] = buildBuildingTile(current, neighborhoods);
                        break;

                    case "Station":
                        tiles[i] = buildStationTile(current, neighborhoods);
                        break;

                    case "Utility":
                        tiles[i] = buildUtilityTile(current, neighborhoods);
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + type);
                }
            }
        }
        return tiles;
    }


    /**
     * @param object
     * @param neighborhoods
     * @return
     */
    public TileBuilding buildBuildingTile(JSONObject object, Map<String, ArrayList<TileProperty>> neighborhoods){
        String hexColour = (String) object.get("Colour");
        String name = (String) object.get("Name");
        int developmentCost = Math.toIntExact((Long) object.get("developmentCost"));
        int price = Math.toIntExact((Long) object.get("Price"));
        JSONArray rentJSONArray = (JSONArray) object.get("rent");
        int[] rentIntArray = new int[rentJSONArray.size()];
        for (int i = 0; i < rentJSONArray.size(); i++)
        {
            rentIntArray[i] = Math.toIntExact((Long) rentJSONArray.get(i));
        }
        Integer[] rentArray = Arrays.stream(rentIntArray).boxed().toArray( Integer[]::new );
        ArrayList<Integer> rent = new ArrayList<>(Arrays.asList(rentArray));
        ArrayList<TileProperty> neighborhood;
        if (neighborhoods.containsKey(hexColour)){
            neighborhood = neighborhoods.get(hexColour);
        }
        else {
            neighborhood = new ArrayList<>();
            neighborhoods.put(hexColour,neighborhood);
        }
        TileBuilding tileBuilding = new TileBuilding(hexColour,rent,developmentCost,0,price,name,null,neighborhood,false);
        neighborhood.add(tileBuilding);
        return tileBuilding;
    }

    /**
     * @param object
     * @param neighborhoods
     * @return
     */
    public TileStation buildStationTile(JSONObject object, Map<String, ArrayList<TileProperty>> neighborhoods){
        String name = (String) object.get("Name");
        int price = Math.toIntExact((Long) object.get("Price"));
        JSONArray rentJSONArray = (JSONArray) object.get("rent");
        int[] rentIntArray = new int[rentJSONArray.size()];
        for (int i = 0; i < rentJSONArray.size(); i++)
        {
            rentIntArray[i] = Math.toIntExact((Long) rentJSONArray.get(i));
        }
        Integer[] rentArray = Arrays.stream(rentIntArray).boxed().toArray( Integer[]::new );
        ArrayList<Integer> rent = new ArrayList<>(Arrays.asList(rentArray));
        ArrayList<TileProperty> neighborhood;
        String neighborhoodName = "Station";
        if (neighborhoods.containsKey(neighborhoodName)){
            neighborhood = neighborhoods.get(neighborhoodName);
        }
        else {
            neighborhood = new ArrayList<>();
            neighborhoods.put(neighborhoodName,neighborhood);
        }
        TileStation tileStation = new TileStation(rent,price,name,null,neighborhood,false);
        neighborhood.add(tileStation);
        return tileStation;
    }

    /**
     * @param object
     * @param neighborhoods
     * @return
     */
    public TileUtility buildUtilityTile(JSONObject object,  Map<String, ArrayList<TileProperty>> neighborhoods){
        String name = (String) object.get("Name");

        int price = Math.toIntExact((Long) object.get("Price"));

        ArrayList<TileProperty> neighborhood;
        String neighborhoodName = "Utility";
        if (neighborhoods.containsKey(neighborhoodName)){
            neighborhood = neighborhoods.get(neighborhoodName);
        }
        else {
            neighborhood = new ArrayList<>();
            neighborhoods.put(neighborhoodName,neighborhood);
        }

        TileUtility tileUtility = new TileUtility(price,name,null,neighborhood,false);
        neighborhood.add(tileUtility);
        return tileUtility;
    }


}
