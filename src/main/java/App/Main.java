package App;

import Eccezioni.*;
import Modello.*;
import Servizio.FlottaService;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(
    name = "rossigo",
    mixinStandardHelpOptions = true,
    version = "RossiGo 1.0",
    description = "Sistema CLI di sharing urbano RossiGo (bici e monopattini)",
    subcommands = {
        Main.SimulaCommand.class,
        Main.StatoCommand.class,
        Main.SbloccaCommand.class,
        Main.TerminaCommand.class,
        Main.RicaricaCommand.class,
        Main.StimaCommand.class
    }
)
public class Main implements Callable<Integer> {

    public static final String BANNER = """
    ================================================================================
      ____                _  ____       
     |  _ \\ ___  ___ ___ (_)/ ___| ___  
     | |_) / _ \\/ __/ __|| | |  _ / _ \\ 
     |  _ < (_) \\__ \\__ \\| | |_| | (_) |
     |_| \\_\\___/|___/___/|_|\\____|\\___/ 
          Mobilità Elettrica & Sharing Urbano - Versione 1.0
    ================================================================================
    """;

    @Override
    public Integer call() {
        System.out.println(BANNER);
        System.out.println("Nessun sottocomando specificato. Esecuzione della simulazione automatica (Punto 7)...\n");
        return new SimulaCommand().call();
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    // ---------------------------------------------------------------------------------
    // SOTTOCOMANDO: SIMULAZIONE SCENARI (Punto 7 della traccia)
    // ---------------------------------------------------------------------------------
    @Command(name = "simula", description = "Simula una giornata tipo con scenari di successo, eccezioni e persistenza")
    public static class SimulaCommand implements Callable<Integer> {
        @Override
        public Integer call() {
            System.out.println(BANNER);
            System.out.println(">>> AVVIO SIMULAZIONE GIORNATA ROSSIGO <<<");
            System.out.println("--------------------------------------------------------------------------------\n");

            try {
                FlottaService flotta = new FlottaService();

                // -------------------------------------------------------------------------
                // SCENARIO 1 (Eccezione): Registrazione utente con Codice Fiscale errato
                // -------------------------------------------------------------------------
                System.out.println("[SCENARIO 1] Tentativo di registrazione utente con Codice Fiscale NON valido");
                try {
                    Utente utenteErrato = new Utente("U999", "Paolo Neri", "paolo.neri@example.com", "CF_NON_VALIDO_99", 10.0);
                    flotta.registraUtente(utenteErrato);
                    System.out.println("ERRORE: La registrazione doveva fallire!");
                } catch (CodiceFiscaleNonValidoException e) {
                    System.out.println("-> Eccezione intercettata con successo:");
                    System.out.println("   [MESSAGGIO UTENTE] Registrazione rifiutata: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("-> Eccezione intercettata con successo:");
                    System.out.println("   [MESSAGGIO UTENTE] Formato dati utente non valido: " + e.getMessage());
                }
                System.out.println();

                // -------------------------------------------------------------------------
                // SCENARIO 2 (Setup): Registrazione utenti e veicoli validi
                // -------------------------------------------------------------------------
                System.out.println("[SCENARIO 2] Registrazione utenti e veicoli regolari nella flotta");
                try {
                    // Utente con credito insufficiente (< 1€)
                    if (!flotta.getUtenti().containsKey("U001")) {
                        flotta.registraUtente(new Utente("U001", "Mario Rossi", "mario.rossi@email.it", "RSSMRA85M01H501Z", 0.50));
                        System.out.println("   + Registrato utente U001 (Mario Rossi, credito: 0.50 €)");
                    }
                    // Utente con credito sufficiente
                    if (!flotta.getUtenti().containsKey("U002")) {
                        flotta.registraUtente(new Utente("U002", "Giulia Bianchi", "giulia.bianchi@email.it", "BNCGLI90A41F205W", 15.00));
                        System.out.println("   + Registrato utente U002 (Giulia Bianchi, credito: 15.00 €)");
                    }
                    if (!flotta.getUtenti().containsKey("U003")) {
                        flotta.registraUtente(new Utente("U003", "Luca Verdi", "luca.verdi@email.it", "VRDLCU80A01H501U", 10.00));
                        System.out.println("   + Registrato utente U003 (Luca Verdi, credito: 10.00 €)");
                    }

                    // Bicicletta con batteria carica
                    if (!flotta.getVeicoli().containsKey("BK-01")) {
                        flotta.registraVeicolo(new Bicicletta("BK-01", 90, StatoVeicolo.LIBERO, 4));
                        System.out.println("   + Registrata bici BK-01 (batteria: 90%, 4 marce)");
                    }
                    // Bicicletta con batteria scarica (< 15%)
                    if (!flotta.getVeicoli().containsKey("BK-02")) {
                        flotta.registraVeicolo(new Bicicletta("BK-02", 10, StatoVeicolo.LIBERO, 6));
                        System.out.println("   + Registrata bici BK-02 (batteria: 10%, 6 marce)");
                    }
                    // Monopattino con batteria carica
                    if (!flotta.getVeicoli().containsKey("SC-01")) {
                        flotta.registraVeicolo(new Monopattino("SC-01", 85, StatoVeicolo.LIBERO, true));
                        System.out.println("   + Registrato monopattino SC-01 (batteria: 85%, zona pedonale: sì)");
                    }
                } catch (Exception e) {
                    System.out.println("   Nota durante registrazione: " + e.getMessage());
                }
                System.out.println();

                // -------------------------------------------------------------------------
                // SCENARIO 3 (Eccezione): Sblocco con credito insufficiente (< 1€)
                // -------------------------------------------------------------------------
                System.out.println("[SCENARIO 3] Utente U001 tenta di sbloccare SC-01 con credito di 0.50 €");
                try {
                    flotta.sbloccaVeicolo("SC-01", "U001");
                    System.out.println("ERRORE: Lo sblocco doveva fallire per credito insufficiente!");
                } catch (CreditoInsufficienteException e) {
                    System.out.println("-> Eccezione intercettata con successo:");
                    System.out.println("   [MESSAGGIO UTENTE] Sblocco non consentito: " + e.getMessage());
                }
                System.out.println();

                // -------------------------------------------------------------------------
                // SCENARIO 4 (Eccezione): Tentativo di sblocco da parte di utente inesistente
                // -------------------------------------------------------------------------
                System.out.println("[SCENARIO 4] Utente sconosciuto 'U-GHOST' tenta di sbloccare BK-01");
                try {
                    flotta.sbloccaVeicolo("BK-01", "U-GHOST");
                    System.out.println("ERRORE: Lo sblocco doveva fallire per utente inesistente!");
                } catch (UtenteNonTrovatoException e) {
                    System.out.println("-> Eccezione intercettata con successo:");
                    System.out.println("   [MESSAGGIO UTENTE] Attenzione: " + e.getMessage());
                }
                System.out.println();

                // -------------------------------------------------------------------------
                // SCENARIO 5 (Eccezione): Sblocco di veicolo con batteria scarica (< 15%)
                // -------------------------------------------------------------------------
                System.out.println("[SCENARIO 5] Utente U002 tenta di sbloccare la bici BK-02 (batteria al 10%)");
                try {
                    flotta.sbloccaVeicolo("BK-02", "U002");
                    System.out.println("ERRORE: Lo sblocco doveva fallire per batteria scarica!");
                } catch (BatteriaScaricaException e) {
                    System.out.println("-> Eccezione intercettata con successo:");
                    System.out.println("   [MESSAGGIO UTENTE] Mezzo non disponibile: batteria al " + e.getBatteriaAttuale() + "%. Scegli un altro mezzo!");
                }
                System.out.println();

                // -------------------------------------------------------------------------
                // SCENARIO 6 (Successo): Noleggio regolare monopattino SC-01 da parte di U002
                // -------------------------------------------------------------------------
                System.out.println("[SCENARIO 6] Noleggio regolare: U002 sblocca il monopattino SC-01 e corre per 20 min");
                try {
                    flotta.sbloccaVeicolo("SC-01", "U002");
                    System.out.println("   + Monopattino SC-01 sbloccato con successo da Giulia Bianchi.");

                    // Termine del noleggio dopo 20 minuti
                    // Costo: 1€ sblocco + (20 min * 0.25€) = 6.00 €
                    // Consumo batteria: 20 min * 1% = 20% (da 85% a 65%)
                    double costo = flotta.terminaNoleggio("SC-01", 20);
                    Veicolo v = flotta.getVeicoli().get("SC-01");
                    Utente u = flotta.getUtenti().get("U002");

                    System.out.printf("   + Corsa terminata: durata 20 min | Costo addebitato: %.2f €%n", costo);
                    System.out.printf("   + Credito residuo utente Giulia Bianchi: %.2f €%n", u.getCredito());
                    System.out.println("   + Batteria residua SC-01: " + v.getBatteria() + "% | Stato veicolo: " + v.getStato());
                } catch (RossiGoException e) {
                    System.out.println("ERRORE inaspettato: " + e.getMessage());
                }
                System.out.println();

                // -------------------------------------------------------------------------
                // SCENARIO 7 (Eccezione): Sblocco di veicolo già in uso
                // -------------------------------------------------------------------------
                System.out.println("[SCENARIO 7] Tentativo di sbloccare un veicolo già in uso");
                try {
                    // Sblocchiamo prima regolarmente la bici BK-01 con U002
                    flotta.sbloccaVeicolo("BK-01", "U002");
                    System.out.println("   + Bici BK-01 sbloccata da Giulia Bianchi (U002).");

                    // Ora Luca Verdi (U003) prova a sbloccare la stessa bici già in uso
                    System.out.println("   + Luca Verdi (U003) prova a sbloccare la stessa bici BK-01...");
                    flotta.sbloccaVeicolo("BK-01", "U003");
                    System.out.println("ERRORE: Lo sblocco doveva fallire per veicolo già in uso!");
                } catch (VeicoloNonDisponibileException e) {
                    System.out.println("-> Eccezione intercettata con successo:");
                    System.out.println("   [MESSAGGIO UTENTE] Mezzo occupato: " + e.getMessage());
                } finally {
                    // Ripristiniamo la bici terminando il noleggio
                    flotta.terminaNoleggio("BK-01", 5);
                }
                System.out.println();

                // -------------------------------------------------------------------------
                // SCENARIO 8 (Bonus): Stima del costo prima del noleggio
                // -------------------------------------------------------------------------
                System.out.println("[SCENARIO 8 - BONUS] Stima preventiva del costo");
                try {
                    // Corsa fattibile: 10 minuti su bici BK-01 (1€ + 1.50€ = 2.50€)
                    double stimaOk = flotta.stimaCosto("BK-01", 10, "U002");
                    System.out.printf("   + Stima 10 min su BK-01 per U002: %.2f € (credito sufficiente: %.2f €)%n",
                            stimaOk, flotta.getUtenti().get("U002").getCredito());

                    // Corsa troppo lunga che supera il credito residuo
                    System.out.println("   + Stima corsa di 120 minuti su monopattino SC-01 per U002 (credito insufficiente)...");
                    flotta.stimaCosto("SC-01", 120, "U002");
                    System.out.println("ERRORE: La stima doveva lanciare CreditoInsufficienteException!");
                } catch (CreditoInsufficienteException e) {
                    System.out.println("-> Eccezione intercettata con successo:");
                    System.out.println("   [MESSAGGIO UTENTE] Preventivo corsa: " + e.getMessage());
                }
                System.out.println();

                // -------------------------------------------------------------------------
                // SCENARIO 9 (Persistenza): Verifica persistenza dati al riavvio del servizio
                // -------------------------------------------------------------------------
                System.out.println("[SCENARIO 9 - PERSISTENZA] Simulazione spegnimento e riavvio del programma");
                System.out.println("   + Chiusura sessione attuale...");
                flotta = null; // Distruzione dell'istanza in memoria

                System.out.println("   + Riavvio applicazione e creazione nuova FlottaService (lettura da SQLite)...");
                FlottaService flottaRiavviata = new FlottaService();

                Utente uRicaricato = flottaRiavviata.getUtenti().get("U002");
                Veicolo vRicaricato = flottaRiavviata.getVeicoli().get("SC-01");

                if (uRicaricato != null && vRicaricato != null) {
                    System.out.println("   >>> DATI RECUPERATI CORRETTAMENTE DA SQLITE! <<<");
                    System.out.printf("   + Utente 'Giulia Bianchi': Credito salvato = %.2f €%n", uRicaricato.getCredito());
                    System.out.printf("   + Monopattino 'SC-01': Batteria salvata = %d%% | Stato = %s%n",
                            vRicaricato.getBatteria(), vRicaricato.getStato());
                    System.out.println("   + Storico noleggi ricaricato dal DB: " + flottaRiavviata.getNoleggi().size() + " noleggi totali registrati.");
                } else {
                    System.out.println("ERRORE: I dati persistenti non sono stati trovati nel database!");
                }

                System.out.println("\n--------------------------------------------------------------------------------");
                System.out.println(">>> SIMULAZIONE COMPLETATA CON SUCCESSO! TUTTI GLI SCENARI SUPERATI <<<");
                System.out.println("================================================================================");

                return 0;

            } catch (ErroreDatabaseException e) {
                System.err.println("Errore irreversibile del database: " + e.getMessage());
                return 1;
            } catch (Exception e) {
                System.err.println("Errore inatteso: " + e.getMessage());
                return 1;
            }
        }
    }

    // ---------------------------------------------------------------------------------
    // SOTTOCOMANDI CLI PICOCLI (sblocca, termina, ricarica, stato, stima)
    // ---------------------------------------------------------------------------------

    @Command(name = "stato", description = "Mostra l'elenco e lo stato attuale dei veicoli e degli utenti")
    public static class StatoCommand implements Callable<Integer> {
        @Override
        public Integer call() {
            try {
                FlottaService flotta = new FlottaService();
                System.out.println("=== FLOTTA VEICOLI ===");
                for (Veicolo v : flotta.getVeicoli().values()) {
                    String tipo = (v instanceof Bicicletta) ? "Bicicletta" : "Monopattino";
                    System.out.printf("- [%s] Codice: %s | Batteria: %d%% | Stato: %s%n",
                            tipo, v.getCodice(), v.getBatteria(), v.getStato());
                }

                System.out.println("\n=== UTENTI REGISTRATI ===");
                for (Utente u : flotta.getUtenti().values()) {
                    System.out.printf("- [ID: %s] %s | CF: %s | Credito: %.2f €%n",
                            u.getId(), u.getNome(), u.getCodiceFiscale(), u.getCredito());
                }
                return 0;
            } catch (ErroreDatabaseException e) {
                System.err.println("Errore database: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "sblocca", description = "Sblocca un veicolo per un determinato utente")
    public static class SbloccaCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Codice del veicolo da sbloccare (es. BK-01, SC-01)")
        private String codice;

        @Parameters(index = "1", description = "ID dell'utente (es. U001)")
        private String idUtente;

        @Override
        public Integer call() {
            try {
                FlottaService flotta = new FlottaService();
                flotta.sbloccaVeicolo(codice, idUtente);
                System.out.println("Veicolo " + codice + " sbloccato con successo per l'utente " + idUtente + "! Buona corsa.");
                return 0;
            } catch (RossiGoException e) {
                System.out.println("Impossibile sbloccare: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "termina", description = "Termina il noleggio di un veicolo indicando i minuti di utilizzo")
    public static class TerminaCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Codice del veicolo (es. BK-01, SC-01)")
        private String codice;

        @Parameters(index = "1", description = "Minuti di durata del noleggio")
        private int minuti;

        @Override
        public Integer call() {
            try {
                FlottaService flotta = new FlottaService();
                double costo = flotta.terminaNoleggio(codice, minuti);
                System.out.printf("Noleggio terminato per %s. Costo totale addebitato: %.2f €.%n", codice, costo);
                return 0;
            } catch (RossiGoException e) {
                System.out.println("Impossibile terminare il noleggio: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "ricarica", description = "Mette in ricarica un veicolo")
    public static class RicaricaCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Codice del veicolo da ricaricare")
        private String codice;

        @Override
        public Integer call() {
            try {
                FlottaService flotta = new FlottaService();
                flotta.mettiInRicarica(codice);
                System.out.println("Veicolo " + codice + " messo in ricarica con successo (batteria ripristinata al 100%).");
                return 0;
            } catch (RossiGoException e) {
                System.out.println("Operazione fallita: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "stima", description = "Stima il costo per una corsa data la durata in minuti")
    public static class StimaCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Codice del veicolo")
        private String codice;

        @Parameters(index = "1", description = "Minuti stimati di percorrenza")
        private int minuti;

        @Parameters(index = "2", description = "ID dell'utente")
        private String idUtente;

        @Override
        public Integer call() {
            try {
                FlottaService flotta = new FlottaService();
                double stima = flotta.stimaCosto(codice, minuti, idUtente);
                System.out.printf("Stima corsa di %d minuti per il veicolo %s: %.2f €.%n", minuti, codice, stima);
                return 0;
            } catch (RossiGoException e) {
                System.out.println("Verifica preventivo non superata: " + e.getMessage());
                return 1;
            }
        }
    }
}
