package MapParameters;

import java.io.FileReader;
import java.io.IOException;

import PopUpWindow.ExceptionWindow;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ReadJSONMapParameters {

    private final JSONParser jsonParser = new JSONParser();

    public int[] readParameters() throws Exception
    {
        try (FileReader reader = new FileReader("parameters.json"))
        {
            Object obj = jsonParser.parse(reader);
            JSONArray StageParameters = (JSONArray) obj;
            return parseSettings((JSONObject) StageParameters.get(0));

        } catch (IOException e) {
            ExceptionWindow exceptionWindow = new ExceptionWindow();
            exceptionWindow.start("Problem reading parameters.json");
            e.printStackTrace();
        }catch (ParseException e){
            ExceptionWindow exceptionWindow = new ExceptionWindow();
            exceptionWindow.start("Parse parameters.json problem");
            e.printStackTrace();
        }
        return new int[0];
    }

    private int[] parseSettings(JSONObject menu)
    {
        JSONObject MenuObject = (JSONObject) menu.get("Parameters");
        int mapWidth = Integer.parseInt((String) MenuObject.get("MapWidth"));
        int mapHeight = Integer.parseInt((String) MenuObject.get("MapHeight"));
        int jungleRatio = Integer.parseInt((String) MenuObject.get("JungleRatio"));
        int animalAmount = Integer.parseInt((String) MenuObject.get("AnimalsCount"));

        int startEnergy = Integer.parseInt((String) MenuObject.get("StartEnergy"));
        int moveEnergy = Integer.parseInt((String) MenuObject.get("MoveEnergy"));
        int plantEnergy = Integer.parseInt((String) MenuObject.get("PlantEnergy"));
        int amountOfMaps = Integer.parseInt((String) MenuObject.get("AmountOfMaps"));

        return new int[] {mapWidth,mapHeight,jungleRatio,animalAmount,startEnergy,moveEnergy,plantEnergy,amountOfMaps};
    }
}