--liquibase formatted sql
--changeset adam.zimny:3 labels:DEV

--------------------------------------------
---------------Tables-----------------------
--------------------------------------------

--------------------------------------------
---------------tokens-----------------
--------------------------------------------
CREATE TABLE tokens (
    id  BIGSERIAL NOT NULL ,
    token VARCHAR(1024) UNIQUE NOT NULL,
    token_type VARCHAR(50) NOT NULL,
    revoked BOOLEAN NOT NULL,
    expired BOOLEAN NOT NULL,
    user_id_fk BIGINT NOT NULL
);
GO

-----------------------------------------------------
--------------Define primary keys-------------------
-----------------------------------------------------
ALTER TABLE tokens
ADD CONSTRAINT tokens_pk
PRIMARY KEY (id);
GO

-------------------------------------
-----Define foreign keys-------------
-------------------------------------

ALTER TABLE tokens
ADD CONSTRAINT tokens_user_fk
FOREIGN KEY (user_id_fk)
REFERENCES users(id);
GO

--rollback DROP TABLE tokens;
