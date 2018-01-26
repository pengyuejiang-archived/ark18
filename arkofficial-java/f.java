import bc.*;

public class f {

	// Import gc
	static GameController gc = Player.gc;

	// Teams
	public static final Team MY_TEAM = gc.team();
	public static final Team ENEMY = MY_TEAM == Team.Blue ? Team.Red : Team.Blue;

	// Directions
	public static Direction[] dirs = Direction.values();

	// Statistics
	public static int workerCount = 0;
    public static int knightCount = 0;
    public static int rangerCount = 0;
    public static int mageCount = 0;
    public static int healerCount = 0;
    public static int factoryCount = 0;
    public static int rocketCount = 0;
	public static int unitCount = 0;

	// Population ctrl
	// base index: the basic scale of everything else
	public static int workerBaseIndex = 8;
	public static int rocketBaseIndex = 1;
	// ratio: the ratio between one and another unit
	public static int rangerWorkerRatio = 4;
	public static int rangerHealerRatio = 4;

	// Earth
	public static MapLocation eLocEarth = new MapLocation(Planet.Earth, 0, 0);
	public static boolean eLocEarthActivated = false;

	// Mars
	public static MapLocation eLocMars = new MapLocation(Planet.Mars, 0, 0);
	public static boolean eLocMarsActivated = false;

	// Rocket
	public static MapLocation assemblyLoc = new MapLocation(Planet.Earth, 0, 0);
	public static boolean assemblyLocActivated = false;

	public static boolean enemyEliminated = false;

}
