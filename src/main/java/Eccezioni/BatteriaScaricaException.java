package Eccezioni;

public class BatteriaScaricaException extends RossiGoException {
    private int batteriaAttuale;

    public BatteriaScaricaException(int batteriaAttuale) {
        super("Batteria scarica sotto il 15% (livello attuale: " + batteriaAttuale + "%)");
        this.batteriaAttuale = batteriaAttuale;
    }

    public BatteriaScaricaException(String message) {
        super(message);
    }

    public BatteriaScaricaException(String message, Throwable cause) {
        super(message, cause);
    }

    public BatteriaScaricaException(String message, int batteriaAttuale) {
        super(message);
        this.batteriaAttuale = batteriaAttuale;
    }

    public int getBatteriaAttuale() {
        return batteriaAttuale;
    }
}

