// import the API.
// See xxx for the javadocs.
import bc.*;

import java.util.Random;
import java.util.ArrayList;
public class Player {

    public static Random RAND = new Random();

    public static ArrayList<MapLocation> knownEnemyLocations=new ArrayList<MapLocation>();
    public static int knownEnemyLocLimit = 30;
    public static float knownEnemyLocRefreshRate = 0.5f;

    public static Direction getRandomDirection(){
        Direction[] directions = Direction.values();
        int i = RAND.nextInt(directions.length);
        return directions[i];
    }



    public static void main(String[] args) {
        // You can use other files in this directory, and in subdirectories.
        Extra extra = new Extra(27);
        System.out.println(extra.toString());

        // MapLocation is a data structure you'll use a lot.
        MapLocation loc = new MapLocation(Planet.Earth, 10, 20);
        System.out.println("loc: "+loc+", one step to the Northwest: "+loc.add(Direction.Northwest));
        System.out.println("loc x: "+loc.getX());

        // One slightly weird thing: some methods are currently static methods on a static class called bc.
        // This will eventually be fixed :/
        System.out.println("Opposite of " + Direction.North + ": " + bc.bcDirectionOpposite(Direction.North));

        // Connect to the manager, starting the game
        GameController gc = new GameController();
        GameMap gm = new GameMap();
        Team myteam = gc.team();
        Team enemyTeam;
        if (myteam==Team.Blue){
            enemyTeam = Team.Red;
        }else{
            enemyTeam = Team.Blue;
        }
        /*Planet thisPlanet = gc.planet();
        Planet otherPlanet;
        if (thisPlanet==Planet.Earth){
            otherPlanet == Planet.Mars;
        }else{
            otherPlanet == Planet.Earth;
        }*/

        // Direction is a normal java enum.
        Direction[] directions = Direction.values();

        gc.queueResearch(UnitType.Worker);
        gc.queueResearch(UnitType.Worker);
        gc.queueResearch(UnitType.Rocket);
        gc.queueResearch(UnitType.Rocket);
        gc.queueResearch(UnitType.Ranger);
        gc.queueResearch(UnitType.Ranger);
        gc.queueResearch(UnitType.Knight);

        //Constants
        int workerLimit = 10;
        int factoryLimit = 6;
        int rangerLimit = 30;
        int rocketLimit = 1;


        //The code in the loop runs each round
        while (true) {

            // for each round
            System.out.println("Current round: "+gc.round());
            int n_worker = 0;
            int n_factory = 0;
            int n_ranger = 0;
            int n_knight = 0;
            int n_rocket = 0;

            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            // first get to know how many of each unit we have
            VecUnit units = gc.myUnits();
            for (int i = 0; i < units.size(); i++) {
                Unit unit = units.get(i);
                switch (unit.unitType()){
                    case Factory:
                        n_factory ++;
                        break;
                    case Ranger:
                        n_ranger++;
                        break;
                    case Worker:
                        n_worker++;
                        break;
                    case Knight:
                        n_knight++;
                        break;
                    case Rocket:
                        n_rocket++;
                        break;
                }
            }

            System.out.println("Current round: "+gc.round());
            for (int i = 0; i < units.size(); i++) {
                Unit unit = units.get(i);
                int uid = unit.id();
                if (!unit.location().isOnMap()){continue;}
                MapLocation maploc = unit.location().mapLocation();
                Direction dir = getRandomDirection();
                // Most methods on gc take unit IDs, instead of the unit objects themselves.
                switch (unit.unitType()){
                    case Worker:
                        //Replicate first
                        if(n_worker<workerLimit && Math.random()<0.5){
                            for (int j = 0; j < directions.length; j++) {
                                dir = directions[j];
                                if (gc.canReplicate(uid,dir)){
                                    gc.replicate(uid,dir);
                                    break;
                                }
                            }
                        }
                        //Build first
                        VecUnit nearbyUnits = gc.senseNearbyUnits(maploc,2);
                        for (int j = 0; j < nearbyUnits.size(); j++) {
                            Unit other = nearbyUnits.get(j);
                            if (gc.canBuild(uid,other.id())){
                                gc.build(uid,other.id());
                                break;
                            }
                        }
                        //Then start building factories
                        for (int j = 0; j < directions.length; j++) {
                            dir = directions[j];
                            if (n_factory<factoryLimit && gc.canBlueprint(uid,UnitType.Factory,dir)){
                                gc.blueprint(uid,UnitType.Factory,dir);
                                break;
                            }
                        }
                        //Try build 1 rocket
                        for (int j = 0; j < directions.length; j++) {
                            dir = directions[j];
                            if (n_rocket<rocketLimit && gc.canBlueprint(uid,UnitType.Rocket,dir)){
                                gc.blueprint(uid,UnitType.Rocket,dir);
                                break;
                            }
                        }
                        //Randomly harvest (Should be able to look for resources)
                        for (int j = 0; j < directions.length; j++) {
                            dir = directions[j];
                            if (gc.canHarvest(uid,dir)){
                                gc.harvest(uid,dir);
                                break;
                            }
                        }
                        if (gc.isMoveReady(uid)){
                            dir = getRandomDirection();
                            if (gc.canMove(uid, dir)) {
                                gc.moveRobot(unit.id(), dir);
                            }
                        }
                        break;
                    case Factory:
                        //Unload unit
                        for (int j = 0; j < directions.length; j++){
                            dir = directions[j];
                            if(gc.canUnload(uid,dir)){
                                gc.unload(uid,dir);
                                break;
                            }
                        }
                        //If cannot unload, build worker first, then ranger
                        if(gc.canProduceRobot(uid,UnitType.Worker) && n_worker<workerLimit){
                            gc.produceRobot(uid,UnitType.Worker);
                        }
                        if(gc.canProduceRobot(uid,UnitType.Ranger) && n_ranger<rangerLimit && Math.random()<0.9){
                            gc.produceRobot(uid,UnitType.Ranger);
                        }
                        if (gc.canProduceRobot(uid,UnitType.Knight)){
                            gc.produceRobot(uid,UnitType.Knight);
                        }
                        break;
                    case Knight:
                        break;
                    case Ranger:
                        VecUnit enemies = gc.senseNearbyUnitsByTeam(maploc,unit.visionRange(),enemyTeam);
                        if(enemies.size()>0) {
                            knownEnemyLocations.add(enemies.get(0).location().mapLocation());
                        }
                        if(gc.isAttackReady(uid)){
                            Unit targetUnit = gc.senseUnitAtLocation(knownEnemyLocations.get(0));
                            if (targetUnit!=null && gc.canAttack(uid,targetUnit.id())){
                                gc.attack(uid,targetUnit.id());
                            }
                        }
                        //Move randomly
                        if(gc.isMoveReady(uid)){
                            if(knownEnemyLocations.size()>0)
                            {
                                float distance = maploc.distanceSquaredTo(knownEnemyLocations.get(0));
                                if(distance>unit.rangerCannotAttackRange()+40)
                                {
                                    Direction movedir = Direction.Center;
                                    if(gc.canMove(uid,movedir)){
                                        gc.moveRobot(uid,movedir);
                                    }
                                }
                            }
                            else {
                                dir = getRandomDirection();
                                if (gc.canMove(uid, dir)) {
                                    gc.moveRobot(uid, dir);
                                }
                            }
                        }
                        break;
                    case Rocket:
                        VecUnit	nearbyWorker = gc.senseNearbyUnitsByType(maploc,2,UnitType.Worker);
                            for (int j = 0; j < nearbyWorker.size(); j++) {
                            Unit other = nearbyWorker.get(j);
                            if(gc.canLoad(uid,other.id()))
                            {
                                gc.load(uid,other.id());
                                break;
                            }
                        }
                        VecUnitID isLoaded = unit.structureGarrison();
                            for (int n = 0; n < 10; n++) {
                                long j = (int) Math.random() * gm.getMars_map().getHeight();
                                long k = (int) Math.random() * gm.getMars_map().getWidth();
                                MapLocation landingLoc = new MapLocation(Planet.Mars,(int)j,(int)k);
                                if (gc.canLaunchRocket(uid, landingLoc) && isLoaded.size() > 0 && gm.getMars_map().isPassableTerrainAt(landingLoc) == 1) {
                                    gc.launchRocket(uid, landingLoc);
                                    break;
                                }
                            }
                        break;
                }
            }
            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
    }
}