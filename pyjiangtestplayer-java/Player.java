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

    public static void main(String[] args) {
		// Initialization
		// Set research queue:
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

		// Footprinting
		VecUnit friendlyAdjUnits = gc.senseNearbyUnitsByTeam(uLoc, 2L, myTeam);

		// Op branch ctrl
		for (int j = 0; j < friendlyAdjUnits.size(); j++) {
			Unit target = friendlyAdjUnits.get(j);
			switch (target.unitType()) {
				case Factory:
					if (gc.canBuild(uID, target.id())) {
						gc.build(uID, target.id());
					}
					break;
				case Rocket:
					if (gc.canBuild(uID, target.id())) {
						gc.build(uID, target.id());
					}
					break;
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
		if (gc.canHarvest(uID, randDir)) {
			gc.harvest(uID, randDir);
		}
		// If nothing to harvest, just mv randly
		else if (gc.isMoveReady(uID) && gc.canMove(uID, randDir)) {
			gc.moveRobot(uID, randDir);
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
			int eID = enemies.get(0).id();
			MapLocation eLoc = enemies.get(0).location().mapLocation();
			long distanceSquaredToEnemy = uLoc.distanceSquaredTo(eLoc);
			// React differently depends on distance to enemy
			if (distanceSquaredToEnemy < unit.rangerCannotAttackRange()) {
				// if (gc.isMoveReady(uID) && gc.canMove(uID, bc.bcDirectionOpposite())) {
                //
				// }
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
		// This is just a placeholder, I must return something for definite
		return Direction.Center;
	}

	// A primative path-finding method
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
