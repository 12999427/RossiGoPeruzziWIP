package Modello;

import java.time.LocalTime;

public class Noleggio {
    private Utente utente;
    private Veicolo veicolo;
    private LocalTime orarioInizio;
    private LocalTime orarioFine;

    public Noleggio(Utente utente, Veicolo veicolo, LocalTime orarioInizio, LocalTime orarioFine) {
        this.utente = utente;
        this.veicolo = veicolo;
        this.orarioInizio = orarioInizio;
        this.orarioFine = orarioFine;
    }

    public Noleggio(Utente utente, Veicolo veicolo, LocalTime orarioInizio) {
        this(utente, veicolo, orarioInizio, null);
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public Veicolo getVeicolo() {
        return veicolo;
    }

    public void setVeicolo(Veicolo veicolo) {
        this.veicolo = veicolo;
    }

    public LocalTime getOrarioInizio() {
        return orarioInizio;
    }

    public void setOrarioInizio(LocalTime orarioInizio) {
        this.orarioInizio = orarioInizio;
    }

    public LocalTime getOrarioFine() {
        return orarioFine;
    }

    public void setOrarioFine(LocalTime orarioFine) {
        this.orarioFine = orarioFine;
    }
}

