import os

# use the upper one when coding, use lower one when testing and competing
# import battlecode.python.battlecode as bc
import battlecode as bc

import random
import sys
import traceback
print("current path:\n"+os.getcwd())
print("testing new player")

####################### HELPERS ##########################
def getEnemyTeam(myteam):
    if myteam == bc.Team.Red:
        return bc.Team.Blue
    else:
        return bc.Team.Red

# def moveToLocationBasic(ourLoc, targetLoc):
#     moveDirection = ourLoc.
#

def findNearestUnit(ourMapLocation,units):
    nearestUnit = units[0]
    minDistance = 999999999
    for unit in units:
        otherLocation = unit.location.map_location()
        distanceSqr = ourMapLocation.distance_squared_to(otherLocation)
        if distanceSqr < minDistance:
            minDistance = distanceSqr
            nearestUnit = unit
    return nearestUnit

####################### HELPERS ########################

# A GameController is the main type that you talk to the game with.
# Its constructor will connect to a running game.
gc = bc.GameController()
directions = list(bc.Direction)

print("pystarted")

# It's a good idea to try to keep your bots deterministic, to make debugging easier.
# determinism isn't required, but it means that the same things will happen in every thing you run,
# aside from turns taking slightly different amounts of time due to noise.
random.seed(6137)

# let's start off with some research!
# we can queue as much as we want.
gc.queue_research(bc.UnitType.Worker)
gc.queue_research(bc.UnitType.Knight)
gc.queue_research(bc.UnitType.Knight)
gc.queue_research(bc.UnitType.Rocket)

my_team = gc.team()
enemy_team = getEnemyTeam(my_team)
print("team: ", my_team, enemy_team)

worker_limit = 8
factory_limit = 6
worker_danger_limit = 1

while True:
    # FOR EACH ROUND OF THE GAME
    # We only support Python 3, which means brackets around print()
    print('pyround:', gc.round())

    # first we figure out how many unit of each type we currently have
    n_worker = 0
    n_factory = 0
    n_knight = 0
    for unit in gc.my_units():
        if unit.unit_type == bc.UnitType.Factory:
            n_factory += 1
        elif unit.unit_type == bc.UnitType.Worker:
            n_worker += 1
        elif unit.unit_type == bc.UnitType.Knight:
            n_knight += 1

    # now actual unit logics
    # frequent try/catches are a good idea
    try:
        # walk through our units:
        for unit in gc.my_units():
            move_found = False
            #FACTORY LOGIC ###########################
            # simply unload anything and then produce knight if possible
            if unit.unit_type == bc.UnitType.Factory:
                garrison = unit.structure_garrison()
                if len(garrison) > 0:
                    for d in directions:
                        if gc.can_unload(unit.id, d):
                            gc.unload(unit.id, d)
                            move_found = True
                            break
                elif n_worker<worker_danger_limit and gc.can_produce_robot(unit.id, bc.UnitType.Worker):
                    gc.produce_robot(unit.id, bc.UnitType.Worker)
                    move_found = True
                elif gc.can_produce_robot(unit.id, bc.UnitType.Knight):
                    gc.produce_robot(unit.id, bc.UnitType.Knight)
                    move_found = True
                if move_found:
                    continue

            # WORKER LOGIC ###########################
            elif unit.unit_type == bc.UnitType.Worker:
                location = unit.location
                if location.is_on_map():
                    ## first, let's look for nearby blueprints to work on
                    nearby = gc.sense_nearby_units(location.map_location(), 2)
                    for other in nearby:
                        if gc.can_build(unit.id, other.id):
                            gc.build(unit.id, other.id)
                            move_found = True
                            break
                    if move_found:
                        continue

                    ## nothing to keep building, then see if should build factory
                    # pick a random direction:
                    d = random.choice(directions)
                    #try to build a factory if we need more factories:
                    if gc.karbonite() > bc.UnitType.Factory.blueprint_cost() and gc.can_blueprint(unit.id, bc.UnitType.Factory, d) and n_factory<5:
                        gc.blueprint(unit.id, bc.UnitType.Factory, d)
                        move_found = True

                    if move_found:
                        continue

                    d = random.choice(directions)
                    if n_worker<worker_limit and gc.can_replicate(unit.id,d):
                        gc.replicate(unit.id,d)
                        move_found = True

                    if move_found:
                        continue

                    ## now check if there are karbonite to harvest.
                    for d in directions:
                        if gc.can_harvest(unit.id,d):
                            gc.harvest(unit.id,d)
                            #TODO need more systematic harvesting according to map info
                            # print("harvest success!!!!!!!!!!!!!")
                            move_found = True
                            break
                    if move_found:
                        continue

                    ## and if nothing to harvest, then move
                    d = random.choice(directions)
                    if gc.is_move_ready(unit.id) and gc.can_move(unit.id, d):
                        gc.move_robot(unit.id, d)
                        move_found = True
                    if move_found:
                        continue

            # KNIGHT LOGIC #####################################
            elif unit.unit_type == bc.UnitType.Knight:
                knight_moved = False
                location = unit.location
                if location.is_on_map():
                    ourMapLoc = location.map_location()
                    ## first, let's look for enemies in vision (not usre if -1 works?)
                    # 50 is the sense range of knight
                    enemies = gc.sense_nearby_units_by_team(location.map_location(),50,enemy_team)
                    if len(enemies)>0:
                        nearestEnemy = findNearestUnit(ourMapLoc,enemies)
                        dirToNearestEnemy = ourMapLoc.direction_to(nearestEnemy.location.map_location())
                        if gc.is_move_ready(unit.id) and gc.can_move(unit.id, dirToNearestEnemy):
                            gc.move_robot(unit.id, dirToNearestEnemy)
                        if gc.is_attack_ready(unit.id)  and gc.can_attack(unit.id,nearestEnemy.id):
                            gc.attack(unit.id, nearestEnemy.id)
                    if gc.is_move_ready(unit.id): # if didn't move to an enemy, then move randomly
                        d = random.choice(directions)
                        if gc.can_move(unit.id, d):
                            gc.move_robot(unit.id, d)


    except Exception as e:
        print('Error:', e)
        # use this to show where the error was
        traceback.print_exc()

    # send the actions we've performed, and wait for our next turn.
    gc.next_turn()

    # these lines are not strictly necessary, but it helps make the logs make more sense.
    # it forces everything we've written this turn to be written to the manager.
    sys.stdout.flush()
    sys.stderr.flush()