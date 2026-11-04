package Modello;

public class Monopattino extends Veicolo {
    private boolean limitatoZonaPedonale;

    public Monopattino (String codice, int batteria)
    {
        super(codice, batteria, StatoVeicolo.LIBERO);
        limitatoZonaPedonale = true;
    }

    public Monopattino (String codice, int batteria, StatoVeicolo s, boolean limitatopedon)
    {
        super(codice, batteria, s);
        limitatoZonaPedonale = limitatopedon;
    }

    @Override
    public double calcolaCosto(int minuti) {
        return 0.25;
    }

    @Override
    public double consumoBatteriaPerMinuto() {
        return 1;
    }

    public boolean isLimitatoInZonaPedonale()
    {
        return limitatoZonaPedonale;
    }
}
