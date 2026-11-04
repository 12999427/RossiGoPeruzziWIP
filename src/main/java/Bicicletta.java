public class Bicicletta extends Veicolo {
    private int numMarce;

    public Bicicletta (String codice, int batteria)
    {
        super(codice, batteria, StatoVeicolo.LIBERO);
        numMarce = 4;
    }
    public Bicicletta (String codice, int batteria, StatoVeicolo s, int numMarce)
    {
        super(codice, batteria, s);
        this.numMarce = numMarce;
    }

    @Override
    public double calcolaCosto(int minuti) {
        return minuti * 0.15;
    }

    @Override
    public double consumoBatteriaPerMinuto() {
        return 0.3;
    }

    public int getNumMarce() {
        return numMarce;
    }

}
