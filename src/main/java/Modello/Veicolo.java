package Modello;

public abstract class Veicolo {
    private String codice;
    private int batteria;
    private StatoVeicolo stato;

    protected Veicolo(String codice, int batteria, StatoVeicolo stato) {
        this.stato = stato;
        this.batteria = batteria;
        this.codice = codice;
    }

    public abstract double calcolaCosto (int minuti);
    public abstract double consumoBatteriaPerMinuto();

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        if (codice.isBlank()) throw new RuntimeException("codice vuoto");
        this.codice = codice;
    }

    public int getBatteria() {
        return batteria;
    }

    public void setBatteria(int batteria) {
        if (batteria < 0) throw new RuntimeException("batteria veicolo < 0");
        this.batteria = batteria;
    }

    public void scalaBatteria(int batteria)
    {
        setBatteria(getBatteria() - batteria);
    }

    public StatoVeicolo getStato() {
        return stato;
    }

    public void setStato(StatoVeicolo stato) {
        this.stato = stato;
    }
}

