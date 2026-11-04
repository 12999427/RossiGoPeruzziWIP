public class VeicoloNonDisponibileException extends Exception {
    public VeicoloNonDisponibileException()
    {
        super("veicolo già in uso o ricarica");
    }
}
