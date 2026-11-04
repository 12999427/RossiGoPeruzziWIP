package Servizio;

import Modello.Utente;
import Modello.Veicolo;

import java.util.HashMap;

public class FlottaService {
    // gestisce veicoli, utenti e noleggi attivi). Deve lavorare con il tipo Modello.Veicolo, mai con Bici
    // o Modello.Monopattino direttamente: è questo il polimorfismo

    private static HashMap<String, Veicolo> veicoli;
    private static HashMap<String, Utente> utenti;

    public FlottaService()
    {

    }

    public static void registraVeicolo(Veicolo v)
    {
        if (veicoli.containsKey(v.getCodice())) throw new RuntimeException("codice veicolo già presente");
        veicoli.put(v.getCodice(), v);
    }

    public static void registraUtente(Utente u)
    {
        if (utenti.containsKey(u.getId())) throw new RuntimeException("codice veicolo già presente");
        utenti.put(u.getId(), u);
    }

    public static void sbloccaVeicolo(String codice, String idUtente)
    {

    }

    public static void terminaNoleggio(String codice, int minutiUsati)
    {
        Veicolo v = veicoli.get(codice);
        double cash = 1 + v.calcolaCosto(minutiUsati);
        v.scalaBatteria((int) (v.consumoBatteriaPerMinuto() * minutiUsati));
    }
    //1 € di sblocco + calcolaCosto(minuti) e scala la batteria usando consumoBatteriaPerMinuto().
    // Il costo cambia in base al tipo di veicolo senza nessun if/instanceof.

   public static void mettiInRicarica(String codice)
   {

   }

}
