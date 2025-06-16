INSERT INTO public.users (id, username, email, password, full_name, active, created_at, updated_at, user_type, create_by)
VALUES (
    gen_random_uuid(),
    'adminuser',
    'admin@exemplo.com',
    '$2y$10$xpSs2TMNknkcw5gpcPg7beba/io/LRd1yOuyyj.oe70hdIpfDjn7i',
    'Administrador',
    true,
    NOW(),
    NOW(),
    'colaborador',
    'admin'
);


INSERT INTO public.user_roles (user_id, role_id)
SELECT u.id, r.id
FROM public.users u, public.roles r
WHERE u.username = 'adminuser' AND r.name = 'ROLE_ADMIN';
