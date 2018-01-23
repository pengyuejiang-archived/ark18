import bc.*;

public class hf {

	// Import gc
	static GameController gc = Player.gc;

	// Rets a randDir, if n == 8: no center.
	public static Direction randDir(int n) {
        int rand = (int)(Math.random() * n);
        for (int i = 0; i < n; i++) {
            if (i == rand) {
                return f.dirs[i];
            }
        }
        return Direction.Center;
    }

}
