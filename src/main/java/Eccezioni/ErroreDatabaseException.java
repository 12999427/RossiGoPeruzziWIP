package Eccezioni;

public class ErroreDatabaseException extends RossiGoException {
    public ErroreDatabaseException()
    {
        super("qualsiasi SQLException incontrata durante la lettura o la scrittura dei dati");
    }

    public ErroreDatabaseException(String msg)
    {
        super(msg);
    }
}
