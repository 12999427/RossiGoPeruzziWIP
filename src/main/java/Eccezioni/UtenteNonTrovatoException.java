package Eccezioni;

public class UtenteNonTrovatoException extends RossiGoException {
    public UtenteNonTrovatoException()
    {
        super ("l'id utente non esiste");
    }
}
