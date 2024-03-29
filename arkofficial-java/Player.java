import bc.*;

public class Player {

	// Import support resources
    public static GameController gc = new GameController();

    public static void main(String[] args) {
		try {

			// Research queue:
			gc.queueResearch(UnitType.Worker);
	        gc.queueResearch(UnitType.Rocket);
			gc.queueResearch(UnitType.Rocket);
			gc.queueResearch(UnitType.Ranger);
			gc.queueResearch(UnitType.Rocket);
			gc.queueResearch(UnitType.Ranger);
			gc.queueResearch(UnitType.Ranger);

			Planet planet = gc.planet();
	        while (true) {
	            System.out.println("Current round: " + gc.round());
				System.out.println("Time left: " + gc.getTimeLeftMs());
	            VecUnit units = gc.myUnits();
				hf.runStatistics();
				f.unitCount = (int)units.size();
	            for (int i = 0; i < units.size(); i++) {
	                Unit unit = units.get(i);
					if (!unit.location().isOnMap()) continue;
	                switch (unit.unitType()) {
	                    case Worker:
	                        rc.runWorker(planet, unit);
	                        break;
	                    case Knight:
	                        rc.runKnight(planet, unit);
	                        break;
	                    case Ranger:
	                        rc.runRanger(planet, unit);
	                        break;
	                    case Mage:
	                        rc.runMage(planet, unit);
	                        break;
	                    case Healer:
	                        rc.runHealer(planet, unit);
	                        break;
	                    case Factory:
	                        rc.runFactory(unit);
	                        break;
	                    case Rocket:
	                        rc.runRocket(planet, unit);
	                        break;
	                }
	            }
				System.gc();
	            gc.nextTurn();
	        }

		} catch (Exception e) {
			System.out.println("I've got an exception!");
			e.printStackTrace();
		}
    }

}
