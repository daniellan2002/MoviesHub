use moviedb;

CREATE TABLE IF NOT EXISTS movies(
    id varchar(10) primary key not null,
    title varchar(100) not null,
    year integer not null,
    director varchar(100) not null
);

CREATE TABLE IF NOT EXISTS stars(
    id varchar(10) primary key not null,
    name varchar(100) not null,
    birthYear integer
);

CREATE TABLE IF NOT EXISTS stars_in_movies(
    starId VARCHAR(10) DEFAULT '',
    movieId VARCHAR(10) DEFAULT '',
    FOREIGN KEY (starId) REFERENCES stars(id) ON DELETE CASCADE,
    FOREIGN KEY (movieId) REFERENCES movies(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS genres(
                                     id integer primary key not null AUTO_INCREMENT,
                                     name varchar(32) not null
    );

CREATE TABLE IF NOT EXISTS genres_in_movies(
                                               genreId integer not null,
                                               movieId VARCHAR(10) not null,
    FOREIGN KEY (genreId) REFERENCES genres(id) ON DELETE CASCADE,
    FOREIGN KEY (movieId) REFERENCES movies(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS creditcards(
    id varchar(20) primary key not null,
    firstName varchar(50) not null,
    lastName varchar(50) not null,
    expiration date not null
    );


CREATE TABLE IF NOT EXISTS customers(
    id integer primary key not null AUTO_INCREMENT,
    firstName varchar(50) not null,
    lastName varchar(50) not null,
    ccId varchar(20) not null,
    address varchar(200) not null,
    email varchar(50) not null,
    password varchar(20) not null,
    FOREIGN KEY (ccId) REFERENCES creditcards(id) ON DELETE CASCADE
    );

CREATE TABLE sales (
                       id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
                       customerId INTEGER NOT NULL,
                       movieId VARCHAR(1000) NOT NULL,
                       saleDate DATE NOT NULL,
                       FOREIGN KEY (customerId) REFERENCES customers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ratings(
    movieId varchar(10) not null,
    rating float not null,
    numVotes integer not null
);