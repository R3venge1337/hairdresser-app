--liquibase formatted sql
--changeset adam.zimny:1 labels:DEV

--------------------------------------------
---------------Tables-----------------------
--------------------------------------------

--------------------------------------------
---------------accounts----------------------
--------------------------------------------
CREATE TABLE dbo.users (
    id bigint NOT NULL IDENTITY(1,1),
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
CREATE TABLE dbo.accounts (
    id bigint NOT NULL IDENTITY(1,1),
    nickname  varchar(60) NOT NULL,
	password varchar(255) NOT NULL,
	date_created TIMESTAMP NOT NULL,
	email varchar(100) NOT NULL,
	is_active bit NOT NULL,
	role_id_fk bigint NOT NULL
);
GO
--------------------------------------------
---------------roles----------------------
--------------------------------------------
CREATE TABLE dbo.roles (
    id bigint NOT NULL IDENTITY(1,1),
    name  varchar(50) NOT NULL
);
GO

-----------------------------------------------------
--------------Define primary keys-------------------
-----------------------------------------------------
ALTER TABLE dbo.users
ADD CONSTRAINT users_pk
PRIMARY KEY (id);
GO

ALTER TABLE dbo.accounts
ADD CONSTRAINT accounts_pk
PRIMARY KEY (id);
GO

ALTER TABLE dbo.roles
ADD CONSTRAINT roles_pk
PRIMARY KEY (id);
GO

-------------------------------------
-----Define foreign keys-------------
-------------------------------------

ALTER TABLE dbo.users
ADD CONSTRAINT users_accounts_fk
FOREIGN KEY (accounts_id_fk)
REFERENCES dbo.accounts(id);
GO

ALTER TABLE dbo.accounts
ADD CONSTRAINT accounts_roles_fk
FOREIGN KEY (roles_id_fk)
REFERENCES dbo.roles(id);
GO

--rollback DROP TABLE dbo.roles;
--rollback DROP TABLE dbo.accounts;
--rollback DROP TABLE dbo.users;