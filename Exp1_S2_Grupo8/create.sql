create database veterinaria_db;
create user 'veterinaria'@'%' identified by 'admin123';
grant all on veterinaria_db.* to 'veterinaria'@'%';