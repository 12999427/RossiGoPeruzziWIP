package Eccezioni;

public class CodiceFiscaleNonValidoException extends RossiGoException {
    public CodiceFiscaleNonValidoException ()
    {
        super("il codice fiscale fornito in registrazione non è valido");
    }
}
