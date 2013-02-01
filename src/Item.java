/**
 * Created with IntelliJ IDEA.
 * User: Emmy
 * Date: 11/25/12
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.List;

/**
 * An item is a food item that has been bought before.
 it contains
 - the avg number of days in between buying the item based on previous lists
 - A probability dictionary of buying that item on a particular day
 - A probability dictionary of buying that item with other items
 - the number of times the item has appeared

 To determine what food items to recommend on a given day, recommender adds each of those 4 properties for each item in a certain way (or some of them are weights), and then ranks
 the likeliest item.
 */
public class Item {

    private String name;
    private DateTime lastBought;
    private float avgPeriod;
    private float timesBought;
    private Integer[] dayCounter;
    private HashMap<String, Float> withItems;
    private Float chance;
    private DateTimeFormatter formatter = DateTimeFormat.forPattern("MM-dd-yyyy:EEE");
    // absolute distance from average period, reduces chance

    public Item(String name){
        this(name, DateTime.now());
    }

    public Item(String name, String firstBought){
        this.name = name;
        dayCounter = new Integer[7];
        for (int i=0; i<dayCounter.length; i++){
            dayCounter[i] = 0;
        }
        withItems = new HashMap<String, Float>();
        lastBought = formatter.parseDateTime(firstBought);
        avgPeriod = 0;
        timesBought = 0;
    }

    public Item(String name, DateTime firstBought){
        this.name = name;
        dayCounter = new Integer[7];
        withItems = new HashMap<String, Float>();
        lastBought = firstBought;
        avgPeriod = 0;
        timesBought = 0;
    }

    private void setTimes(DateTime dateBought){
        int daysSince = Days.daysBetween(lastBought,dateBought).getDays();
        lastBought = dateBought;
        if (timesBought == 1){
            this.avgPeriod = 0;
        } else {
//            if (this.name.equals("Green Onions")) System.out.println("("+daysSince+"+("+avgPeriod+"*("+(timesBought-2)+")))/"+(timesBought-1));
            this.avgPeriod = (daysSince+(avgPeriod*(timesBought-2)))/(timesBought-1);
//            if (this.name.equals("Green Onions")) System.out.println("average " + avgPeriod);
        }
    }

    /**
     * The buy command is used everytime this item is bought and takes in a list of all the other items it is bought with
     * @param otherItems
     */
    public void buy(List<String> otherItems,String dateBought){
        this.timesBought++;
//        if (this.name.equals("Green Onions")) System.out.println(timesBought);
        setTimes(formatter.parseDateTime(dateBought));
        for (String item : otherItems){
            Float originalAmount = this.withItems.get(item);
            if (originalAmount == null){
                this.withItems.put(item, new Float(1.0));
            } else {
                this.withItems.put(item, originalAmount+1);
            }
        }
//        if (this.name.equals("Green Onions")) System.out.println(otherItems);
//        if (this.name.equals("Green Onions")) System.out.println(withItems);
        dayCounter[lastBought.getDayOfWeek()-1]++;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!this.getClass().equals(obj.getClass())) return false;
        Item obj2 = (Item) obj;
        if (this.name.equals(obj2.name)) return true;
        else return false;
    }

    public String getName(){
        return this.name;
    }

    public String toString(){
        return this.name;
    }

    public Float getTimesBought(){
        return this.timesBought;
    }

    public Float getAvgPeriod(){
        return avgPeriod;
    }

    public float getTimesBoughtRank(float totalBought){
        return timesBought/totalBought;
    }

    public float timesBoughtWith(List<Item> otherFood,float totalBuys){
        float timesBoughtWithRank = 0;
        for (Item item : otherFood){
            Float timesBoughtWith = withItems.get(item.getName());
            if (timesBoughtWith == null) timesBoughtWith=new Float(0);
            timesBoughtWithRank = timesBoughtWithRank + (timesBoughtWith/item.getTimesBought())*(timesBoughtWith/timesBought);
//            timesBoughtWithRank = timesBoughtWithRank + (withItems.get(item.getName())/item.getTimesBought())*(withItems.get(item.getName())/timesBought);
//            timesBoughtWithRank = timesBoughtWithRank + (item.getTimesBought()/totalBuys)*(withItems.get(item.getName())/timesBought);
//            timesBoughtWithRank = timesBoughtWithRank + withItems.get(item.getName());  niave, add items
//            or another method ... divide by total number of items bought
        }
        return timesBoughtWithRank;
    }

    public Float getAvgPeriodRank(DateTime timeOfCurrentList){
        if (avgPeriod != 0){
            float daysSince = Days.daysBetween(lastBought,timeOfCurrentList).getDays();  // always positive
            float daysTill = avgPeriod - daysSince;                                      // negative means its overdue, positive means its not yet due
            if  (daysTill == 0) {
                return new Float(0.5);
            } else if (Math.abs(daysTill)==1){
                return new Float(0.4);
            } else if (Math.abs(daysTill)==2){
                return new Float(0.3);
            } else {
                return new Float(0);
            }
        } else {
            return new Float(0);
        }
    }

    public Float timesBoughtOnDay(Integer day){
        return new Float((dayCounter[day%7]*0.7+dayCounter[(day-1)%7]*0.5+dayCounter[(day+1)%7]*0.5+dayCounter[(day-2)%7]*0.2+dayCounter[(day+2)%7]*0.2+dayCounter[(day-3)%7]*0.1+dayCounter[(day+3)%7]*0.1)/timesBought);
    }

    public Float getChance(List<Item> otherFood, String buyDate, float totalBuys){
        chance = timesBoughtWith(otherFood,totalBuys) + timesBoughtOnDay(formatter.parseDateTime(buyDate).getDayOfWeek()-1) + getAvgPeriodRank(formatter.parseDateTime(buyDate)) + (timesBought*timesBought)/totalBuys;
//        System.out.println("times bought with; " + timesBoughtWith(otherFood,totalBuys));
//        System.out.println("times bought on day: "+ timesBoughtOnDay(formatter.parseDateTime(buyDate).getDayOfWeek()-1));
//        System.out.println("time from last bought: "+ getAvgPeriodRank(formatter.parseDateTime(buyDate)));
//        System.out.println("times bought rank: "+ getTimesBoughtRank(totalBuys));
//        System.out.println("total rank: "+chance);
        return chance;
    }

    public void getInfo(){
        System.out.println( "item: "+name+"\navg period: "+avgPeriod+"\ntimes bought: "+timesBought+"\ntimes bought on days:\n");
        for (int i = 0; i < 7; i++){
            System.out.println(i + " : " + dayCounter[i]);
        }
        System.out.println(withItems);
    }
}
