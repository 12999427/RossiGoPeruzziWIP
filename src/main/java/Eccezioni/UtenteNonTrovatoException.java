package Eccezioni;

public class UtenteNonTrovatoException extends RossiGoException {
    public UtenteNonTrovatoException() {
        super("L'id utente non esiste");
    }

    public UtenteNonTrovatoException(String message) {
        super(message);
    }

    public UtenteNonTrovatoException(String message, Throwable cause) {
        super(message, cause);
    }
}

