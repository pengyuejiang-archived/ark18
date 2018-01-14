import bc.*;

public class Player {

	public static GameController gc = new GameController();

	public static Direction[] directions = Direction.values();

    public static void main(String[] args) {





		// Initialization
		// Set research queue:
		gc.queueResearch(UnitType.Worker);
		gc.queueResearch(UnitType.Rocket);





		// Code for each round
        while (true) {
            System.out.println("Current round: " + gc.round());
            VecUnit units = gc.myUnits();
            for (int i = 0; i < units.size(); i++) {
                Unit unit = units.get(i);





				// Code for each bot
				// If unit is off the map, then there's really nothing to do
				if (!unit.location().isOnMap()) continue;
				// Unit branch ctrl
				switch (unit.unitType()) {
					case Worker:





						Direction randDir = randDir(8);
						// Replicate is good:
						if (gc.canReplicate(unit.id(), randDir)) {
							gc.replicate(unit.id(), randDir);
						}
						// We love harvest:
						if (gc.canHarvest(unit.id(), randDir)) {
							gc.harvest(unit.id(), randDir);
						}
						// If nothing to harvest, just go:
						else if (gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), randDir)) {
							gc.moveRobot(unit.id(), randDir);
						}





						break;
					case Knight:
						break;
					case Ranger:
						break;
					case Mage:
						break;
					case Healer:
						break;
					case Factory:
						break;
					case Rocket:
						break;
				}





            }
            gc.nextTurn();
        }
    }
























	// Helper funcs
	// Generate a rand dir based on the num of dirs desired
	// if 8: !include center
	// if 9: include center
	public static Direction randDir(int n) {
		int rand = (int)(Math.random() * n);
		for (int i = 0; i < n; i++) {
			if (i == rand) {
				return directions[i];
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
			MapLocation tempLoc = src.add(directions[i]);
			temp = tempLoc.distanceSquaredTo(dest);
			// Refresh min distance:
			if (temp < min && gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), directions[i])) {
				min = temp;
				optDir = directions[i];
			}
		}
		if (gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), optDir)) {
			gc.moveRobot(unit.id(), optDir);
		}
	}

}
