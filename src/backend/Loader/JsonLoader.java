package backend.Loader;

import backend.Board;
import backend.Tiles.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JsonLoader {



    public Board startUp(String jsonLocation){
        Board board = new Board();
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
            assert boardData != null;
            JSONObject current = (JSONObject) boardData.get(Integer.toString(i+1));
            String type = (String) current.get("Type");

            switch (type){
                case "PotLuck":

                    break;

                case "Tax":
                    int tax = (int) current.get("Tax");
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

        board.setTiles(tiles);
        return board;
    }


    private TileBuilding buildBuildingTile(JSONObject object, Map<String, ArrayList<TileProperty>> neighborhoods){
        String hexColour = (String) object.get("Colour");
        String name = (String) object.get("Name");
        int developmentCost = (int) object.get("developmentCost");
        int price = (int) object.get("Price");

        int[] rentIntArray = (int[]) object.get("Rent");
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

    private TileStation buildStationTile(JSONObject object, Map<String, ArrayList<TileProperty>> neighborhoods){
        String name = (String) object.get("Name");
        int price = (int) object.get("Price");

        int[] rentIntArray = (int[]) object.get("Rent");
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

    private TileUtility buildUtilityTile(JSONObject object,  Map<String, ArrayList<TileProperty>> neighborhoods){
        String name = (String) object.get("Name");

        int price = (int) object.get("Price");

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


    private void setUpCard(Board board, String jsonLocation){

        JSONParser jsonParser = new JSONParser();
        JSONObject cardData = null;

        try {
            FileReader fileReader = new FileReader(jsonLocation);
            Object obj = jsonParser.parse(fileReader);
            cardData = (JSONObject) obj;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }


    }

}
