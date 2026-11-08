package Eccezioni;

public class CreditoInsufficienteException extends RossiGoException {
    private double creditoAttuale;

    public CreditoInsufficienteException() {
        super("Credito insufficiente per l'operazione richiesta");
    }

    public CreditoInsufficienteException(double creditoAttuale) {
        super(String.format("Credito insufficiente: %.2f € (soglia minima di sblocco: 1.00 €)", creditoAttuale));
        this.creditoAttuale = creditoAttuale;
    }

    public CreditoInsufficienteException(String message) {
        super(message);
    }

    public CreditoInsufficienteException(String message, Throwable cause) {
        super(message, cause);
    }

    public double getCreditoAttuale() {
        return creditoAttuale;
    }
}

