INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('HAIRDRESSER');
INSERT INTO roles (name) VALUES ('CUSTOMER');

INSERT INTO public.hairoffers (name, description, price, duration) VALUES
('Strzyżenie Damskie', 'Profesjonalne strzyżenie włosów damskich z myciem i modelowaniem.', 85.00, 60);

INSERT INTO public.hairoffers (name, description, price, duration) VALUES
('Farbowanie Całościowe', 'Farbowanie włosów na jednolity kolor, obejmujące aplikację, mycie i odżywienie.', 180.00, 150);

INSERT INTO public.hairoffers (name, description, price, duration) VALUES
('Cieniowanie i Podcięcie', 'Delikatne cieniowanie i podcięcie końcówek dla odświeżenia fryzury.', 60.00, 45);

INSERT INTO public.hairoffers (name, description, price, duration) VALUES
('Balayage / Ombre', 'Technika rozjaśniania włosów, tworząca naturalny efekt przejścia kolorów.', 250.00, 210);

INSERT INTO public.hairoffers (name, description, price, duration) VALUES
('Strzyżenie Męskie', 'Klasyczne lub nowoczesne strzyżenie męskie z myciem i stylizacją.', 50.00, 40);

-- Admin Account (rola_id_fk = 1) haslo zaq1@WSX! do wszystkich
INSERT INTO public.accounts (username, password, date_created, email, is_active, role_id_fk) VALUES
('admin', '$2a$12$G9w0UMfTQ8cOrslicGYFy./tIyxaUfGxCRMU4WxW8uNKcJULSif1m', CURRENT_TIMESTAMP, 'adamczyk97@op.pl', TRUE, 1);

-- Fryzjer Accounts (rola_id_fk = 2)
INSERT INTO public.accounts (username, password, date_created, email, is_active, role_id_fk) VALUES
('fryzjer1', '$2a$12$G9w0UMfTQ8cOrslicGYFy./tIyxaUfGxCRMU4WxW8uNKcJULSif1m', CURRENT_TIMESTAMP, 'adamczyk97@op.pl', TRUE, 2);

INSERT INTO public.accounts (username, password, date_created, email, is_active, role_id_fk) VALUES
('fryzjer2', '$2a$12$G9w0UMfTQ8cOrslicGYFy./tIyxaUfGxCRMU4WxW8uNKcJULSif1m', CURRENT_TIMESTAMP, 'adamczyk97@op.pl', TRUE, 2);

INSERT INTO public.accounts (username, password, date_created, email, is_active, role_id_fk) VALUES
('fryzjer3', '$2a$12$G9w0UMfTQ8cOrslicGYFy./tIyxaUfGxCRMU4WxW8uNKcJULSif1m', CURRENT_TIMESTAMP, 'adamczyk97@op.pl', TRUE, 2);

INSERT INTO public.accounts (username, password, date_created, email, is_active, role_id_fk) VALUES
('fryzjer4', '$2a$12$G9w0UMfTQ8cOrslicGYFy./tIyxaUfGxCRMU4WxW8uNKcJULSif1m', CURRENT_TIMESTAMP, 'adamczyk97@op.pl', TRUE, 2);

INSERT INTO public.accounts (username, password, date_created, email, is_active, role_id_fk) VALUES
('fryzjer5', '$2a$12$G9w0UMfTQ8cOrslicGYFy./tIyxaUfGxCRMU4WxW8uNKcJULSif1m', CURRENT_TIMESTAMP, 'adamczyk97@op.pl', TRUE, 2);

-- Dane osobowe dla Admina (account_id_fk = 1)
INSERT INTO public.users (uuid, firstname, surname, phone, account_id_fk) VALUES
(gen_random_uuid(), 'Anna', 'Kowalska', '500123456', 1);

-- Dane osobowe dla Fryzjerów (account_id_fk od 2 do 6)
INSERT INTO public.users (uuid, firstname, surname, phone, account_id_fk) VALUES
(gen_random_uuid(), 'Marta', 'Nowak', '501234567', 2);

INSERT INTO public.users (uuid, firstname, surname, phone, account_id_fk) VALUES
(gen_random_uuid(), 'Krzysztof', 'Woźniak', '502345678', 3);

INSERT INTO public.users (uuid, firstname, surname, phone, account_id_fk) VALUES
(gen_random_uuid(), 'Ewa', 'Dąbrowska', '503456789', 4);

INSERT INTO public.users (uuid, firstname, surname, phone, account_id_fk) VALUES
(gen_random_uuid(), 'Tomasz', 'Kamiński', '504567890', 5);

INSERT INTO public.users (uuid, firstname, surname, phone, account_id_fk) VALUES
(gen_random_uuid(), 'Magdalena', 'Lewandowska', '505678901', 6);