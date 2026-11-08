package Eccezioni;

public class CodiceFiscaleNonValidoException extends RossiGoException {
    private String codiceFiscale;

    public CodiceFiscaleNonValidoException() {
        super("Il codice fiscale fornito in registrazione non è valido");
    }

    public CodiceFiscaleNonValidoException(String codiceFiscale) {
        super("Il codice fiscale fornito in registrazione non è valido: " + codiceFiscale);
        this.codiceFiscale = codiceFiscale;
    }

    public CodiceFiscaleNonValidoException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }
}

