use moviedb;
DROP TABLE IF EXISTS genres_in_movies_bp;
DROP TABLE IF EXISTS genres_bp;
DROP TABLE IF EXISTS stars_in_movies_bp;
DROP TABLE IF EXISTS movies_bp;
DROP TABLE IF EXISTS stars_bp;

CREATE TABLE IF NOT EXISTS movies_bp(
    id varchar(10) primary key not null,
    title varchar(100) not null,
    year integer not null,
    director varchar(100) not null
    );
Insert INTO movies_bp SELECT * FROM movies;

CREATE TABLE IF NOT EXISTS stars_bp (
    id varchar(10) primary key not null,
    name varchar(100) not null,
    birthYear integer
    );
insert into stars_bp SELECT * FROM moviedb.stars;

CREATE TABLE IF NOT EXISTS stars_in_movies_bp (
    starId VARCHAR(10) NOT NULL,
    moviesId VARCHAR(10) NOT NULL,
    FOREIGN KEY(starId) REFERENCES stars_bp(id) ON DELETE CASCADE,
    FOREIGN KEY(moviesId) REFERENCES movies_bp(id) ON DELETE CASCADE
    );
insert into stars_in_movies_bp SELECT * FROM moviedb.stars_in_movies;

CREATE TABLE IF NOT EXISTS genres_bp(
    id integer primary key not null AUTO_INCREMENT,
    name varchar(32) not null
    );
insert into genres_bp SELECT * FROM moviedb.genres;

CREATE TABLE IF NOT EXISTS genres_in_movies_bp(
    genreId integer not null,
    movieId VARCHAR(10) not null,
    FOREIGN KEY (genreId) REFERENCES genres_bp(id) ON DELETE CASCADE,
    FOREIGN KEY (movieId) REFERENCES movies_bp(id) ON DELETE CASCADE
    );
insert into genres_in_movies_bp SELECT * FROM moviedb.genres_in_movies;

CREATE TABLE IF NOT EXISTS ratings_bp(
    movieId varchar(10) not null,
    rating float not null,
    numVotes integer not null
    );
insert into ratings_bp SELECT * FROM moviedb.ratings;

