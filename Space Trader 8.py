# Space Trader - Go to market_news
# https://github.com/rwaddilove/spacetrader
# By Roland Waddilove as a Python learning exercise
# Travel from planet to planet buying and selling goods, but watch out for space pirates!
# There are many Space Trader games and I don't know who originally created it.
# ship and planet names from https://taverncrowd.com/

import random
import os

ship_names = ['Vortexodus', 'Small shuttle',
              'Nebulaburst', 'Medium cargo',
              'Celestiadon', 'Large cargo',
              'Titanstar', 'Super hauler'
              ]

planet_names = ['Kyrathia', 'Aurorion', 'Thornfell', 'Drelkoria', 'Sylnostar',
                'Thalendor', 'Vaeltiria', 'Vordrak', 'Lynadrius', 'Thyronak']

planetloc = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]    # planet locations

goods_names = ['Silver', 'Gold', 'Food', 'Iron', 'Tech', 'Arms']

# amount of each good for each panet (row)
planetgood = [
    [0, 0, 0, 0, 0, 0],
    [0, 0, 0, 0, 0, 0],
    [0, 0, 0, 0, 0, 0],
    [0, 0, 0, 0, 0, 0],
    [0, 0, 0, 0, 0, 0],
    [0, 0, 0, 0, 0, 0],
    [0, 0, 0, 0, 0, 0],
    [0, 0, 0, 0, 0, 0],
    [0, 0, 0, 0, 0, 0],
    [0, 0, 0, 0, 0, 0]
]

# price of each item in goods_names[] for each planet (row)
planetprice = [
    [0, 0, 0, 0, 0, 0],
    [0, 0, 0, 0, 0, 0],
    [0, 0, 0, 0, 0, 0],
    [0, 0, 0, 0, 0, 0],
    [0, 0, 0, 0, 0, 0],
    [0, 0, 0, 0, 0, 0],
    [0, 0, 0, 0, 0, 0],
    [0, 0, 0, 0, 0, 0],
    [0, 0, 0, 0, 0, 0],
    [0, 0, 0, 0, 0, 0]
]

over_supply = [0,0,0,0]       # counter,good,planet1,planet2 - too many goods lower prices

class Ship:
    def __init__(self) -> None:     # Default values are for the easy level
        self.shiptype = 0
        self.money = 200
        self.cargo_hold = 0         # amount of cargo held
        self.max_cargo_hold = 20    # maximum
        self.shields = 20
        self.max_shields = 20
        self.fuel = 20              # how far you can go
        self.max_fuel = 20          # max distance
        self.speed = 20
        self.location = 0           # randomised later
        self.laser = 20
        self.cargo = [0, 0, 0, 0, 0, 0]         # quantity of each item in goods_names[]
        self.cargo_price = [0, 0, 0, 0, 0, 0]   # price of each item bought


def set_difficulty() -> None:       # set Medium or Hard levels. Easy is default
    s = input("\nEnter difficulty (E)asy, (M)edium, (H)ard: ").upper()
    if s == 'M':
        myship.money = 100
        myship.max_cargo_hold = 20
        myship.fuel = 20
        myship.max_fuel = 20
        myship.shields = 20
        myship.laser = 20
    if s == 'H':
        myship.money = 100
        myship.max_cargo_hold = 0
        myship.fuel = 0
        myship.max_fuel = 10
        myship.shields = 0
        myship.laser = 0
        input("Tip: visit ship maintenance first...")
        print()


def create_planets() -> None:
    for row in range(len(planetgood)):                          # for each planetgood[] row
        planetloc[row] = random.randint(0, 40)              # planet location
        for good in range(len(goods_names)):                    # for each item in goods_names[]
            planetgood[row][good] = random.randint(0, 20)      # amount varies with each visit
            planetprice[row][good] = random.randint(10, 30)    # price set for the game, except news()


def market() -> None:
    print("----------------------------")
    print(f"    {planet_names[myship.location]} Market")
    print("----------------------------")
    bs = ''             # buy/sell
    while bs != 'M':
        show_goods()    # show items for sale in planet[]
        print(f"\nYou have ${myship.money} to spend.")
        print(f"Cargo = {myship.cargo_hold} units, ({myship.max_cargo_hold - myship.cargo_hold} free space).")
        bs = input("(B)uy or (S)ell (M)ain menu? ").upper()
        if bs == 'B': print(buy_goods())
        if bs == 'S': print(sell_goods())
        if bs != 'M': print(f"-------- {planet_names[myship.location]} Market --------")


def show_goods() -> None:
    print(f"    Item      Amount   Price")
    for i in range(len(goods_names)):
        print(f"{i:>2}. {goods_names[i]:<10}", end="")                  # goods name
        print(f"{planetgood[myship.location][i]:>4}     ", end="")      # amount
        print(f"${planetprice[myship.location][i]:>2}")                 # price


def buy_goods() -> str:         # called by market()
    i = 999                     # item to buy
    while i < 0 or i >= len(goods_names):
        inp = input("Buy which item? ")
        if inp == '': return " "
        if inp.isdigit(): i = int(inp)
    q = 0                       # quantity to buy
    while q < 1:
        inp = input("Quantity? ")
        if inp == '': return " "
        if inp.isdigit(): q = int(inp)

    stock = planetgood[myship.location][i]
    price = planetprice[myship.location][i]

    if q > stock: return "\n===> Not enough stock of that item.\n"
    if q + myship.cargo_hold > myship.max_cargo_hold: return "===> Not enough cargo space!\n"
    if q * price > myship.money: return "===> Not enough money!\n"

    planetgood[myship.location][i] = stock - q    # reduce planet stock
    myship.money -= q * price       # subtract cost
    myship.cargo_hold += q          # increase used cargo space
    myship.cargo[i] += q            # add this item to ship cargo
    myship.cargo_price[i] = price   # save the price paid
    return f"Bought {goods_names[i]}\n"


def sell_goods() -> str:        # called by market()
    show_cargo()
    i = 999                     # item to sell
    while i < 0 or i >= len(goods_names):
        inp = input("Sell which item ($5 tax)? ")
        if inp == '': return " "
        if inp.isdigit(): i = int(inp)
    q = 0                       # quantity to sell
    while q < 1:
        inp = input("Quantity? ")
        if inp == '': return " "
        if inp.isdigit(): q = int(inp)
    if q > myship.cargo[i]: return "===> Not enough of that item.\n"

    price = planetprice[myship.location][i]
    myship.cargo[i] -= q
    planetgood[myship.location][i] += q
    myship.money += (q * price) - 5         # sales tax
    if myship.money <0: myship.money = 0
    myship.cargo_hold -= q      # decrease used cargo space
    return f"\nSold! Returning to {planet_names[myship.location]} market\n"


def show_cargo() -> None:   # list the cargo items you are carrying and current planet prices
    print("You have the following cargo items,")
    print("with local market prices per unit:")
    print("\n    Item        Qty   Price")
    for i in range(len(goods_names)):
        print(f"{i:>2}. {goods_names[i]:<10}", end="")              # good name
        print(f"{myship.cargo[i]:>4}     ", end="")                 # amount you have
        print(f"${planetprice[myship.location][i]:>2}", end="")     # local price
        if myship.cargo[i] > 0:
            print(f" (You paid ${myship.cargo_price[i]})")
        else:
            print()
    print(f"\nYou have {myship.max_cargo_hold - myship.cargo_hold} space left in the cargo hold.")

def leave_planet() -> None:
    print("----------------------------------")
    print(f" Nearby planets. You have {myship.fuel} fuel")
    print("----------------------------------")
    print("    Planet      Distance")
    for row in range(len(planet_names)):      # for each planet[row]
        print(f"{row:>2}. {planet_names[row]:<12}   {(abs(planetloc[row] - planetloc[myship.location])):>2}")

    # input planet to go to
    fr = 999    #fuel required
    p = 0       # planet to go to
    inp = ''
    while myship.fuel < fr and inp != 'M':
        inp = input("\nPlanet to visit, or (M)ain menu: ").upper()
        if inp.isdigit():
            p = int(inp)      # get planet to visit
            fr = abs(planetloc[p] - planetloc[myship.location])     # fuel required
            if myship.fuel < fr: print(f"Not enough fuel! (Fuel = {myship.fuel})")
    if inp == 'M': return

    # travel to new planet
    myship.location = p
    myship.fuel -= fr
    print(f"\nTraveling to {planet_names[myship.location]}...")
    input("Enter hyperspace...")
    if random.randint(0, 10) > 4: space_pirates()      # random chance of space pirates


def planet_goods_changes() -> None:
    for row in range(len(planetgood)):                        # for ech planet[row]
        for n in range(len(goods_names)):                       # for each item in goods_names[]
            planetgood[row][n] = random.randint(0, 20)      # amount


def news() -> None:
    if over_supply[0] > 0:                  # over_supply[]=counter,good,planet1,planet2
        # print(f"\nNews: {planet_names[over_supply[2]]} has {goods_names[over_supply[1]]} oversupply.")
        # print(f"{planet_names[over_supply[3]]} desperately needs {goods_names[over_supply[1]]}.\n")
        over_supply[0] -= 1                 # counter
        if over_supply[0] > 0: return       # not ready to reset over supply?
        planetprice[over_supply[2]][over_supply[1]] = random.randint(10, 30)    # planet1 reset price
        planetprice[over_supply[3]][over_supply[1]] = random.randint(10, 30)    # planet2 reset price
        planet_goods_changes()              # good time to reset all planet goods amounts
        return

    # pick planets and good for over/undersupply
    g = random.randint(0, len(goods_names) - 1)     # random good
    p1 = random.randint(0, len(planetgood) - 1)     # random planet
    p2 = random.randint(0, len(planetgood) - 1)     # random planet
    while p1 == p2:
        p2 = random.randint(0, len(planetgood) - 1)  # random planet
    over_supply[0] = 10         # counter -1 every time main menu shown
    over_supply[1] = g          # good
    over_supply[2] = p1         # planet 1
    over_supply[3] = p2         # planet 2
    planetgood[p1][g] = random.randint(30, 50)      # large stock planet 1 good
    planetprice[p1][g] = random.randint(1, 10)      # low price planet 1 good
    planetgood[p2][g] = random.randint(0, 5)        # low stock planet 2 good
    planetprice[p2][g] = random.randint(20, 30)      # high price planet 2 good
    print(f"\nNews: {planet_names[p1]} has {goods_names[g]} oversupply.")
    print(f"{planet_names[p2]} desperately needs {goods_names[g]}.")


def space_pirates() -> None:
    pirates.shields = pirates.max_shields
    print("Space pirates see you! They look dangerous!")
    print(f"Shields at {myship.shields}. Pirates estimated at {pirates.shields}")
    s = ''
    while s == '' or s not in 'RF':
        s = input("(F)ight or (R)un? ").upper()
    print()
    if s == 'R':
        print("You burn all your fuel to get away from them!")
        myship.fuel = 0
        return
    
    # battle with pirates
    while myship.shields >= 0 and pirates.shields >= 0:
        damage = random.randint(0, pirates.laser)   # pirate shots more variable
        myship.shields -= damage
        if damage > 0:
            print(f"The pirates fire at you and do {damage} damage!")
        else:
            print(f"The pirates fire at you, but they miss!")
        damage = random.randint(5, myship.laser)    # you never miss
        pirates.shields -= damage
        print(f"You fire at the pirates causing {damage} damage!")
    if myship.shields < 0: return       # you lost!
    print(f"\nYes! You destroyed the pirate ship!")
    pirates.laser += 1          # pirates get stronger each time defeated
    pirates.max_shields += 2

    # pick up pirate cargo
    n = random.randint(0, 5)     # random amount of pirate cargo
    if n == 0 or myship.cargo_hold == myship.max_cargo_hold:   # no cargo or no space
        input("Enter to continue to planet...")
        return
    if (myship.cargo_hold + n) > myship.max_cargo_hold:   # got cargo space?
        n = myship.max_cargo_hold - myship.cargo_hold   # don't exceed space
    c = random.randint(0,len(myship.cargo)-1)    # cargo[] item to add to
    myship.cargo[c] += n        # add cargo
    myship.cargo_price[c] = 0   # price
    myship.cargo_hold += n      # amount of cargo held
    print(f"You salvage their cargo: {goods_names[c]} x {n}")
    input("Enter to continue to planet...")


def ship_maintenance() -> None:
    print("----------------------------")
    print(" Ship Status & Maintenance")
    print("----------------------------")
    print(f"Ship: {ship_names[myship.shiptype]}".ljust(18), f"({ship_names[myship.shiptype + 1]})\n")
    print(f"Fuel: {myship.fuel}".ljust(18), f"Max fuel: {myship.max_fuel}")
    print(f"Cargo held: {myship.cargo_hold}".ljust(18), f"Max cargo: {myship.max_cargo_hold}")
    print(f"Shields: {myship.shields}".ljust(18), f"Max shield: {myship.max_shields}")
    print(f"Laser power: {myship.laser}".ljust(18), f"Speed: {myship.speed} ltyr/h")
    print(f"\nMaintenance options:")
    print(f"0. Buy a bigger ship!     $200")
    print(f"1. Upgrade laser power    $ 30")
    print(f"2. Increase cargo hold    $ 20")
    print(f"3. Get better shields     $ 30")
    print(f"4. Repair the shields     $ 15")
    print(f"5. Fill up with fuel      $ 20")
    print(f"6. Bigger fuel tank       $ 25")
    s = ''
    while s != 'M':
        print(f"\nYou have ${myship.money} in the bank.")
        s = input("Item to upgrade, or (M)ain menu: ").upper()
        if s == '0': upgrade_ship(200)
        if s == '1': upgrade_laser(30)
        if s == '2': upgrade_cargo(20)
        if s == '3': upgrade_shields(30)
        if s == '4': repair_shields(15)
        if s == '5': fill_with_fuel(20)
        if s == '6': upgrade_fuel_tank(25)


def upgrade_fuel_tank(cost) -> None:
    if myship.money < cost:
        print("Not enough money for that!")
        return
    myship.max_fuel += 5
    print("Fuel tank increased by 10.")

def fill_with_fuel(cost) -> None:
    if myship.money < cost:
        print("Not enough money for that!")
        return
    myship.fuel = myship.max_fuel
    print(f"Fuel tank topped up. Fuel = {myship.fuel}")

def upgrade_shields(cost) -> None:
    if myship.money < cost:
        print("Not enough money for that!")
        return
    myship.max_shields += 5
    myship.money -= cost
    print(f"Laser power now {myship.laser}. Pirates beware!")

def repair_shields(cost) -> None:
    if myship.shields == myship.max_shields:
        print("Shields already at full power.")
        return
    if myship.money < cost:
        print("Not enough money for that!")
        return
    myship.shields = myship.max_shields
    myship.money -= cost
    print("Shields now at maximum.")

def upgrade_cargo(cost) -> None:
    if myship.money < cost:
        print("Not enough money for that!")
        return
    myship.max_cargo_hold += 5
    myship.money -= cost
    print(f"Cargo carrying capacity now {myship.max_cargo_hold}")

def upgrade_laser(cost) -> None:
    if myship.money < cost:
        print("Not enough money for that!")
        return
    myship.laser += 3
    myship.money -= cost
    print(f"Upgraded laser to {myship.laser} power.")

def upgrade_ship(cost) -> None:
    if myship.money < cost:
        print("Not enough money for that!")
        return
    if (myship.shiptype + 2) == len(ship_names):
        print("You have the biggest ship.")
        return
    myship.shiptype += 2            # ship names = name,type pairs
    myship.max_cargo_hold += 15
    myship.max_fuel += 15
    myship.max_shields += 15
    myship.laser += 15
    myship.money -= cost
    print(f"\nYour new ship is {ship_names[myship.shiptype]} ({ship_names[myship.shiptype + 1]})")  # see shipnames[]
#    print("Increased cargo, fuel, shields and laser.\n")
    print(f"Max fuel: {myship.max_fuel}".ljust(16), f"|  Max cargo: {myship.max_cargo_hold}")
    print(f"Laser power: {myship.laser}".ljust(16), f"|  Max shield: {myship.max_shields}")


# ================ MAIN ============================
# os.system('cls') if os.name == 'nt' else os.system('clear')

print("\n-----------------------------------------")
print("  S P A C E    T R A D E R    E L I T E  ")
print("-----------------------------------------")
print("Travel between planets buying and selling")
print("goods, but watch out for space pirates!")

create_planets()
myship = Ship()
myship.location = random.randint(0, 9)      # random planet
pirates = Ship()                                # bad guys have ships!
set_difficulty()
pirates.max_shields = myship.max_shields // 2   # make it easy!
pirates.laser = myship.laser // 2               # you have a better laser

game_over = False
while not game_over:
    print("\n--------------------------------------")
    print(f" You are docked at planet {planet_names[myship.location]}.")
    print(f" Cargo: {myship.cargo_hold} | Fuel: {myship.fuel} | Money: ${myship.money}")
    print("--------------------------------------")
    news()
    print()
    print(" 1. Visit the market")
    print(" 2. View cargo hold")
    print(" 3. Ship maintenance")
    print(" 4. Leave the planet")
    print(" Q. Quit game\n")
    s = input("Action: ").upper()
    print()
    if s == '1': market()
    if s == '2': show_cargo()
    if s == '3': ship_maintenance()
    if s == '4': leave_planet()
    if s == 'Q': game_over = True
    if myship.shields < 0:
        print("Game over: Your ship was destroyed!")
        game_over = True
    if myship.money + myship.cargo_hold == 0:
        print("Game Over: You have no money and no cargo!")
        game_over = True
print()
