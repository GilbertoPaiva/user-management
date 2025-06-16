INSERT INTO public.users (id, username, email, password, full_name, active, created_at, updated_at, user_type, create_by)
VALUES (
    gen_random_uuid(),
    'userteste',
    'userteste@exemplo.com',
    '$2y$10$xpSs2TMNknkcw5gpcPg7beba/io/LRd1yOuyyj.oe70hdIpfDjn7i',
    'User Teste',
    true,
    NOW(),
    NOW(),
    'comum',
    'system'
);


INSERT INTO public.user_roles (user_id, role_id)
SELECT u.id, r.id
FROM public.users u, public.roles r
WHERE u.username = 'userteste' AND r.name = 'ROLE_USER';


CREATE TABLE IF NOT EXISTS tickets
(
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    ticket_status VARCHAR(20) NOT NULL,
    user_id UUID NOT NULL,
    assigned_to UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_tickets_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_tickets_assigned_to FOREIGN KEY (assigned_to) REFERENCES users(id)
);
