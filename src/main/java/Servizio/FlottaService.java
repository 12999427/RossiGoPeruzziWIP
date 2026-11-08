package Servizio;

import Eccezioni.ErroreDatabaseException;
import Modello.Noleggio;
import Modello.Utente;
import Modello.Veicolo;
import Persistenza.DatabaseManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FlottaService {
    // gestisce veicoli, utenti e noleggi attivi). Deve lavorare con il tipo Modello.Veicolo, mai con Bici
    // o Modello.Monopattino direttamente: è questo il polimorfismo

    private HashMap<String, Veicolo> veicoli;
    private HashMap<String, Utente> utenti;
    private List<Noleggio> noleggi;
    DatabaseManager dbm;

    public FlottaService() throws ErroreDatabaseException {
        dbm = DatabaseManager.getInstance();
        veicoli = dbm.caricaVeicoli();
        utenti = dbm.caricaUtenti();
        noleggi = dbm.caricaNoleggi(utenti, veicoli);
    }

    public List<Noleggio> getNoleggi() {
        return noleggi;
    }

    public HashMap<String, Veicolo> getVeicoli() {
        return veicoli;
    }

    public HashMap<String, Utente> getUtenti() {
        return utenti;
    }

    public void registraVeicolo(Veicolo v)
    {
        if (veicoli.containsKey(v.getCodice())) throw new RuntimeException("codice veicolo già presente");
        veicoli.put(v.getCodice(), v);
        dbm.aggiungiVeicolo(v);
    }

    public void registraUtente(Utente u)
    {
        if (utenti.containsKey(u.getId())) throw new RuntimeException("codice veicolo già presente");
        utenti.put(u.getId(), u);
        dbm.aggiungiUtente(u);
    }

    public void sbloccaVeicolo(String codice, String idUtente)
    {

    }

    public void terminaNoleggio(String codice, int minutiUsati)
    {
        Veicolo v = veicoli.get(codice);
        double cash = 1 + v.calcolaCosto(minutiUsati);
        v.scalaBatteria((int) (v.consumoBatteriaPerMinuto() * minutiUsati));
    }
    //1 € di sblocco + calcolaCosto(minuti) e scala la batteria usando consumoBatteriaPerMinuto().
    // Il costo cambia in base al tipo di veicolo senza nessun if/instanceof.

   public void mettiInRicarica(String codice)
   {

   }

}
