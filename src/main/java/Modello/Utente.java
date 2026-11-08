package Modello;

import org.apache.commons.validator.routines.DomainValidator;
import org.apache.commons.validator.routines.EmailValidator;

public class Utente {
    private String id;
    private String nome;
    private String email;
    private String codiceFiscale;
    private double credito;

    public Utente (String id, String nome, String email, String codiceFiscale, double credito)
    {
        setId(id);
        setNome(nome);
        setEmail(email);
        setCodiceFiscale(codiceFiscale);
        setCredito(credito);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome.isBlank()) throw new IllegalArgumentException("nome utente is empty");
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id.isBlank()) throw new IllegalArgumentException("id utente < 0");
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email.isBlank()) throw new IllegalArgumentException("email utente is empty");
        EmailValidator ev = EmailValidator.getInstance();
        if (!ev.isValid(email)) throw new IllegalArgumentException("email is not valid - commons validator");
        this.email = email;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        if (codiceFiscale.isBlank() || codiceFiscale.length() != 16) throw new IllegalArgumentException("codiceFiscale utente is empty");

        this.codiceFiscale = codiceFiscale;
    }

    public double getCredito() {
        return credito;
    }

    public void setCredito(double credito) {
        this.credito = credito;
    }
}
