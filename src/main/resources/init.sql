-- S: studente, I: insegnanete, E: esterno, A: admin
CREATE TABLE IF NOT EXISTS utenti (
	username varchar(30) PRIMARY KEY,
	email varchar(50) UNIQUE NOT NULL,
	sesso varchar(1) NOT NULL,
	data_nascita date NOT NULL,
	ruolo varchar(10) NOT NULL,
	password_hash varbinary(256) NOT NULL,
	salt varbinary(64) NOT NULL
);

--Per il tipo: può essere Terra (T), Sintetico (S), Cemento (C)
CREATE TABLE IF NOT EXISTS campi (
	id smallint(4) UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	indirizzo varchar(50) NOT NULL,
    tipo char(1) NOT NULL
);

CREATE TABLE IF NOT EXISTS sport (
	id smallint(4) UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	nome_sport varchar(30) NOT NULL,
	descrizione varchar(256)
);

CREATE TABLE IF NOT EXISTS tornei (
	id int PRIMARY KEY AUTO_INCREMENT,
	id_sport smallint(4) UNSIGNED NOT NULL,
	id_campo smallint(4) UNSIGNED NOT NULL,
	username_creatore varchar(30) NOT NULL,
	descrizione varchar(256),
    data_inizio DATE,
    data_fine DATE,
	FOREIGN KEY (id_sport) REFERENCES sport(id),
	FOREIGN KEY (id_campo) REFERENCES campo(id),
	FOREIGN KEY (username_creatore) REFERENCES utenti(username)

);

--con anni_classi_partecipanti si intende Biennio (B), Triennio (T) o Misto (M), per genere si intende che il torneo è dedicato a Maschi (M), Femmine (F), o misto (M).
CREATE TABLE IF NOT EXISTS partite_scolastiche (
	id int PRIMARY KEY AUTO_INCREMENT,
	id_campo smallint(4) UNSIGNED NOT NULL,
	username_creatore varchar(30) NOT NULL,
	descrizione varchar(256),
    data_partita DATE,
    orario timestamp,
    anni_classe_partecipanti char(1),
    max_giocatori tinyint(4),
    min_giocatori tinyint(4),
    numero_giocatori_per_squadra tinyint(4),
    genere char(1),
	id_torneo int REFERENCES tornei(id),
	FOREIGN KEY (id_campo) REFERENCES campo(id),
	FOREIGN KEY (username_creatore) REFERENCES utenti(username)

);
CREATE TABLE IF NOT EXISTS partite_amatoriali (
	id int PRIMARY KEY AUTO_INCREMENT,
	id_campo smallint(4) UNSIGNED NOT NULL,
	username_creatore varchar(30) NOT NULL,
	descrizione varchar(256),
    data_partita DATE,
    orario timestamp,
    max_giocatori tinyint(4),
    min_giocatori tinyint(4),
    numero_giocatori_per_squadra tinyint(4),
    genere char(1),
    id_torneo int REFERENCES tornei(id),
	FOREIGN KEY (id_campo) REFERENCES campo(id),
	FOREIGN KEY (username_creatore) REFERENCES utenti(username)

);

-- P: principiante, I: intermedio, A: agonistico
CREATE TABLE IF NOT EXISTS iscrizioni_sport (
	id_sport smallint(4) UNSIGNED,
	studente varchar(30) NOT NULL,
	livello_professionalita varchar(1) NOT NULL CHECK (livello_professionalita IN ("P", "I", "A")) -- Da controllare
);

CREATE TABLE IF NOT EXISTS iscrizioni_utenti_tornei (
	id_torneo int,
	username_professore varchar(30) NOT NULL,
	ruolo varchar(1) NOT NULL,
	FOREIGN KEY (id_torneo) REFERENCES tornei(id),
	FOREIGN KEY (username_professore) REFERENCES utenti(username)
);

CREATE TABLE IF NOT EXISTS iscrizioni_utenti_tornei (
	id_torneo int,
	username_studente varchar(30) NOT NULL,
	FOREIGN KEY (id_torneo) REFERENCES tornei(id)
);
