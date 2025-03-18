// Space Trader!
// This version by Roland Waddilove (https://github.com/rwaddilove/)
// Public domain. Use it, copy it, change it, laugh at the bad code!

import java.util.Random;
import java.util.Scanner;

class Ship {                            // Default values are for the easy level
    int shipType = 0;
    int money = 900;
    int cargoHold = 0;                 // amount of cargo held
    int maxCargoHold = 20;            // maximum
    int shields = 20;
    int maxShields = 20;
    int fuel = 10;                      // how far you can travel
    int maxFuel = 20;                  // max distance
    int speed = 20;
    int location = 0;                   // randomised later
    int laser = 20;
    int[] cargo = {0,0,0,0,0,0};        // quantity of each item in goods_names[]

    public void ShowCargo(String[] goodsNames) {
        System.out.println("\n ---------- Your Cargo -----------");
        System.out.println("   Item       Qty");
        for (int i = 0; i < goodsNames.length; ++i)
            if (cargo[i] > 0) System.out.printf("%d. %-10s %d\n", i, goodsNames[i], cargo[i]);
        System.out.println("\nCargo usage: "+ cargoHold  +" of "+maxCargoHold+" storage units.");
    }
}

class Input {
    public static String inputStr(String prompt) {
        System.out.print(prompt);
        Scanner input = new Scanner(System.in);
        return input.nextLine().strip().toUpperCase();
    }       //end
    public static int inputInt(String prompt) {
        System.out.print(prompt);
        Scanner input = new Scanner(System.in);
        String inp = input.nextLine().strip().toUpperCase();
        try {
            return Integer.parseInt(inp); }
        catch (NumberFormatException e) {
            return 9999; }      // a value not used
    }       //end
    public static char inputChr(String prompt) {
        System.out.print(prompt);
        Scanner input = new Scanner(System.in);
        String inp = input.nextLine().strip().toUpperCase();
        return inp.isEmpty() ? '*' : inp.charAt(0);
    }       //end
    public static boolean isNumber(String s) {
        for (char c : s.toCharArray())
            if (!Character.isDigit(c)) return false;
        return true;
    }
}

class Initialise {
    public static void PlanetLocations(int[] planetLoc) {
        Random random = new Random();
        for (int i = 0; i < planetLoc.length; ++i)
            planetLoc[i] = random.nextInt(2,40);                        // distance to planet
    }

    public static void GoodsQuantities(int[][] planetGood) {
        Random random = new Random();
        for (int planet = 0; planet < planetGood.length; ++planet) {    // for each planet
            for (int good = 0; good < planetGood[0].length; ++good)     // for each good
                planetGood[planet][good] = random.nextInt(20);   // random quantity
        }
    }

    public static void GoodsPrices(int[][] planetPrice) {
        Random random = new Random();
        for (int planet = 0; planet < planetPrice.length; ++planet) {    // for each planet
            for (int good = 0; good < planetPrice[0].length; ++good)     // for each good
                planetPrice[planet][good] = random.nextInt(5,20);   // random price
        }
    }

    public static void Difficulty(Ship myShip, Ship pirates) {
        pirates.maxShields = 10;
        pirates.laser = 10;
        char d = '*';
        while (d != 'E' && d != 'M' && d != 'H')    // Easy level uses Ship class defaults
            d = Input.inputChr("Choose: (E)asy, (M)edium, (H)ard: ");
        if (d == 'M') {
            myShip.money = 100;
            myShip.maxCargoHold = 15;
            myShip.fuel = 15;
            myShip.maxFuel = 15;
            myShip.shields = 15;
            myShip.laser = 15;
            pirates.laser = 15;
            pirates.maxShields = 15; }
        if (d == 'H') {
            myShip.money = 100;
            myShip.maxCargoHold = 10;
            myShip.fuel =10;
            myShip.maxFuel = 10;
            myShip.shields = 10;
            myShip.laser = 10;
            pirates.laser = 20;
            pirates.maxShields = 20;
            Input.inputStr("Tip: visit ship maintenance first..."); }
        System.out.println();
    }
}

class Travel {      // travel between planets and meet pirates

    public static boolean Planet(Ship myShip, String[] planetNames, int[] planetLoc) {  // true if travelled
        System.out.println("\n---------- TRAVEL MENU ---------------");
        System.out.println("You are at " + planetNames[myShip.location] + " with " + myShip.fuel + " fuel.");
        System.out.println("Travel to another planet?\n");
        System.out.println("Planet            Distance");
        for (int i = 0; i < planetNames.length; ++i) {
            System.out.printf("%d. %-15s ", i, planetNames[i]);  // list planets
            if (i == myShip.location) {
                System.out.println(" 0 (You are here!)"); }      // fuel=0
            else {
                System.out.printf("%2d\n", planetLoc[i]); }     // fuel to get to planet
        }
        System.out.println();

        int planet = 999;           // dummy value
        while (true) {
            planet = Input.inputInt("Planet ("+myShip.location+" stays here): ");  // get destination
            if (myShip.location == planet) return false;      // did not travel anywhere
            if (planet < planetNames.length) {       // if valid planet number
                if (Math.abs(planetLoc[planet] - myShip.location) <= myShip.fuel) {
                    break; }    // enough fuel?
                else {
                    System.out.println("Not enough fuel to reach that planet!"); }
            }
        }
        myShip.location = planet;
        myShip.fuel -= Math.abs(planetLoc[planet] - myShip.location);
        System.out.println("Traveling to " + planetNames[myShip.location] + "...");
        return true;            // travelling to new planet
    }

    public static void Pirates(Ship myShip, Ship pirates, String[] goodsNames, String[] planetNames) {
        Random random = new Random();
        if (random.nextInt(10) > 5) return;    // chance of meeting pirates
        pirates.shields = pirates.maxShields;
        System.out.println("\n------------- Pirate Attack! --------------");
        System.out.println("Space pirates see you! They look dangerous!");
        System.out.println("Scanning pirates... Shields: " + pirates.shields + " | Laser: " + pirates.laser);
        System.out.println("Your ship defenses: Shields: " + myShip.shields + " | Laser: " + myShip.laser);

        while (myShip.shields >= 0 && pirates.shields >= 0) {
            char action = Input.inputChr("(F)ight or (R)un? ");

            if (action == 'R') {        // run!
                System.out.println("\nYou dump your cargo and burn all\nyour fuel to escape from them!");
                myShip.fuel = 0;
                myShip.cargoHold = 0;
                for (int i = 0; i < myShip.cargo.length; ++i)
                    myShip.cargo[i] = 0;    // set all cargo to 0
                return;
            }

            if (action == 'F') {        // fight!
                int shot = random.nextInt(myShip.laser);    // you shoot
                if (shot > 3) {                             // chance of hitting
                    pirates.shields -= shot;
                    System.out.println("You fire at the pirate ship causing " + shot + " damage."); }
                else {
                    System.out.println("You fire at the pirate ship... and miss!"); }
                shot = random.nextInt(pirates.laser);       // pirates shoot
                if (shot > 5) {                             // chance of hitting
                    myShip.shields -= shot;
                    System.out.println("The pirates shoot, causing " + shot + " damage.\nYour shields are down to " + myShip.shields); }
                else {
                    System.out.println("The pirates shoot... and miss!"); }
            }
            Input.inputStr("Press Enter...");
        }
        if (myShip.shields < 0) return;     // you're dead!
        pirates.laser += 1;                 // pirates get stronger if defeated
        pirates.maxShields += 2;

        // pick up pirate cargo
        System.out.println("You destroyed the pirate ship! Well done!");
        if (random.nextInt(10) < 5) return;     // chance of picking up pirate cargo
        System.out.println("The pirate ship's cargo is floating in space...");
        if (myShip.cargoHold == myShip.maxCargoHold) {
            System.out.println("but your cargo hold is already full.");
            return; }
        int q = random.nextInt(1, (myShip.maxCargoHold - myShip.cargoHold));    // quantity
        int c = random.nextInt(myShip.cargo.length);    // random cargo
        myShip.cargo[c] += q;
        myShip.cargoHold += q;      // amount of cargo in hold
        System.out.println("\nYou pick p " + goodsNames[c] + " x " + q);
        System.out.println("Continuing to planet " + planetNames[myShip.location]);
        Input.inputStr("Press a key to continue....");
    }
}

class ShipYard {
    public static void Maintenance(Ship myShip, String[] shipNames, String[] shipTypes) {
        while (true) {
            System.out.println("\n---------- Ship Maintenance -----------");
            System.out.printf("Ship: %-13s Type: %s\n", shipNames[myShip.shipType], shipTypes[myShip.shipType]);
            System.out.printf("Fuel: %-13s Max fuel: %s\n", myShip.fuel, myShip.maxFuel);
            System.out.printf("Cargo: %-12s Max cargo: %s\n", myShip.cargoHold, myShip.maxCargoHold);
            System.out.printf("Laser: %-12s Speed: %s\n", myShip.laser, myShip.speed);
            System.out.printf("Shields: %-10s Max shield: %s\n\n", myShip.shields, myShip.maxShields);
            System.out.println("0. Buy a bigger ship!     $200");
            System.out.println("1. Upgrade laser power    $ 30");
            System.out.println("2. Increase cargo hold    $ 20");
            System.out.println("3. Upgrade the shields     $ 30");
            System.out.println("4. Shields to maximum     $ 15");
            System.out.println("5. Fill up with fuel      $ 20");
            System.out.println("6. Bigger fuel tank       $ 25");
            System.out.println("\nYou have $"+myShip.money+" in the bank.");
            char action = Input.inputChr("Item to upgrade (L=Leave): ");
            if (action == '0') System.out.println(UpgradeShip(myShip, shipTypes, shipNames, 200));
            if (action == '1') System.out.println(UpgradeLaser(myShip, 30));
            if (action == '2') System.out.println(UpgradeCargo(myShip, 20));
            if (action == '3') System.out.println(UpgradeShields(myShip, 30));
            if (action == '4') System.out.println(RepairShields(myShip, 15));
            if (action == '5') System.out.println(FillWithFuel(myShip, 20));
            if (action == '6') System.out.println(UpgradeFuelTank(myShip, 25));
            if (action == 'L') return;
            Input.inputStr("Press Enter...");
        }
    }

    public static String UpgradeShip(Ship myShip, String[] shipTypes, String[] shipNames, int cost) {
        if ((myShip.shipType+1) == shipTypes.length) return "You already have the best ship.";
        myShip.shipType += 1;
        myShip.money -= cost;
        myShip.maxCargoHold += 10;
        myShip.maxFuel += 12;
        myShip.maxShields += 15;
        myShip.laser += 15;
        System.out.println("\nPurchased a "+shipNames[myShip.shipType]+" ("+shipTypes[myShip.shipType]+")");
        System.out.println("Upgrades: Fuel, cargo, shields, laser.");
//        Input.inputStr("Press Enter...");
        return " ";
    }
    public static String UpgradeLaser(Ship myShip, int cost) {
        if (cost > myShip.money) return "Not enough money for that!";
        myShip.money -= cost;
        myShip.laser += 3;
        return "Laser power boosted to "+myShip.laser;
    }
    public static String UpgradeCargo(Ship myShip, int cost) {
        if (cost > myShip.money) return "Not enough money for that!";
        myShip.money -= cost;
        myShip.maxCargoHold += 5;
        return "Cargo capacity is now "+myShip.maxCargoHold;
    }
    public static String UpgradeShields(Ship myShip, int cost) {
        if (cost > myShip.money) return "Not enough money for that!";
        myShip.money -= cost;
        myShip.maxShields += 5;
        return "Maximum shield capacity is now "+myShip.maxShields;
    }
    public static String RepairShields(Ship myShip, int cost) {
        if (myShip.shields == myShip.maxShields) return "Shields already at maximum.";
        if (cost > myShip.money) return "Not enough money for that!";
        myShip.money -= cost;
        myShip.shields = myShip.maxShields;
        return "Shields repaired. Now at maximum.";
    }
    public static String FillWithFuel(Ship myShip, int cost) {
        if (cost > myShip.money) return "Not enough money for that!";
        myShip.money -= cost;
        myShip.fuel = myShip.maxFuel;
        return "Fuel tanks filled. Fuel = "+myShip.fuel;
    }
    public static String UpgradeFuelTank(Ship myShip, int cost) {
        if (cost > myShip.money) return "Not enough money for that!";
        myShip.money -= cost;
        myShip.maxFuel += 5;
        return "Max fuel increased to "+myShip.maxFuel;
    }
}

class Market {
    public static void Show(Ship myShip, String[] planetNames, String[] goodsNames, int[][] planetGood, int[][] planetPrice) {
        while (true) {
            System.out.println("\n-------- "+planetNames[myShip.location]+" Market --------");
            System.out.println("   Item          Qty   Price");
            for (int i = 0; i < goodsNames.length; ++i)
                System.out.printf("%d. %-8s %8d   $%2d\n", i, goodsNames[i], planetGood[myShip.location][i], planetPrice[myShip.location][i]);
            System.out.println("\nYou have $"+myShip.money+" in your bank account.");
            System.out.println("Cargo usage: "+ myShip.cargoHold  +" of "+myShip.maxCargoHold+" storage units.");
//            System.out.println("There is a surplus of "+goodsNames[news[3]]+" at "+planetNames[news[1]]);
//            System.out.println("There is a shortage of "+goodsNames[news[3]]+" at "+planetNames[news[2]]+"\n");
            char bs = Input.inputChr("(B)uy, (S)ell, (L=Leave): ");
            if (bs == 'L') break;
            if (bs == 'B') System.out.println(BuyGoods(myShip, goodsNames, planetGood, planetPrice));
            if (bs == 'S') System.out.println(SellGoods(myShip, goodsNames, planetGood, planetPrice));
            Input.inputStr("Press Enter to continue...");
        }
    }

    public static String BuyGoods(Ship myShip, String[] goodsNames, int[][] planetGood, int[][] planetPrice) {
        int item = Input.inputInt("Buy which item (L=Leave)? ");
        if (item < 0 || item > goodsNames.length) return " ";
        int quantity = Input.inputInt("How many units to buy? ");
        if (quantity == 0 || quantity > 99) return " ";  // invalid value, eg L, quits
        int cost = quantity * planetPrice[myShip.location][item];

        if (quantity > planetGood[myShip.location][item]) return "Not enough stock!";
        if (cost > myShip.money) return "Not enough money!";
        if (quantity > (myShip.maxCargoHold - myShip.cargoHold)) return "Insufficient cargo space";

        myShip.money -= cost;
        myShip.cargoHold += quantity;
        planetGood[myShip.location][item] -= quantity;
        myShip.cargo[item] += quantity;        // cargo[] = qty of each cargoNames[] held
        return "\nYou bought "+quantity+" "+goodsNames[item]+" for $"+cost;
    }

    public static String SellGoods(Ship myShip, String[] goodsNames, int[][] planetGood, int[][] planetPrice) {
        myShip.ShowCargo(goodsNames);
        int item = Input.inputInt("\nSell which item (L=Leave)? ");
        if (item < 0 || item > goodsNames.length) return " ";
        if (myShip.cargo[item] == 0) return "You don't have any "+goodsNames[item];
        int quantity = Input.inputInt("How many units to Sell (L=Leave)? ");
        if (quantity == 0) return "No cargo sold.";
        if (quantity > myShip.cargo[item]) quantity = myShip.cargo[item];

        myShip.cargoHold -= quantity;
        myShip.cargo[item] -= quantity;
        myShip.money += quantity * planetPrice[myShip.location][item];  //planetPrice[planet][good price]
        planetGood[myShip.location][item] += quantity;                  // decrease planet stock
        return "You sold "+quantity+" of "+goodsNames[item]+" for $"+(quantity * planetPrice[myShip.location][item]);
    }

    public static void News(int[] news, int[][] planetPrice, String[] planetNames, int[][] planetGood, String[] goodsNames) {
        Random random = new Random();
        news[0] -= 1;    // [0]=counter, [1]=planet1, [2]=planet2, [3]=good
        if (news[0] > 0) return;
        Initialise.GoodsQuantities(planetGood);                     // good time to vary goods quantities
        planetPrice[news[1]][news[3]] = random.nextInt(20);  // reset price planet 1
        planetPrice[news[2]][news[3]] = random.nextInt(20);  // reset price planet 2

        // create goods shortage and surplus
        news[0] = 5;        // countdown - do this every 5th time
        news[1] = random.nextInt(planetNames.length);                           // random planet1
        news[2] = (news[1] + random.nextInt(1,5)) % planetNames.length; // random planet2
        news[3] = random.nextInt(goodsNames.length);                            // random good
        planetGood[news[1]][news[3]] = random.nextInt(15, 30);        // surplus
        planetPrice[news[1]][news[3]] = random.nextInt(3, 10);        // low price
        planetGood[news[2]][news[3]] = random.nextInt(5);                   // shortage
        planetPrice[news[2]][news[3]] = random.nextInt(15, 30);       // high price
        System.out.println("There is a surplus of "+goodsNames[news[3]]+" at "+planetNames[news[1]]);
        System.out.println("There is a shortage of "+goodsNames[news[3]]+" at "+planetNames[news[2]]+"\n");
    }
}

public class SpaceTrader {
    public static void main(String[] args) {
        Random random = new Random();   // random start planet
        String[] shipNames = {"Vortexodus","Nebulaburst","Celestiadon","Titansta"};
        String[] shipTypes = {"Small shuttle","Medium cargo","Large cargo","Super hauler"};
        String[] planetNames = {"Kyrathia","Aurorion","Thornfell","Drelkoria","Sylnostar","Thalendor","Vaeltiria","Vordrak","Lynadrius","Thyronak"};
        String[] goodsNames = {"Silver","Gold","Food","Iron","Tech","Arms"};    // 6 goods
        int[] planetLoc = new int[10];              // 10 planets - locations
        int[][] planetGood = new int[10][6];        // 10 planets x 6 goods - quantity
        int[][] planetPrice = new int[10][6];       // 10 planets x 6 goods - price
        int[] news = {0,0,0,0};                    // [0]=counter, [1]=planet1, [2]=planet2, [3]=good
        
        System.out.println("-----------------------------------------");
        System.out.println("  S P A C E    T R A D E R    E L I T E  ");
        System.out.println("-----------------------------------------");
        System.out.println("Travel between planets buying and selling");
        System.out.println("goods, but watch out for space pirates!\n");
        Ship myShip = new Ship();
        Ship pirates = new Ship();
        myShip.location = random.nextInt(10);
        Initialise.PlanetLocations(planetLoc);
        Initialise.GoodsPrices(planetPrice);
        Initialise.GoodsQuantities(planetGood);
        Initialise.Difficulty(myShip, pirates);

        boolean showNews = false;        // whether to show Market.News
        while (myShip.shields > -1) {
            System.out.println("\n------------ MAIN MENU ---------------");
            System.out.println(" You are docked at planet " + planetNames[myShip.location] + ".");
            System.out.println(" Cargo: "+myShip.cargoHold+" | Fuel: "+myShip.fuel+" | Money: $"+myShip.money+"\n");
            System.out.println(" 1. Visit the market");
            System.out.println(" 2. View cargo hold");
            System.out.println(" 3. Ship maintenance");
            System.out.println(" 4. Leave the planet");
            System.out.println(" Q. Quit game\n");
            if (showNews) {                 // don't show first time around game loop
                Market.News(news, planetPrice, planetNames, planetGood, goodsNames);
                showNews = false; }         // show once on arriving at a new planet
            char action = Input.inputChr("Action: ");
            if (action == 'Q') break;
            if (action == '1') Market.Show(myShip, planetNames, goodsNames, planetGood, planetPrice);
            if (action == '2') {
                myShip.ShowCargo(goodsNames);
                Input.inputStr("Press Enter..."); }
            if (action == '3') ShipYard.Maintenance(myShip, shipNames, shipTypes);
            if (action == '4') {
                if (Travel.Planet(myShip, planetNames, planetLoc)) {        // if you've travelled
                    Travel.Pirates(myShip, pirates, goodsNames, planetNames);
                    showNews = true;    // show news when you travel to a new planet
                }
            }
        }

        System.out.println("Game Over!");
        if (myShip.shields < 0)
            System.out.println("Your ship was destroyed!");
    }
}