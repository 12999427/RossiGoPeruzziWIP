package Eccezioni;

class BattieraScaricaException extends Exception {
    private int batteriaAttuale;
    public BattieraScaricaException(int batteriaAttuale)
    {
        super("batteria sotto 15%");
        this.batteriaAttuale = batteriaAttuale;
    }

    public int getBatteriaAttuale()
    {
        return batteriaAttuale;
    }
}
