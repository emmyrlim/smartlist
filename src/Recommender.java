import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: Emmy
 * Date: 11/26/12
 * Time: 1:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class Recommender {

    // data is delimited by commas, with first element containing date
    static String[] TEST_DATA_SPECIFIC = new String[12];
    static List<Item> itemsInDatabase = new ArrayList<Item>();
    static float totalBuys = 0;

    public static void main(String[] args){
        populateTestData();        // data
        rankTestData();            // learn from data
        demo();
    }

    private static void rankTestData() {
        for (String shoppingListRaw : TEST_DATA_SPECIFIC){
            List<String> splitList = Arrays.asList(shoppingListRaw.split(","));
            ArrayList<String> itemList = new ArrayList<String>();
            itemList.addAll(splitList);
            String currentDate = itemList.get(0);
            itemList.remove(0);
            for (int i=0; i < itemList.size(); i++){
                String food = itemList.get(0);
                itemList.remove(0);
                Item foodItem = fetchFoodItem(food, currentDate);
                foodItem.buy(itemList,currentDate);
                itemList.add(food);
                totalBuys++;
            }
        }
        System.out.println("Ranking Complete");
    }

    private static Item fetchFoodItem(String food, String currentDate) {
        Item foodItem = new Item(food,currentDate);
        int index = itemsInDatabase.indexOf(foodItem);
        if (index >= 0){
            foodItem = itemsInDatabase.get(index);
        } else {
            itemsInDatabase.add(foodItem);
        }
        return foodItem;
    }

    private static List<Item> rankChance(List<Item> currentlyBuying, List<Item> tempDatabase){
        List<Item> ranked = new ArrayList<Item>();
        TreeMap<Float,String> rankings = new TreeMap<Float,String>();
        for (Item databaseItem : tempDatabase){
            rankings.put(databaseItem.getChance(currentlyBuying, "12-01-2012:Sat", totalBuys), databaseItem.getName());
        }
        System.out.println(rankings);
        return ranked;
    }

    private static void demo(){
        /**
         *  For shopping list 12/1: [
         *      Onions,
         *      Green Onions,
         *      Celery,
         *      Carrots,
         *      GroundBeef,
         *      Tofu,
         *      White Mushrooms]
          */
        List<Item> tempDatabase = new ArrayList<Item>();
        tempDatabase.addAll(itemsInDatabase);
        List<Item> testList = new ArrayList<Item>();
        // Initial
        rankChance(testList, tempDatabase);
        //Select Onion
        Item onions = fetchFoodItem("Onions","12-01-2012:Sat");
        testList.add(onions);
        tempDatabase.remove(onions);
        rankChance(testList,tempDatabase);
        //Select Green Onion
        Item greenonions = fetchFoodItem("Green Onions","12-01-2012:Sat");
        testList.add(greenonions);
        tempDatabase.remove(greenonions);
        rankChance(testList,tempDatabase);
        //Select Celery
        Item celery = fetchFoodItem("Celery","12-01-2012:Sat");
        testList.add(celery);
        tempDatabase.remove(celery);
        rankChance(testList,tempDatabase);
        //Select Carrots
        Item carrots = fetchFoodItem("Carrots","12-01-2012:Sat");
        testList.add(carrots);
        tempDatabase.remove(carrots);
        rankChance(testList,tempDatabase);
        //Select Ground Beef
        Item groundbeef = fetchFoodItem("Ground Beef","12-01-2012:Sat");
        testList.add(groundbeef);
        tempDatabase.remove(groundbeef);
        rankChance(testList,tempDatabase);
        //Select Tofu
        Item tofu = fetchFoodItem("Tofu","12-01-2012:Sat");
        testList.add(tofu);
        tempDatabase.remove(tofu);
        rankChance(testList,tempDatabase);
        //Select white mushrooms
        Item whitemushrooms = fetchFoodItem("White Mushrooms","12-01-2012:Sat");
        testList.add(whitemushrooms);
        tempDatabase.remove(whitemushrooms);
        rankChance(testList,tempDatabase);
    }


    private static void populateTestData(){
        // start comparing to next at test data[3]. in other words, learnt from
        // 4
        TEST_DATA_SPECIFIC[0] = "09-08-2012:Sat,Canned Soup,Chicken Stock,Vegetable Stock,Beef Stock,Pasta,Onions,White Mushrooms,Milk,Yoghurt,";
        TEST_DATA_SPECIFIC[1] = "09-14-2012:Sun,Instant Ramen,Vegetable Oil,Canned Soup,Onions,Acorn Squash,Pasta,Green Onions,Celery,Carrots,Basil,Oyster Sauce,Tofu,Chilli Bean Paste,Black Bean Paste,Rice Wine,Soy Sauce,Noodles,Rice,Daikon,Ginger,Galangal,Pumpkin,Shitaki Mushrooms,";
        TEST_DATA_SPECIFIC[2] = "09-23-2012:Sun,Pasta Sauce,Eggs,Curry,Pork Belly,Green Onions";
        TEST_DATA_SPECIFIC[3] = "09-28-2012:Fri,Ground Beef,Plum Tomatoes,Carrots,Milk,Carrots,Cheese,Tofu,Yoghurt,Beef Consomme,Mushroom Soup,Bacon";
        TEST_DATA_SPECIFIC[4] = "09-29-2012:Sat,Onions,Carrots,Mushrooms,Stew Pork,Instant Ramen,Tomatoes,Chicken Breasts,Ketchup,Apple Sauce,Canned Soup,Canned Tomato,Cheese,Steak,Ground Turkey,Celery,Tofu,Grapefruit,Spinach,Ice Cream,Cream cheese,White Mushrooms,Green Onions";
        TEST_DATA_SPECIFIC[5] = "10-04-2012:Thu,Pasta Sauce,Eggs,Pasta,Shrimp,Garlic";
        TEST_DATA_SPECIFIC[6] = "10-13-2012:Sat,Pork Neck,Chicken Drumsticks,Onions,Beef Bones,Stew Beef,Pork Feet,Jelly,Garlic,Rice,Green Onions,White Mushrooms,Celery,";
        TEST_DATA_SPECIFIC[7] = "10-22-2012:Mon,Instant Mac and Cheese,Canned Soup,Instant Ramen,Mushroom Soup,Parmesan,Canned Tomatoes,Onions,Butter,Marsala,Bacon";
        TEST_DATA_SPECIFIC[8] = "11-04-2012:Sun,Pork Hock,Stew Beef,Beef Brisket,Tsuyu,Wasabi,Tofu,Udon,Black Vinegar,Oyster Sauce,Jelly,Black Sesame oil,Noodles,Onions,Carrot,Green Onions,Enoki Mushrooms,XO Sauce,Seaweed,Pasta,Creme Fraiche,Yoghurt,Artichoke,Mascarpone,Butternut Squash,Cider";
        TEST_DATA_SPECIFIC[9] = "11-11-2012:Sun,Ground Beef,Green Onions,Ground Pork,Onions,Carrots,Steak,Tomatoes,Chicken Breast,Oyster Sauce,Fish Sauce,Rice,Oyster Mushrooms";
        TEST_DATA_SPECIFIC[10] = "11-17-2012:Sat,Oxtail,Whole Chicken,Ginger,Pork Hock,Pork Feet,Noodles,Tofu,Soy Milk,Milk,Black Vinegar,Sesame,Chicken Essence,Ginseng,Enoki Mushrooms,Mirin,Honey";
        TEST_DATA_SPECIFIC[11] = "11-24-2012:Sat,Pork Chop,Ground Beef,Green Onions,Carrots,Onions,Basil,Portabello Mushrooms,Instant Mac and Cheese,Mushroom Soup,Chicken Stock,Plum Tomatoes,Pasta,Celery,Tomato,Sweet Potato,Bacon,Pancetta";

    }
}


