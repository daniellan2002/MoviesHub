
DELIMITER $$
CREATE PROCEDURE generate_star_id(out new_star_id varchar(9))
BEGIN
    DECLARE max_id INT;
SELECT MAX(RIGHT(id, 7)) INTO max_id FROM stars;
SET new_star_id = CONCAT('nm', LPAD(max_id + 1, 7, '0'));
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE generate_movie_id(out new_movie_id varchar(9))
BEGIN
    DECLARE max_id INT;
SELECT MAX(RIGHT(id, 7)) INTO max_id FROM movies;
SET new_movie_id = CONCAT('tt', LPAD(max_id + 1, 7, '0'));
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE insert_movie (
    IN title VARCHAR(100),
    IN year INT,
    IN director VARCHAR(100)
)
BEGIN
    DECLARE new_id VARCHAR(10);
    DECLARE max_id VARCHAR(10);
    DECLARE prefix VARCHAR(10);
    DECLARE suffix VARCHAR(10);

SELECT MAX(id) INTO max_id FROM movies;

SET prefix = SUBSTRING(max_id, 1, 2);
    SET suffix = SUBSTRING(max_id, 3);

    SET suffix = LPAD(CAST(suffix AS UNSIGNED) + 1, 7, '0');

    SET new_id = CONCAT(prefix, suffix);

INSERT INTO movies (id, title, year, director)
VALUES (new_id, title, year, director);
END$$
DELIMITER ;


