package Eccezioni;

public class VeicoloNonDisponibileException extends RossiGoException {
    public VeicoloNonDisponibileException()
    {
        super("veicolo già in uso o ricarica");
    }
}
