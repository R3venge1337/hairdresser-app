--liquibase formatted sql
--changeset adam.zimny:1 labels:DEV

--------------------------------------------
---------------Tables-----------------------
--------------------------------------------

--------------------------------------------
---------------accounts----------------------
--------------------------------------------
CREATE TABLE users (
    id BIGSERIAL NOT NULL,
    uuid  UUID NOT NULL,
	firstname varchar(100) NOT NULL,
	surname varchar(100) NOT NULL,
	phone varchar(15) NOT NULL,
	account_id_fk bigint NOT NULL
);
GO

--------------------------------------------
---------------accounts----------------------
--------------------------------------------
CREATE TABLE accounts (
    id BIGSERIAL NOT NULL ,
    username  varchar(60) NOT NULL,
	password varchar(255) NOT NULL,
	date_created TIMESTAMP NOT NULL,
	email varchar(100) NOT NULL,
	is_active boolean NOT NULL,
	role_id_fk bigint NOT NULL
);
GO
--------------------------------------------
---------------roles----------------------
--------------------------------------------
CREATE TABLE roles (
    id BIGSERIAL NOT NULL ,
    name  varchar(50) NOT NULL
);
GO

-----------------------------------------------------
--------------Define primary keys-------------------
-----------------------------------------------------
ALTER TABLE users
ADD CONSTRAINT users_pk
PRIMARY KEY (id);
GO

ALTER TABLE accounts
ADD CONSTRAINT accounts_pk
PRIMARY KEY (id);
GO

ALTER TABLE roles
ADD CONSTRAINT roles_pk
PRIMARY KEY (id);
GO

-------------------------------------
-----Define foreign keys-------------
-------------------------------------

ALTER TABLE users
ADD CONSTRAINT users_accounts_fk
FOREIGN KEY (account_id_fk)
REFERENCES accounts(id);
GO

ALTER TABLE accounts
ADD CONSTRAINT accounts_roles_fk
FOREIGN KEY (role_id_fk)
REFERENCES roles(id);
GO

--rollback DROP TABLE roles;
--rollback DROP TABLE accounts;
--rollback DROP TABLE users;