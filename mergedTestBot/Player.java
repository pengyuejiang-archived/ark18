import bc.*;

public class Player {

    // Useful fields that are needed constantly, globally
    public static GameController gc = new GameController();
    public static Direction[] dirs = Direction.values();
    // Teams
    public static Team myTeam = gc.team();
    public static Team enemy = enemyOf(myTeam);
    // Statistics
    public static int workerCount = 0;
    public static int knightCount = 0;
    public static int rangerCount = 0;
    public static int mageCount = 0;
    public static int healerCount = 0;
    public static int factoryCount = 0;
    public static int rocketCount = 0;
    // Shared array channels
    public static final long C_ELOC_X = 0;
    public static final long C_ELOC_Y = 1;
    // Others
    public static boolean eLocNotEmpty = false;

    public static void main(String[] args) {
        // Initialization
        // Set research queue
        gc.queueResearch(UnitType.Worker);
        gc.queueResearch(UnitType.Rocket);
        gc.queueResearch(UnitType.Ranger);
        gc.queueResearch(UnitType.Ranger);
        gc.queueResearch(UnitType.Ranger);
        // Code for each round
        while (true) {
            System.out.println("Current round: " + gc.round());
            // Count all units
            runStatistics();
            // For each unit
            VecUnit units = gc.myUnits();
            for (int i = 0; i < units.size(); i++) {
                Unit unit = units.get(i);
                // Code for each bot
                // If unit is off the map, then there's really nothing to do
                if (!unit.location().isOnMap()) continue;
                // Initialize unit info
                int uID = unit.id();
                MapLocation uLoc = unit.location().mapLocation();
                // Unit branch ctrl
                switch (unit.unitType()) {
                    case Worker:
                        runWorker(unit);
                        break;
                    case Knight:
                        runKnight(unit);
                        break;
                    case Ranger:
                        runRanger(unit);
                        break;
                    case Mage:
                        runMage(unit);
                        break;
                    case Healer:
                        runHealer(unit);
                        break;
                    case Factory:
                        runFactory(unit);
                        break;
                    case Rocket:
                        runFactory(unit);
                        break;
                }
            }
            gc.nextTurn();
        }
    }

    // Branch ctrl run methods

    public static void runWorker(Unit unit) {

        // Universal initialization
        int uID = unit.id();
        UnitType uType = unit.unitType();
        MapLocation uLoc = unit.location().mapLocation();

        // Specific initialization
        Direction randDir = randDir(8);
        boolean projectsAround = false;

        // Footprinting
        VecUnit enemies = gc.senseNearbyUnitsByTeam(uLoc, unit.visionRange(), enemy);
        VecUnit friendlyAdjUnits = gc.senseNearbyUnitsByTeam(uLoc, 2L, myTeam);

        // Response mechanism
        // Handling enemies is the priority, if there are some enemies…
        if (enemies.size() > 0) {
            // Nearest enemy initialization
            int eID = nearestUnit(unit, enemies).id();
            MapLocation eLoc = nearestUnit(unit, enemies).location().mapLocation();
            long distanceSquaredToEnemy = uLoc.distanceSquaredTo(eLoc);
            // Escape from the evil hands of enemies!
            runAwayFrom(unit, eLoc);
        } else {
            // First check if there are Ks around
            if (gc.canHarvest(uID, randDir)) {
                gc.harvest(uID, randDir);
            }
            // Since it doesn't conflict with other actions
			/* Pseudo-code:
			if (mines aound) {
				mine
			}
			if (constructions around) {
				construct
			} else {
				mv randly }
			*/
            for (int j = 0; j < friendlyAdjUnits.size(); j++) {
                Unit target = friendlyAdjUnits.get(j);
                if (target.unitType() == UnitType.Factory || target.unitType() == UnitType.Rocket) {
                    if (gc.canBuild(uID, target.id())) {
                        projectsAround = true;
                        gc.build(uID, target.id());
                    }
                }
            }
            if (gc.isMoveReady(uID) && gc.canMove(uID, randDir) && !projectsAround) {
                gc.moveRobot(uID, randDir);
            }
        }

        // First we need sufficient amount of workers to build our econ:
        if (gc.canReplicate(uID, randDir) && workerCount < 8) {
            gc.replicate(uID, randDir);
        }
        // Blueprint factories
        if (gc.canBlueprint(uID, UnitType.Factory, randDir) && factoryCount < 8) {
            gc.blueprint(uID, UnitType.Factory, randDir);
        }
    }

    public static void runKnight(Unit unit) {

        // Universal initialization
        int uID = unit.id();
        UnitType uType = unit.unitType();
        MapLocation uLoc = unit.location().mapLocation();

    }

    public static void runRanger(Unit unit) {

        // Universal initialization
        int uID = unit.id();
        UnitType uType = unit.unitType();
        MapLocation uLoc = unit.location().mapLocation();

        // Specific initialization
        Direction randDir = randDir(8);

        // Footprinting
        VecUnit enemies = gc.senseNearbyUnitsByTeam(uLoc, unit.visionRange(), enemy);

        // Response mechanism
        // Handling enemies is the priority, if there are some enemies…
        if (enemies.size() > 0) {
            // Nearest enemy initialization
            int eID = nearestUnit(unit, enemies).id();
            MapLocation eLoc = nearestUnit(unit, enemies).location().mapLocation();
            reportELoc(eLoc);
            long distanceSquaredToEnemy = uLoc.distanceSquaredTo(eLoc);
            // React differently depends on distance to enemy
            if (distanceSquaredToEnemy < unit.rangerCannotAttackRange()) {
                if (gc.isMoveReady(uID)) {
                    runAwayFrom(unit, eLoc);
                }
            } else if (distanceSquaredToEnemy < unit.attackRange()) {
                if (gc.isAttackReady(uID) && gc.canAttack(uID, eID)) {
                    gc.attack(uID, eID);
                }
            } else {
                if (gc.isMoveReady(uID)) {
                    findPathTo(unit, eLoc);
                }
            }
        } else {
            // If there are sufficient amount of rangers on earth, march for Mars!
            // Developing…
            // if (rangerCount > 4 * workerCount) {
            // 	findPathTo(unit, )
            // } else
            if (eLocNotEmpty) {
                findPathTo(unit, getELoc());
            }
            // If no enemies around, mv randly
            if (gc.isMoveReady(uID) && gc.canMove(uID, randDir)) {
                gc.moveRobot(uID, randDir);
            }
        }
    }

    public static void runMage(Unit unit) {

        // Universal initialization
        int uID = unit.id();
        UnitType uType = unit.unitType();
        MapLocation uLoc = unit.location().mapLocation();

    }

    public static void runHealer(Unit unit) {

        // Universal initialization
        int uID = unit.id();
        UnitType uType = unit.unitType();
        MapLocation uLoc = unit.location().mapLocation();

    }

    public static void runFactory(Unit unit) {

        // Universal initialization
        int uID = unit.id();
        UnitType uType = unit.unitType();
        MapLocation uLoc = unit.location().mapLocation();

        // Start from north, check if factory can unload unit in the given dir.
        for (int j = 0; j < dirs.length; j++) {
            if (gc.canUnload(uID, dirs[j])) {
                gc.unload(uID, dirs[j]);
            }
        }

        // build rangers:
        if (gc.canProduceRobot(uID, UnitType.Ranger)) {
            gc.produceRobot(uID, UnitType.Ranger);
        }

    }

    public static void runRocket(Unit unit) {

        // Universal initialization
        int uID = unit.id();
        UnitType uType = unit.unitType();
        MapLocation uLoc = unit.location().mapLocation();

    }
























    // Helper funcs

    // I am surprised that there are actually no methods which rets the enemy team.
    public static Team enemyOf(Team team) {
        return team == Team.Blue ? Team.Red : Team.Blue;
    }

    // Generate a rand dir based on the num of dirs desired
    // if 8: !include center; if 9: include center
    public static Direction randDir(int n) {
        int rand = (int)(Math.random() * n);
        for (int i = 0; i < n; i++) {
            if (i == rand) {
                return dirs[i];
            }
        }
        return Direction.Center;
    }

    // Simplify the ps of w r array
    public static int read(long channel) {
        return gc.getTeamArray(gc.planet()).get(channel);
    }

    public static void write(long channel, int value) {
        gc.writeTeamArray(channel, value);
    }

    public static void reportELoc(MapLocation eLoc) {
        eLocNotEmpty = true;
        write(C_ELOC_X, eLoc.getX());
        write(C_ELOC_Y, eLoc.getY());
    }

    public static MapLocation getELoc() {
        return new MapLocation(gc.planet(), read(C_ELOC_X), read(C_ELOC_Y));
    }

    // A primative path-finding method.
    public static void findPathTo(Unit unit, MapLocation dest) {
        MapLocation src = unit.location().mapLocation();
        long min = src.distanceSquaredTo(dest);
        Direction optDir = Direction.Center;
        long temp;
        for (int i = 0; i < 8; i++) {
            MapLocation tempLoc = src.add(dirs[i]);
            temp = tempLoc.distanceSquaredTo(dest);
            // Refresh min distance:
            if (temp < min && gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), dirs[i])) {
                min = temp;
                optDir = dirs[i];
            }
        }
        if (gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), optDir)) {
            gc.moveRobot(unit.id(), optDir);
        }
    }

    // The counterpart of findPathTo(), having the exact same architecture.
    public static void runAwayFrom(Unit unit, MapLocation loc) {
        MapLocation uLoc = unit.location().mapLocation();
        long max = uLoc.distanceSquaredTo(loc);
        Direction optDir = Direction.Center;
        long temp;
        for (int i = 0; i < 8; i++) {
            MapLocation tempLoc = uLoc.add(dirs[i]);
            temp = tempLoc.distanceSquaredTo(loc);
            // Refresh min distance:
            if (temp > max && gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), dirs[i])) {
                max = temp;
                optDir = dirs[i];
            }
        }
        if (gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), optDir)) {
            gc.moveRobot(unit.id(), optDir);
        }
    }

    public static Unit nearestUnit(Unit unit, VecUnit units) {
        MapLocation uLoc = unit.location().mapLocation();
        long min = Long.MAX_VALUE;
        // I have to init this, it's required
        Unit nearestUnit = unit;
        for (int i = 0; i < units.size(); i++) {
            MapLocation tLoc = units.get(i).location().mapLocation();
            if (uLoc.distanceSquaredTo(tLoc) < min) {
                min = uLoc.distanceSquaredTo(tLoc);
                nearestUnit = units.get(i);
            }
        }
        return nearestUnit;
    }

    // Self-explanatory methods for statistics.
    public static void clrStatistics() {
        workerCount = 0;
        knightCount = 0;
        rangerCount = 0;
        mageCount = 0;
        healerCount = 0;
        factoryCount = 0;
        rocketCount = 0;
    }

    public static void runStatistics() {
        clrStatistics();
        VecUnit units = gc.myUnits();
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            switch (unit.unitType()) {
                case Worker:
                    workerCount++;
                    break;
                case Knight:
                    knightCount++;
                    break;
                case Ranger:
                    rangerCount++;
                    break;
                case Mage:
                    mageCount++;
                    break;
                case Healer:
                    healerCount++;
                    break;
                case Factory:
                    factoryCount++;
                    break;
                case Rocket:
                    rocketCount++;
                    break;
            }
        }
    }

}
