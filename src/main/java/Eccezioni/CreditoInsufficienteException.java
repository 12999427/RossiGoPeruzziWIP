package Eccezioni;

public class CreditoInsufficienteException extends RossiGoException {
    public CreditoInsufficienteException ()
    {
        super("credito sotto la soglia di sblocco");
    }
}
