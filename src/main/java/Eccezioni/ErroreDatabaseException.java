package Eccezioni;

public class ErroreDatabaseException extends Exception {
    public ErroreDatabaseException()
    {
        super("qualsiasi SQLException incontrata durante la lettura o la scrittura dei dati");
    }
}
