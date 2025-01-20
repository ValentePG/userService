insert into tbl_usuario (id, email, first_name, last_name, roles, password) values (1, 'yusuke@gmail.com', 'yusuke', 'urameshi','USER','{bcrypt}$2a$10$RTw9.dfy8DZ813JcadlXlOw9H6UWF7Y31HRU.YVGQXxqqe6ihqDC.');
insert into tbl_usuario (id, email, first_name, last_name, roles, password)values (2, 'hiei@gmail.com', 'hiei', 'dragon','USER','{bcrypt}$2a$10$RTw9.dfy8DZ813JcadlXlOw9H6UWF7Y31HRU.YVGQXxqqe6ihqDC.');
insert into tbl_profile (id, description, name) values (1, 'Manages everything', 'Admin');
insert into user_profile(id, user_id, profile_id) values (1, 1, 1);
insert into user_profile(id, user_id, profile_id) values (2, 2, 1);