package Eccezioni;

public class UtenteNonTrovatoException extends Exception {
    public UtenteNonTrovatoException()
    {
        super ("l'id utente non esiste");
    }
}
