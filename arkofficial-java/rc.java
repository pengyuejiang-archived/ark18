import bc.*;

public class rc {

	// Import gc
	static GameController gc = Player.gc;

	public static void runWorker(Planet planet, Unit unit) {

        // Universal initialization
        int uID = unit.id();
        UnitType uType = unit.unitType();
        MapLocation uLoc = unit.location().mapLocation();

		// See if there are still enemies
		if (uLoc.equals(f.eLoc)) {
			f.eLocActivated = false;
			if (gc.round() > 400) f.enemyEliminated = true;
		}

        // Specific initialization
        Direction randDir = hf.randDir(8);
		boolean movementGranted = true;

        // Footprinting
        VecUnit enemies = gc.senseNearbyUnitsByTeam(uLoc, unit.visionRange(), f.ENEMY);
        VecUnit friendlyAdjUnits = gc.senseNearbyUnitsByTeam(uLoc, 2L, f.MY_TEAM);

		if (planet == Planet.Earth && (f.enemyEliminated || gc.round() > 650)) {
			// If the great flood is near or there's no enemies on the planet, the only thing you do is produce rockets.
			if (gc.canBlueprint(uID, UnitType.Rocket, randDir) && f.rocketCount < f.rocketBaseIndex) {
				gc.blueprint(uID, UnitType.Rocket, randDir);
				f.rocketBaseIndex++;
			}
			return;
		}

        // Response mechanism
        // Handling enemies is the priority, if there are some enemies…
        if (enemies.size() > 0) {
            // Nearest enemy initialization
            int eID = hf.nearestUnit(unit, enemies).id();
            MapLocation eLoc = hf.nearestUnit(unit, enemies).location().mapLocation();
            long distanceSquaredToEnemy = uLoc.distanceSquaredTo(eLoc);
            // Report eLoc
			f.eLocActivated = true;
			f.eLoc = eLoc;
            // Escape from the evil hands of enemies!
            hf.runAwayFrom(unit, eLoc);
        } else {
			/*
			 * Priority list:
			 * 1. Replicate
			 * 2. Build
			 * 3. Blueprint Factory
			 * 4. Blueprint Rocket
			 * 5. Harvest
			 * 6. Mv Randly
			 */
			for (int j = 0; j < friendlyAdjUnits.size(); j++) {
                Unit target = friendlyAdjUnits.get(j);
                if (target.unitType() == UnitType.Factory || target.unitType() == UnitType.Rocket) {
                    if (gc.canBuild(uID, target.id())) {
						movementGranted = false;
                        gc.build(uID, target.id());
                    }
                }
            }
			if (gc.canReplicate(uID, randDir) && f.workerCount < f.workerBaseIndex) {
				gc.replicate(uID, randDir);
			}
			// Since structures can only be built on earth…
			if (planet == Planet.Earth) {
				if (gc.canBlueprint(uID, UnitType.Factory, randDir) && f.factoryCount < f.workerBaseIndex) {
					gc.blueprint(uID, UnitType.Factory, randDir);
				}
				if (gc.canBlueprint(uID, UnitType.Rocket, randDir) && f.rocketCount < f.rocketBaseIndex && f.factoryCount >= f.workerBaseIndex) {
					gc.blueprint(uID, UnitType.Rocket, randDir);
				}
			}
			// Adjacent mining
			for (int i = 0; i < f.dirs.length; i++) {
				if (gc.canHarvest(uID, f.dirs[i])) {
					movementGranted = false;
					gc.harvest(uID, f.dirs[i]);
					break;
				}
			}
			// Worker can only mv if they are not building something!
			if (movementGranted && gc.isMoveReady(uID)) {
				if (f.assemblyLocActivated) {
					hf.findPathTo(unit, f.assemblyLoc);
				} else if (gc.canMove(uID, randDir)) {
	                gc.moveRobot(uID, randDir);
	            }
			}
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

		// See if there are still enemies
		if (uLoc.equals(f.eLoc)) {
			f.eLocActivated = false;
			if (gc.round() > 400) f.enemyEliminated = true;
		}

        // Specific initialization
        Direction randDir = hf.randDir(8);

        // Footprinting
        VecUnit enemies = gc.senseNearbyUnitsByTeam(uLoc, unit.visionRange(), f.ENEMY);

		// If no enemy, all mv to Mars.
		// if (planet == Planet.Earth && (f.enemyEliminated || gc.round() > 650)) {
		// 	if (f.assemblyLocActivated) {
		// 		hf.findPathTo(unit, f.assemblyLoc);
		// 	}
		// 	return;
		// }

        // Response mechanism
        // Handling enemies is the priority, if there are some enemies…
        if (enemies.size() > 0) {
            // Nearest enemy initialization
            int eID = hf.nearestUnit(unit, enemies).id();
            MapLocation eLoc = hf.nearestUnit(unit, enemies).location().mapLocation();
            long distanceSquaredToEnemy = uLoc.distanceSquaredTo(eLoc);
			// Report eLoc
			f.eLocActivated = true;
			f.eLoc = eLoc;
            // Rangers react differently depends on distance to enemy
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
			if (f.eLocActivated && hf.alliesAround(unit, f.allyScope)) {
				hf.findPathTo(unit, f.eLoc);
			}
			if (f.assemblyLocActivated) {
				hf.findPathTo(unit, f.assemblyLoc);
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

		// See if there are still enemies
		if (uLoc.equals(f.eLoc)) {
			f.eLocActivated = false;
			if (gc.round() > 400) f.enemyEliminated = true;
		}

		// Specific initialization
        Direction randDir = hf.randDir(8);

        // Footprinting
        VecUnit enemies = gc.senseNearbyUnitsByTeam(uLoc, unit.visionRange(), f.ENEMY);
		VecUnit allies = gc.senseNearbyUnitsByTeam(uLoc, unit.attackRange(), f.MY_TEAM);

		// If no enemy, all mv to Mars.
		// if (planet == Planet.Earth && (f.enemyEliminated || gc.round() > 650)) {
		// 	if (f.assemblyLocActivated) {
		// 		hf.findPathTo(unit, f.assemblyLoc);
		// 	}
		// 	return;
		// }

        // Response mechanism
        // Handling enemies is the priority, if there are some enemies…
        if (enemies.size() > 0) {
            // Nearest enemy initialization
            int eID = hf.nearestUnit(unit, enemies).id();
            MapLocation eLoc = hf.nearestUnit(unit, enemies).location().mapLocation();
            long distanceSquaredToEnemy = uLoc.distanceSquaredTo(eLoc);
			// Report eLoc
			f.eLocActivated = true;
			f.eLoc = eLoc;
		} else {
			if (f.eLocActivated && hf.alliesAround(unit, f.allyScope)) {
				hf.findPathTo(unit, f.eLoc);
			}
			if (f.assemblyLocActivated) {
				hf.findPathTo(unit, f.assemblyLoc);
			}
            if (gc.isMoveReady(uID) && gc.canMove(uID, randDir)) {
                gc.moveRobot(uID, randDir);
            }
        }
		// No matter if there are enemies around, healers heal ppl
		Unit weakestAlly = hf.getWeakestUnit(allies);
		if (gc.isHealReady(uID) && gc.canHeal(uID, weakestAlly.id())) {
			gc.heal(uID, weakestAlly.id());
		}
    }

    public static void runFactory(Unit unit) {

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

		// Backup production of workers in case they all died.
		if (gc.canProduceRobot(uID, UnitType.Worker) && f.workerCount == 0) {
			gc.produceRobot(uID, UnitType.Worker);
		}

		// If no enemy, stop all production activities
		if (gc.round() > 400 && !f.eLocActivated) return;

		// build rangers:
		if (gc.canProduceRobot(uID, UnitType.Ranger) && !(f.rangerCount > f.rangerWorkerRatio * f.workerCount && f.rocketCount < f.rocketBaseIndex)) {
			gc.produceRobot(uID, UnitType.Ranger);
		}

		// build healers:
		if (gc.canProduceRobot(uID, UnitType.Healer) && f.healerCount < f.rangerCount / f.rangerHealerRatio) {
			gc.produceRobot(uID, UnitType.Healer);
		}

    }

    public static void runRocket(Planet planet, Unit unit) {

        // Universal initialization
        int uID = unit.id();
        UnitType uType = unit.unitType();
        MapLocation uLoc = unit.location().mapLocation();

		// Differentiate ops on Earth and Mars.
		if (planet == Planet.Earth) {
			// Specialized initialization
			// This must be contained within the "Earth" section, otherwise they will just stick to the rockets on Mars.
			if (unit.structureIsBuilt() == 1 && !f.assemblyLocActivated) {
				f.assemblyLoc = uLoc;
				f.assemblyLocActivated = true;
			}
			VecUnit colonists = gc.senseNearbyUnitsByTeam(uLoc, 2, f.MY_TEAM);
	        VecUnitID colonistLoadedIDs = unit.structureGarrison();
			if (colonistLoadedIDs.size() < unit.structureMaxCapacity()) {
				for (int i = 0; i < colonists.size(); i++) {
					Unit colonist = colonists.get(i);
					if (gc.canLoad(uID, colonist.id())) {
						gc.load(uID, colonist.id());
						break;
					}
				}
			} else {
				while (true) {
					int targetX = (int)(Math.random() * gc.startingMap(Planet.Mars).getWidth());
		            int targetY = (int)(Math.random() * gc.startingMap(Planet.Mars).getHeight());
		            MapLocation landingLoc = new MapLocation(Planet.Mars, targetX, targetY);
		            if (gc.canLaunchRocket(uID, landingLoc) && gc.startingMap(Planet.Mars).isPassableTerrainAt(landingLoc) == 1) {
		                gc.launchRocket(uID, landingLoc);
						f.assemblyLocActivated = false;
						// Every time it goes to Mars, expand population.
						f.workerBaseIndex++;
		                break;
		            }
		        }
			}
		} else {
			for (int i = 0; i < f.dirs.length; i++) {
				if (gc.canUnload(uID, f.dirs[i])) {
					gc.unload(uID, f.dirs[i]);
					break;
				}
			}
		}

    }

}
