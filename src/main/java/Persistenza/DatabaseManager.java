package Persistenza;

import Eccezioni.ErroreDatabaseException;
import Modello.*;
import org.sqlite.SQLiteException;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseManager {
    private Connection connection = null;
    private static DatabaseManager instance = null;
    private final String url = null;

    private DatabaseManager(String url) throws SQLException {
        connection = DriverManager.getConnection(url);
    }

    public static DatabaseManager getInstance() throws ErroreDatabaseException {
        try {
            if (instance == null) {
                instance = new DatabaseManager("jdbc:sqlite:rossigo.db");
                instance.createTablesIfNotExist();
            }
            return instance;
        } catch (SQLException e)
        {
            throw new ErroreDatabaseException("Errore di connessione al database: " + e.getMessage());
        }
    }

    private boolean testConnection ()
    {
        try {
            return connection.isValid(5);
        } catch (SQLException e)
        {
            return false;
        }
    }

    public void createTablesIfNotExist() throws ErroreDatabaseException {

        if (!testConnection())
            throw new ErroreDatabaseException("Impossibile collegarsi");

        //2 preparazione query
        //String query = "INSERT INTO sushi_menu (nome_piatto, prezzo, quantita) VALUES ('" + nomePiatto +  "', " + prezzo + ", " + quantita + ")";
        String stmt = """
                create table if not exists utenti
                (
                    id            varchar(32)  not null
                        constraint utenti_pk
                            primary key,
                    nome          varchar(32)  not null,
                    email         varchar(128) not null,
                    codice_fiscale varchar(16)  not null,
                    credito       double       not null
                );
                
                create table if not exists noleggi
                (
                    utente    varchar(32)  not null
                        constraint noleggi_utenti_id_fk
                            references utenti,
                    veicolo   varchar(100) not null
                        constraint noleggi_noleggi_veicolo_fk
                            references noleggi (veicolo),
                    oraInizio time,
                    oraFine   time,
                    constraint noleggi_pk
                        primary key (utente, veicolo)
                );
                
                create table if not exists veicoli
                (
                    codice                 varchar(100) not null
                        constraint codice
                            primary key,
                    batteria               integer      not null,
                    stato_veicolo          varchar(32)      not null,
                    limitato_zona_pedonale boolean,
                    num_marce              integer,
                    tipo_veicolo           varchar(32)  not null
                );
                
                """;

        //3 esecuzione query
        try {
            PreparedStatement statement = connection.prepareStatement(stmt);
            statement.execute();
        }
        catch (SQLException e)
        {
            throw new ErroreDatabaseException("Invalid creazione tabelle se non esistono " + e.getMessage());
        }
    }

    public boolean aggiungiUtente(Utente u)
    {

        if (!testConnection())
            return false;

        //2 preparazione query
        //String query = "INSERT INTO sushi_menu (nome_piatto, prezzo, quantita) VALUES ('" + nomePiatto +  "', " + prezzo + ", " + quantita + ")";
        String query = "INSERT INTO utenti (id, nome, email, codice_fiscale, credito) VALUES (?, ?, ?, ?, ?)";

        //3 esecuzione query
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, u.getId());
            statement.setString(2, u.getNome());
            statement.setString(3, u.getEmail());
            statement.setString(4, u.getCodiceFiscale());
            statement.setDouble(5, u.getCredito());
            statement.execute();
        }
        catch (SQLException e)
        {
            return false;
        }
        return true;
    }

    public boolean aggiungiVeicolo(Veicolo v)
    {
        if (!testConnection())
            return false;

        //2 preparazione query
        //String query = "INSERT INTO sushi_menu (nome_piatto, prezzo, quantita) VALUES ('" + nomePiatto +  "', " + prezzo + ", " + quantita + ")";
        String query = "INSERT INTO veicoli (codice, batteria, stato_veicolo, limitato_zona_pedonale, num_marce, tipo_veicolo) VALUES (?, ?, ?, ?, ?, ?)";

        //3 esecuzione query
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, v.getCodice());
            statement.setInt(2, v.getBatteria());
            statement.setString(3, v.getStato().name());

            if (v instanceof Bicicletta b) {
                int marce = b.getNumMarce();
                statement.setNull(4, Types.BOOLEAN);
                statement.setInt(5, b.getNumMarce());
                statement.setString(6, "BICICLETTA");
            } else if (v instanceof Monopattino m) {
                boolean limitato = m.isLimitatoInZonaPedonale();
                statement.setBoolean(4, m.isLimitatoInZonaPedonale());
                statement.setNull(5, Types.INTEGER);
                statement.setString(6, "MONOPATTINO");
            }

            statement.execute();
        }
        catch (SQLException e)
        {
            return false;
        }
        return true;
    }

    public HashMap<String, Veicolo> caricaVeicoli() {
        HashMap<String, Veicolo> ret = new HashMap<>();
        String sql = "SELECT codice, batteria, stato_veicolo, tipo_veicolo, limitato_zona_pedonale, num_marce FROM veicoli";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String cod = rs.getString("codice");
                int bat = rs.getInt("batteria");
                StatoVeicolo statov = StatoVeicolo.valueOf(rs.getString("stato_veicolo"));
                String tipo = rs.getString("tipo_veicolo");

                Veicolo v = null;
                if (tipo.equalsIgnoreCase("BICICLETTA")) {
                    int numm = rs.getInt("num_marce");
                    v = new Bicicletta(cod, bat, statov, numm);
                } else if (tipo.equalsIgnoreCase("MONOPATTINO")) {
                    boolean limit = rs.getBoolean("limitato_zona_pedonale");
                    v = new Monopattino(cod, bat, statov, limit);
                } else {
                    continue;
                }
                ret.put(v.getCodice(), v);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    public HashMap<String, Utente> caricaUtenti ()
    {
        HashMap<String, Utente> ret = new HashMap<>();
        String sql = "SELECT id, nome, email, codice_fiscale, credito FROM utenti";

        try (PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String cod = rs.getString("id");
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                String codice_fiscale = rs.getString("codice_fiscale");
                double credito = rs.getDouble("credito");
                Utente u = new Utente(cod, nome, email, codice_fiscale, credito);
                ret.put(u.getId(), u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }


    public List<Noleggio> caricaNoleggi() {
        return caricaNoleggi(caricaUtenti(), caricaVeicoli());
    }

    public List<Noleggio> caricaNoleggi(HashMap<String, Utente> utenti, HashMap<String, Veicolo> veicoli) {
        List<Noleggio> ret = new ArrayList<>();
        String sql = "SELECT utente, veicolo, oraInizio, oraFine FROM noleggi";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String idUtente = rs.getString("utente");
                String codiceVeicolo = rs.getString("veicolo");
                Time tInizio = rs.getTime("oraInizio");
                Time tFine = rs.getTime("oraFine");

                Utente u = (utenti != null) ? utenti.get(idUtente) : null;
                Veicolo v = (veicoli != null) ? veicoli.get(codiceVeicolo) : null;
                LocalTime oraInizio = (tInizio != null) ? tInizio.toLocalTime() : null;
                LocalTime oraFine = (tFine != null) ? tFine.toLocalTime() : null;

                ret.add(new Noleggio(u, v, oraInizio, oraFine));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    //IMPORTANTE: qui metto i metodi non che corripondono esattamente alle necessità
    //della flottaservice, ma che ne siano le generalizzazioni
    //ad esempio una funzione di FlottaService vuole impostare lo stato su ON
    //qui fai una funzione per impostare lo stato di un veicolo v a StatoVeicolo x passato come parametro

    public boolean aggiornaStatoVeicolo(String codice, StatoVeicolo nuovoStato, int nuovaBatteria) // -> fa l'UPDATE veicoli SET stato_veicolo = ?, batteria = ? WHERE codice = ?
    {
        String sql = "UPDATE veicoli SET stato_veicolo = ?, batteria = ? WHERE codice = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nuovoStato.name());
            ps.setInt(2, nuovaBatteria);
            ps.setString(3, codice);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }
    public boolean aggiornaCreditoUtente(String idUtente, double nuovoCredito) //-> fa l'UPDATE utenti SET credito = ? WHERE id = ?
    {
        String sql = "UPDATE utenti SET credito = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, nuovoCredito);
            ps.setString(2, idUtente);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }
    public boolean inserisciInizioNoleggio(String idUtente, String codiceVeicolo) //-> fa la INSERT INTO noleggi
    {
        String sql = "INSERT INTO noleggi (utente, veicolo, oraInizio, oraFine) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, idUtente);
            ps.setString(2, codiceVeicolo);
            ps.setTime(3, Time.valueOf(java.time.LocalTime.now()));
            ps.setNull(4, Types.TIME);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean chiudiNoleggio(String codiceVeicolo, LocalTime oraFine) {
        String sql = "UPDATE noleggi SET oraFine = ? WHERE veicolo = ? AND oraFine IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTime(1, Time.valueOf(oraFine));
            ps.setString(2, codiceVeicolo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}
