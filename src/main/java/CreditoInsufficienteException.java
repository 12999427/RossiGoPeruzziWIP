public class CreditoInsufficienteException extends Exception {
    public CreditoInsufficienteException ()
    {
        super("credito sotto la soglia di sblocco");
    }
}
