CREATE TABLE IF NOT EXISTS classi(
                                     nome varchar(4) PRIMARY KEY CHECK (nome LIKE("____"))
);

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

CREATE TABLE IF NOT EXISTS iscrizioni_utenti_classi(
                                                       nome_classe varchar(4),
                                                       studente varchar(30),
                                                       FOREIGN KEY (nome_classe) REFERENCES classi(nome),
                                                       FOREIGN KEY (studente) REFERENCES utenti(username)
);

CREATE TABLE IF NOT EXISTS sport (
                                     id smallint(4) UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                                     nome_sport varchar(30) NOT NULL,
                                     descrizione varchar(256)
);

-- P: principiante, I: intermedio, A: agonistico
CREATE TABLE IF NOT EXISTS iscrizioni_sport (
                                                id_sport smallint(4) UNSIGNED,
                                                studente varchar(30) NOT NULL,
                                                livello_professionalita varchar(1) NOT NULL CHECK (livello_professionalita IN ("P", "I", "A")) -- Da controllare
);

CREATE TABLE IF NOT EXISTS campo (
                                     id smallint(4) UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                                     indirizzo varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS tornei (
                                      id int PRIMARY KEY AUTO_INCREMENT,
                                      id_sport smallint(4) UNSIGNED NOT NULL,
                                      id_campo smallint(4) UNSIGNED NOT NULL,
                                      coach varchar(30) NOT NULL,
                                      prof_creatore varchar(30) NOT NULL,
                                      descrizione varchar(256),
                                      FOREIGN KEY (id_sport) REFERENCES sport(id),
                                      FOREIGN KEY (id_campo) REFERENCES campo(id),
                                      FOREIGN KEY (coach) REFERENCES utenti(username),
                                      FOREIGN KEY (prof_creatore) REFERENCES utenti(username)

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
