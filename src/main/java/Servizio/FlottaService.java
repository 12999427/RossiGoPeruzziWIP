package Servizio;

import Eccezioni.*;
import Modello.*;
import Persistenza.DatabaseManager;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FlottaService {
    // Gestisce veicoli, utenti e noleggi attivi. Lavora con il tipo Modello.Veicolo, mai con Bicicletta
    // o Monopattino direttamente: è questo il polimorfismo.

    private HashMap<String, Veicolo> veicoli;
    private HashMap<String, Utente> utenti;
    private List<Noleggio> noleggi;
    private final DatabaseManager dbm;

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

    public void registraVeicolo(Veicolo v) throws ErroreDatabaseException {
        if (v == null) {
            throw new IllegalArgumentException("Veicolo non valido");
        }
        if (veicoli.containsKey(v.getCodice())) {
            throw new IllegalArgumentException("Codice veicolo già presente: " + v.getCodice());
        }
        veicoli.put(v.getCodice(), v);
        dbm.aggiungiVeicolo(v);
    }

    public void registraUtente(Utente u) throws CodiceFiscaleNonValidoException, ErroreDatabaseException {
        if (u == null) {
            throw new IllegalArgumentException("Utente non valido");
        }
        if (utenti.containsKey(u.getId())) {
            throw new IllegalArgumentException("Utente con ID già presente: " + u.getId());
        }

        // Validazione del Codice Fiscale
        if (!isCodiceFiscaleValido(u.getCodiceFiscale())) {
            throw new CodiceFiscaleNonValidoException(u.getCodiceFiscale());
        }

        utenti.put(u.getId(), u);
        dbm.aggiungiUtente(u);
    }

    private boolean isCodiceFiscaleValido(String cf) {
        if (cf == null) return false;
        // Controllo standard del formato del codice fiscale italiano (16 caratteri alfanumerici)
        String regex = "^[A-Za-z]{6}[0-9]{2}[A-EHLMPR-Ta-ehlpmr-t][0-9]{2}[A-Za-z][0-9]{3}[A-Za-z]$";
        return cf.matches(regex);
    }

    public void sbloccaVeicolo(String codice, String idUtente) throws RossiGoException {
        Utente u = utenti.get(idUtente);
        if (u == null) {
            throw new UtenteNonTrovatoException("Utente con ID '" + idUtente + "' non trovato");
        }

        if (u.getCredito() < 1.0) {
            throw new CreditoInsufficienteException(u.getCredito());
        }

        Veicolo v = veicoli.get(codice);
        if (v == null) {
            throw new VeicoloNonDisponibileException("Veicolo '" + codice + "' inesistente");
        }

        if (v.getStato() != StatoVeicolo.LIBERO) {
            throw new VeicoloNonDisponibileException("Veicolo '" + codice + "' non disponibile (attualmente in stato: " + v.getStato() + ")");
        }

        if (v.getBatteria() < 15) {
            throw new BatteriaScaricaException(v.getBatteria());
        }

        // Sblocco del veicolo
        v.setStato(StatoVeicolo.IN_USO);
        LocalTime oraInizio = LocalTime.now();
        Noleggio nuovoNoleggio = new Noleggio(u, v, oraInizio);
        noleggi.add(nuovoNoleggio);

        // Aggiornamento persistenza su SQLite
        dbm.aggiornaStatoVeicolo(codice, StatoVeicolo.IN_USO, v.getBatteria());
        dbm.inserisciInizioNoleggio(idUtente, codice);
    }

    public double terminaNoleggio(String codice, int minutiUsati) throws RossiGoException {
        Veicolo v = veicoli.get(codice);
        if (v == null) {
            throw new VeicoloNonDisponibileException("Veicolo '" + codice + "' non trovato");
        }
        if (v.getStato() != StatoVeicolo.IN_USO) {
            throw new VeicoloNonDisponibileException("Il veicolo '" + codice + "' non è attualmente in uso");
        }

        // Ricerca del noleggio attivo per questo veicolo
        Noleggio noleggioAttivo = null;
        for (int i = noleggi.size() - 1; i >= 0; i--) {
            Noleggio n = noleggi.get(i);
            if (n.getVeicolo() != null && n.getVeicolo().getCodice().equals(codice) && n.getOrarioFine() == null) {
                noleggioAttivo = n;
                break;
            }
        }

        // Calcolo polimorfico del costo e del consumo senza nessun if/instanceof
        double costoCorsa = 1.0 + v.calcolaCosto(minutiUsati);
        int consumo = (int) Math.round(v.consumoBatteriaPerMinuto() * minutiUsati);
        v.scalaBatteria(consumo);
        v.setStato(StatoVeicolo.LIBERO);

        LocalTime oraFine = LocalTime.now();
        if (noleggioAttivo != null) {
            noleggioAttivo.setOrarioFine(oraFine);
            Utente u = noleggioAttivo.getUtente();
            if (u != null) {
                u.setCredito(u.getCredito() - costoCorsa);
                dbm.aggiornaCreditoUtente(u.getId(), u.getCredito());
            }
        }

        // Aggiorna veicolo e chiude il noleggio nel database
        dbm.aggiornaStatoVeicolo(codice, StatoVeicolo.LIBERO, v.getBatteria());
        dbm.chiudiNoleggio(codice, oraFine);

        return costoCorsa;
    }

    public void mettiInRicarica(String codice) throws RossiGoException {
        Veicolo v = veicoli.get(codice);
        if (v == null) {
            throw new VeicoloNonDisponibileException("Veicolo '" + codice + "' non trovato");
        }
        if (v.getStato() == StatoVeicolo.IN_USO) {
            throw new VeicoloNonDisponibileException("Impossibile mettere in ricarica il veicolo '" + codice + "' perché è attualmente in uso");
        }

        v.setStato(StatoVeicolo.IN_RICARICA);
        v.setBatteria(100);
        dbm.aggiornaStatoVeicolo(codice, StatoVeicolo.IN_RICARICA, v.getBatteria());
    }

    // BONUS: Stima del costo con eccezione se credito insufficiente
    public double stimaCosto(String codiceVeicolo, int minuti, String idUtente) throws RossiGoException {
        Veicolo v = veicoli.get(codiceVeicolo);
        if (v == null) {
            throw new VeicoloNonDisponibileException("Veicolo '" + codiceVeicolo + "' non trovato");
        }
        Utente u = utenti.get(idUtente);
        if (u == null) {
            throw new UtenteNonTrovatoException("Utente '" + idUtente + "' non trovato");
        }

        double costoStimato = 1.0 + v.calcolaCosto(minuti);
        if (u.getCredito() < costoStimato) {
            throw new CreditoInsufficienteException(
                String.format("Credito insufficiente per completare la corsa di %d min: credito attuale %.2f €, costo stimato %.2f €",
                        minuti, u.getCredito(), costoStimato)
            );
        }
        return costoStimato;
    }
}

