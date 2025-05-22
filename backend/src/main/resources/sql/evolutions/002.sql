--liquibase formatted sql
--changeset adam.zimny:2 labels:DEV

--------------------------------------------
---------------Tables-----------------------
--------------------------------------------

--------------------------------------------
---------------hairoffers----------------------
--------------------------------------------
CREATE TABLE hairoffers (
    id BIGSERIAL NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    duration BIGINT NOT NULL
);
GO

--------------------------------------------
---------------appointments-----------------
--------------------------------------------
CREATE TABLE appointments (
    id BIGSERIAL NOT NULL,
    uuid  UUID NOT NULL,
    total_cost DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    booked_date TIMESTAMP NOT NULL,
    customer_id_fk  bigint NOT NULL,
    hairdresser_id_fk  bigint NOT NULL
);
GO

--------------------------------------------
---------------appointments_hairoffers-----------------
--------------------------------------------
CREATE TABLE appointments_hairoffers (
    id BIGSERIAL NOT NULL,
    appointment_id_fk  bigint NOT NULL,
    hairoffer_id_fk  bigint NOT NULL
);
GO

-----------------------------------------------------
--------------Define primary keys-------------------
-----------------------------------------------------
ALTER TABLE hairoffers
ADD CONSTRAINT hairoffers_pk
PRIMARY KEY (id);
GO

ALTER TABLE appointments
ADD CONSTRAINT appointments_pk
PRIMARY KEY (id);
GO

ALTER TABLE appointments_hairoffers
ADD CONSTRAINT appointments_hairoffers_pk
PRIMARY KEY (id);
GO

-------------------------------------
-----Define foreign keys-------------
-------------------------------------

ALTER TABLE appointments
ADD CONSTRAINT appointments_customer_fk
FOREIGN KEY (customer_id_fk)
REFERENCES users(id);
GO

ALTER TABLE appointments
ADD CONSTRAINT appointments_hairdresser_fk
FOREIGN KEY (hairdresser_id_fk)
REFERENCES users(id);
GO

ALTER TABLE appointments_hairoffers
ADD CONSTRAINT appointments_hairoffers_appointment_fk
FOREIGN KEY (appointment_id_fk)
REFERENCES appointments(id);
GO

ALTER TABLE appointments_hairoffers
ADD CONSTRAINT appointments_hairoffers_hairoffer_fk
FOREIGN KEY (hairoffer_id_fk)
REFERENCES hairoffers(id);
GO
--rollback DROP TABLE hairoffers;
--rollback DROP TABLE appointments;
--rollback DROP TABLE appointments_hairoffers;