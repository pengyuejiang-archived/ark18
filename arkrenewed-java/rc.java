import bc.*;

public class rc {

	// Import gc
	static GameController gc = Player.gc;

	public static void runWorker(Planet planet, Unit unit) {

		// Universal intialization
		int uID = unit.id();
		Type uType = unit.unitType();
		MapLocation uLoc = unit.location().mapLocation();

		// Footprinting
		VecUnit enemies = gc.senseNearbyUnitsByTeam(uLoc, unit.visionRange(), f.ENEMY);

		// Response mechanism
		if (enemies.size() > 0) {

		} else {
			
		}

    }

    public static void runKnight(Planet planet, Unit unit) {

    }

    public static void runRanger(Planet planet, Unit unit) {

		// Universal intialization
		int uID = unit.id();
		Type uType = unit.unitType();
		MapLocation uLoc = unit.location().mapLocation();

		// Footprinting
		VecUnit enemies = gc.senseNearbyUnitsByTeam(uLoc, unit.visionRange(), f.ENEMY);

		// Response mechanism
		if (enemies.size() > 0) {

		} else {

		}

    }

    public static void runMage(Planet planet, Unit unit) {

    }

    public static void runHealer(Planet planet, Unit unit) {

		// Universal intialization
		int uID = unit.id();
		Type uType = unit.unitType();
		MapLocation uLoc = unit.location().mapLocation();

		// Footprinting
		VecUnit enemies = gc.senseNearbyUnitsByTeam(uLoc, unit.visionRange(), f.ENEMY);

		// Response mechanism
		if (enemies.size() > 0) {

		} else {

		}

    }

    public static void runFactory(Planet planet, Unit unit) {

		// Universal intialization
		int uID = unit.id();
		Type uType = unit.unitType();
		MapLocation uLoc = unit.location().mapLocation();

    }

    public static void runRocket(Planet planet, Unit unit) {

		// Universal intialization
		int uID = unit.id();
		Type uType = unit.unitType();
		MapLocation uLoc = unit.location().mapLocation();

    }

}
