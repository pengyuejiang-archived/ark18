import bc.*;

public class Player {

	public static GameController gc = new GameController();

	public static Direction[] directions = Direction.values();

    public static void main(String[] args) {





		// Initialization






		// Code for each round
        while (true) {
            System.out.println("Current round: " + gc.round());
            VecUnit units = gc.myUnits();
            for (int i = 0; i < units.size(); i++) {
                Unit unit = units.get(i);





				// Code for each bot






            }
            gc.nextTurn();
        }
    }
























	// Helper funcs


}
