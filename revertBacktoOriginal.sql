use moviedb;
DROP TABLE IF EXISTS genres_in_movies;
DROP TABLE IF EXISTS ratings;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS stars_in_movies;
DROP TABLE IF EXISTS movies;
DROP TABLE IF EXISTS stars;

CREATE TABLE IF NOT EXISTS movies(
    id varchar(10) primary key not null,
    title varchar(100) not null,
    year integer not null,
    director varchar(100) not null
);
Insert INTO movies SELECT * FROM movies_bp;


CREATE TABLE IF NOT EXISTS stars (
    id varchar(10) primary key not null,
    name varchar(100) not null,
    birthYear integer
);
insert into stars SELECT * FROM moviedb.stars_bp;


CREATE TABLE IF NOT EXISTS stars_in_movies (
    starId VARCHAR(10) NOT NULL,
    moviesId VARCHAR(10) NOT NULL
);
insert into stars_in_movies SELECT * FROM moviedb.stars_in_movies_bp;


CREATE TABLE IF NOT EXISTS genres(
    d integer primary key not null AUTO_INCREMENT,
    name varchar(32) not null
);
insert into genres SELECT * FROM moviedb.genres_bp;


CREATE TABLE IF NOT EXISTS genres_in_movies(
    genreId integer not null,
    movieId VARCHAR(10) not null
);
insert into genres_in_movies SELECT * FROM moviedb.genres_in_movies_bp;

CREATE TABLE IF NOT EXISTS ratings(
    movieId varchar(10) not null,
    rating float not null,
    numVotes integer not null
    );
insert into ratings SELECT * FROM moviedb.ratings_bp;
