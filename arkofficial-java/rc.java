import bc.*;

public class rc {

	// Import gc
	static GameController gc = Player.gc;

	public static void runWorker(Planet planet, Unit unit) {

        // Universal initialization
        int uID = unit.id();
        UnitType uType = unit.unitType();
        MapLocation uLoc = unit.location().mapLocation();

        // Specific initialization
        Direction randDir = hf.randDir(8);
        boolean projectsAround = false;

        // Footprinting
        VecUnit enemies = gc.senseNearbyUnitsByTeam(uLoc, unit.visionRange(), f.ENEMY);
        VecUnit friendlyAdjUnits = gc.senseNearbyUnitsByTeam(uLoc, 2L, f.MY_TEAM);

        // Response mechanism
        // Handling enemies is the priority, if there are some enemies…
        if (enemies.size() > 0) {
            // Nearest enemy initialization
            int eID = hf.nearestUnit(unit, enemies).id();
            MapLocation eLoc = hf.nearestUnit(unit, enemies).location().mapLocation();
            long distanceSquaredToEnemy = uLoc.distanceSquaredTo(eLoc);
			// Report eLoc
			hf.reportELoc(eLoc);
            // Escape from the evil hands of enemies!
            hf.runAwayFrom(unit, eLoc);
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
        if (gc.canReplicate(uID, randDir) && f.workerCount < 8) {
            gc.replicate(uID, randDir);
        }
        // Blueprint factories
        if (gc.canBlueprint(uID, UnitType.Factory, randDir) && f.factoryCount < 8) {
            gc.blueprint(uID, UnitType.Factory, randDir);
        }
    }

    public static void runKnight(Planet planet, Unit unit) {

        // Universal initialization
        int uID = unit.id();
        UnitType uType = unit.unitType();
        MapLocation uLoc = unit.location().mapLocation();

    }

    public static void runRanger(Planet planet, Unit unit) {

        // Universal initialization
        int uID = unit.id();
        UnitType uType = unit.unitType();
        MapLocation uLoc = unit.location().mapLocation();

        // Specific initialization
        Direction randDir = hf.randDir(8);

        // Footprinting
        VecUnit enemies = gc.senseNearbyUnitsByTeam(uLoc, unit.visionRange(), f.ENEMY);

        // Response mechanism
        // Handling enemies is the priority, if there are some enemies…
        if (enemies.size() > 0) {
            // Nearest enemy initialization
            int eID = hf.nearestUnit(unit, enemies).id();
            MapLocation eLoc = hf.nearestUnit(unit, enemies).location().mapLocation();
            long distanceSquaredToEnemy = uLoc.distanceSquaredTo(eLoc);
			// Report eLoc
			hf.reportELoc(eLoc);
            // React differently depends on distance to enemy
            if (distanceSquaredToEnemy < unit.rangerCannotAttackRange()) {
                if (gc.isMoveReady(uID)) {
                    hf.runAwayFrom(unit, eLoc);
                }
            } else if (distanceSquaredToEnemy < unit.attackRange()) {
                if (gc.isAttackReady(uID) && gc.canAttack(uID, eID)) {
                    gc.attack(uID, eID);
                }
            } else {
                if (gc.isMoveReady(uID)) {
                    hf.findPathTo(unit, eLoc);
                }
            }
        } else {
            // If there are sufficient amount of rangers on earth, march for Mars!
            // Developing…
            // if (f.rangerCount > 4 * f.workerCount) {
            // 	findPathTo(unit, )
            // } else
            // If no enemies around, mv randly
			if (hf.eLocInitialized(planet)) {
				hf.findPathTo(unit, hf.getELoc(planet));
			}
            if (gc.isMoveReady(uID) && gc.canMove(uID, randDir)) {
                gc.moveRobot(uID, randDir);
            }
        }
    }

    public static void runMage(Planet planet, Unit unit) {

        // Universal initialization
        int uID = unit.id();
        UnitType uType = unit.unitType();
        MapLocation uLoc = unit.location().mapLocation();

    }

    public static void runHealer(Planet planet, Unit unit) {

        // Universal initialization
        int uID = unit.id();
        UnitType uType = unit.unitType();
        MapLocation uLoc = unit.location().mapLocation();

    }

    public static void runFactory(Planet planet, Unit unit) {

        // Universal initialization
        int uID = unit.id();
        UnitType uType = unit.unitType();
        MapLocation uLoc = unit.location().mapLocation();

        // Start from north, check if factory can unload unit in the given dir.
        for (int j = 0; j < f.dirs.length; j++) {
            if (gc.canUnload(uID, f.dirs[j])) {
                gc.unload(uID, f.dirs[j]);
            }
        }

        // build rangers:
        if (gc.canProduceRobot(uID, UnitType.Ranger)) {
            gc.produceRobot(uID, UnitType.Ranger);
        }

    }

    public static void runRocket(Planet planet, Unit unit) {

        // Universal initialization
        int uID = unit.id();
        UnitType uType = unit.unitType();
        MapLocation uLoc = unit.location().mapLocation();

    }

}
