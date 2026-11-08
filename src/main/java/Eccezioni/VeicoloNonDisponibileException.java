package Eccezioni;

public class VeicoloNonDisponibileException extends RossiGoException {
    public VeicoloNonDisponibileException() {
        super("Veicolo non disponibile (già in uso o in ricarica)");
    }

    public VeicoloNonDisponibileException(String message) {
        super(message);
    }

    public VeicoloNonDisponibileException(String message, Throwable cause) {
        super(message, cause);
    }
}

