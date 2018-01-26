import bc.*;
import java.util.LinkedList;
import java.util.Queue;

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

	// ELoc
	public static void reportELoc(MapLocation eLoc) {
		if (gc.planet() == Planet.Earth) {
			f.eLocEarthActivated = true;
			f.eLocEarth = eLoc;
		} else {
			f.eLocMarsActivated = true;
			f.eLocMars = eLoc;
		}
	}

	public static MapLocation getELoc(Planet planet) {
		return planet == Planet.Earth ? f.eLocEarth : f.eLocMars;
	}

	public static boolean eLocActivated(Planet planet) {
		return planet == Planet.Earth ? f.eLocEarthActivated : f.eLocMarsActivated;
	}

	public static void deactivateELoc(Planet planet) {
		if (planet == Planet.Earth) {
			f.eLocEarthActivated = false;
		} else {
			f.eLocMarsActivated = false;
		}
	}

	//Get Optimal Launching time
    public static long getNextLaunchTime(){
        long period = gc.orbitPattern().getPeriod();
        long duration = gc.orbitPattern().duration(gc.round());
        long launchRound = gc.round();
        for (long i = 0; i < 750 / period; i++) {
            if (gc.round() < (3 / 4 + i) * period) {
                for (long j = gc.round(); j < (3 / 4 + i) * period / 2; j++) {
                    long tempDur = gc.orbitPattern().duration(j) + (j - gc.round());
                    if (tempDur < duration) {
                        duration = tempDur;
                        launchRound = j;
                    }
                }
            }
        }
        return launchRound;
    }

    public static Direction bfs(MapLocation start, MapLocation end){
        int xsize = (int)gc.startingMap(gc.planet()).getWidth();
        int ysize = (int)gc.startingMap(gc.planet()).getHeight();
        int visit[][] = new int[xsize][ysize];
        int stepArr[][] = new int[][]{{-1,0},{1,0},{0,-1},{0,1},{1,1},{1,-1},{-1,1},{-1,-1}};
        Queue<MapLocation> queue = new LinkedList<MapLocation>();
        queue.add(start);
        while (!queue.isEmpty()){
            MapLocation newLoc = queue.poll();
            visit[newLoc.getX()][newLoc.getY()] = 1;
            for(int i = 0; i < 8; i++){
                int x = newLoc.getX() + stepArr[i][0];
                int y = newLoc.getY() + stepArr[i][1];
                if(x == end.getX() && y == end.getX()){
                    return start.directionTo(queue.element());
                }
                MapLocation tryloc = new MapLocation(gc.planet(), x, y);
                if(x >= 0 && y >= 0 && x < xsize && y < ysize && visit[x][y] == 0 && gc.startingMap(gc.planet()).isPassableTerrainAt(tryloc) == 1){
                    MapLocation next = new MapLocation(gc.planet(),x,y);
                    queue.add(next);
                }
            }
        }
        return Direction.Center;
    }

	public static Unit getWeakestUnit(VecUnit units) {
		long minHP = Long.MAX_VALUE;
		Unit weakestUnit = units.get(0);
		for (int i = 0; i < units.size(); i++) {
			Unit target = units.get(i);
			if (target.health() < minHP && target.health() != target.maxHealth()) {
				minHP = target.health();
				weakestUnit = target;
			}
		}
		return weakestUnit;
	}

}
