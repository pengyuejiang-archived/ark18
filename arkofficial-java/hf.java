import bc.*;

public class hf {

	// Import gc
	static GameController gc = Player.gc;

	// Generate a rand dir based on the num of dirs desired
    // if 8: !include center; if 9: include center
    public static Direction randDir(int n) {
		return f.dirs[(int)(Math.random() * n)];
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

	// A primative path-finding method.
    public static void findPathTo(Unit unit, MapLocation dest) {
        MapLocation src = unit.location().mapLocation();
        long min = src.distanceSquaredTo(dest);
        Direction optDir = Direction.Center;
        long temp;
        for (int i = 0; i < 8; i++) {
            MapLocation tempLoc = src.add(f.dirs[i]);
            temp = tempLoc.distanceSquaredTo(dest);
            // Refresh min distance:
            if (temp < min && gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), f.dirs[i])) {
                min = temp;
                optDir = f.dirs[i];
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
            MapLocation tempLoc = uLoc.add(f.dirs[i]);
            temp = tempLoc.distanceSquaredTo(loc);
            // Refresh min distance:
            if (temp > max && gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), f.dirs[i])) {
                max = temp;
                optDir = f.dirs[i];
            }
        }
        if (gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), optDir)) {
            gc.moveRobot(unit.id(), optDir);
        }
    }

    // Self-explanatory methods for statistics.
    public static void runStatistics() {
        clrStatistics();
        VecUnit units = gc.myUnits();
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            switch (unit.unitType()) {
                case Worker:
                    f.workerCount++;
                    break;
                case Knight:
                    f.knightCount++;
                    break;
                case Ranger:
                    f.rangerCount++;
                    break;
                case Mage:
                    f.mageCount++;
                    break;
                case Healer:
                    f.healerCount++;
                    break;
                case Factory:
                    f.factoryCount++;
                    break;
                case Rocket:
                    f.rocketCount++;
                    break;
            }
        }
    }

	private static void clrStatistics() {
		f.workerCount = f.knightCount = f.rangerCount = f.mageCount = f.healerCount = f.factoryCount = f.rocketCount = f.unitCount = 0;
	}

	public static void reportELoc(MapLocation eLoc) {
		if (gc.planet() == Planet.Earth) {
			f.eLocEarthInitialized = true;
			f.eLocEarth = eLoc;
		} else {
			f.eLocMarsInitialized = true;
			f.eLocMars = eLoc;
		}
	}

	public static MapLocation getELoc(Planet planet) {
		return planet == Planet.Earth ? f.eLocEarth : f.eLocMars;
	}

	public static boolean eLocInitialized(Planet planet) {
		return planet == Planet.Earth ? f.eLocEarthInitialized : f.eLocMarsInitialized;
	}

}
