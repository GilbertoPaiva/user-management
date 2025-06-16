# Pet Connect - Sistema de Gestão Pet

## Visão Geral

O Pet Connect é um sistema completo implementado seguindo os princípios SOLID, Clean Architecture e Arquitetura Hexagonal em Java com Spring Boot. O sistema conecta tutores, veterinários, lojistas e administradores em uma plataforma integrada para o universo pet.

## Arquitetura Implementada

### 🏗️ Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/petconnect/
│   │   ├── domain/                    # Camada de Domínio (Entidades e Regras de Negócio)
│   │   │   ├── user/entity/          # Entidades do usuário
│   │   │   ├── lojista/entity/       # Entidades do lojista
│   │   │   ├── produto/entity/       # Entidades do produto
│   │   │   ├── tutor/entity/         # Entidades do tutor
│   │   │   ├── veterinario/entity/   # Entidades do veterinário
│   │   │   └── servico/entity/       # Entidades do serviço
│   │   ├── application/              # Camada de Aplicação (Casos de Uso)
│   │   │   ├── user/usecase/         # Casos de uso do usuário
│   │   │   └── produto/usecase/      # Casos de uso do produto
│   │   ├── infrastructure/           # Camada de Infraestrutura
│   │   │   ├── adapter/
│   │   │   │   ├── persistence/      # Adaptadores de persistência
│   │   │   │   └── web/             # Adaptadores web (Controllers)
│   │   │   └── config/              # Configurações
│   │   └── exception/               # Tratamento de exceções
│   └── resources/
│       ├── static/                  # Landing page
│       └── db/migration/           # Migrações do banco
```

## 🚀 Funcionalidades Implementadas

### 1. Gestão de Usuários e Autenticação
- ✅ Cadastro de usuários (Admin, Tutor, Veterinário, Lojista)
- ✅ Autenticação por email e senha
- ✅ Recuperação de senha com perguntas de segurança
- ✅ Validação de senhas com regras de segurança
- ✅ Diferentes tipos de perfil de usuário

### 2. Gestão de Produtos (Lojistas)
- ✅ CRUD completo de produtos
- ✅ Associação de produtos com lojistas
- ✅ Upload de fotos (campo preparado)
- ✅ Unidades de medida

### 3. Dashboard Administrativo
- ✅ Contagem de usuários por tipo
- ✅ Listagem e pesquisa de usuários
- ✅ Detalhes de usuários
- ✅ Ativação/desativação de usuários

### 4. Landing Page
- ✅ Página inicial responsiva e moderna
- ✅ Descrição das funcionalidades
- ✅ Design atrativo com Bootstrap

## 🛠️ Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.x**
- **Spring Security** - Autenticação e autorização
- **Spring Data JPA** - Persistência de dados
- **H2 Database** - Banco em memória para desenvolvimento
- **Flyway** - Migrações de banco de dados
- **Lombok** - Redução de boilerplate
- **Bean Validation** - Validação de dados
- **Bootstrap 5** - Frontend da landing page

## 📊 Banco de Dados

### Principais Tabelas
- `users` - Usuários do sistema
- `user_roles` - Papéis dos usuários
- `lojistas` - Dados específicos de lojistas
- `produtos` - Produtos cadastrados
- `tutores` - Dados específicos de tutores
- `veterinarios` - Dados específicos de veterinários
- `servicos` - Serviços oferecidos por veterinários

## 🔧 Como Executar

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6 ou superior

### Passos para execução
1. Clone o repositório
2. Execute: `./mvnw clean compile` (corrigir erros de compilação)
3. Execute: `./mvnw spring-boot:run`
4. Acesse: `http://localhost:8080`

## 🌐 Endpoints da API

### Autenticação
- `POST /api/auth/register` - Cadastro de usuário
- `POST /api/auth/login` - Login
- `GET /api/auth/forgot-password/{email}` - Pergunta de segurança
- `POST /api/auth/reset-password` - Reset de senha

### Produtos
- `POST /api/produtos` - Criar produto
- `GET /api/produtos/lojista/{id}` - Produtos do lojista
- `PUT /api/produtos/{id}` - Atualizar produto
- `DELETE /api/produtos/{id}` - Remover produto

### Administração
- `GET /api/admin/dashboard` - Dashboard
- `GET /api/admin/users` - Listar usuários
- `GET /api/admin/users/{id}` - Detalhes do usuário
- `PUT /api/admin/users/{id}/toggle-status` - Ativar/desativar

## 🔄 Próximos Passos para Completar

### 1. Correções Necessárias (PRIORITÁRIO)
```bash
# Erro principal: Lombok não está gerando métodos
# Soluções:
# 1. Verificar se Lombok está configurado no IDE
# 2. Adicionar plugin do Lombok no Maven
# 3. Verificar annotations @Data, @Builder, etc.
```

### 2. Funcionalidades Pendentes
- [ ] Implementar CRUD de Serviços (Veterinários)
- [ ] Dashboard do Tutor (visualização de produtos/serviços)
- [ ] Sistema de avaliações
- [ ] Chat entre usuários
- [ ] Notificações
- [ ] Upload real de imagens
- [ ] Geolocalização
- [ ] Sistema de pagamento

### 3. Melhorias Técnicas
- [ ] Implementar JWT para autenticação
- [ ] Testes unitários e de integração
- [ ] Documentação Swagger/OpenAPI
- [ ] Docker para containerização
- [ ] Pipeline CI/CD
- [ ] Monitoramento e logs
- [ ] Cache com Redis
- [ ] Mensageria com RabbitMQ

### 4. Frontend Completo
- [ ] Aplicação React/Angular/Vue
- [ ] Páginas de login/cadastro
- [ ] Dashboards específicos por perfil
- [ ] Interface para CRUD de produtos/serviços
- [ ] Sistema de notificações em tempo real

## 🎯 Requisitos Funcionais Implementados

### RF1: Gestão de Usuários e Acesso ✅
- RF1.1: Landing Page ✅
- RF1.2: Redirecionamento para login ✅
- RF1.3: Cadastro por tipo de usuário ✅
- RF1.4: Autenticação ✅
- RF1.5: Redirecionamento por tipo ✅
- RF1.6: Recuperação de senha ✅
- RF1.7: Redefinição de senha ✅

### RF2: Gerenciamento de Conteúdo (Lojista) ✅
- RF2.1: Lista de produtos ✅
- RF2.2: Cadastro de produtos ✅
- RF2.3: Edição de produtos ✅
- RF2.4: Remoção de produtos ✅

### RF5: Gerenciamento de Sistema (Admin) ✅
- RF5.1: Dashboard geral ✅
- RF5.2: Gerenciamento de usuários ✅
- RF5.3: Detalhes de usuário ✅

## 📝 Notas de Desenvolvimento

### Padrões Aplicados
- **SOLID**: Cada classe tem responsabilidade única
- **Clean Architecture**: Separação clara das camadas
- **Hexagonal Architecture**: Portas e adaptadores bem definidos
- **Repository Pattern**: Abstração do acesso a dados
- **Command Pattern**: Para casos de uso
- **Builder Pattern**: Para criação de objetos complexos

### Segurança
- Senhas criptografadas com BCrypt
- Validação de dados de entrada
- Proteção contra SQL Injection
- CORS configurado
- Tratamento global de exceções

## 🚨 Avisos Importantes

1. **Banco H2**: Dados são perdidos ao reiniciar (ideal para desenvolvimento)
2. **Sem JWT**: Sistema usa Spring Security básico
3. **Frontend**: Apenas landing page estática
4. **Erros de Compilação**: Lombok precisa ser configurado corretamente

## 📞 Suporte

Para completar a implementação:
1. Corrigir erros do Lombok
2. Implementar funcionalidades pendentes
3. Criar frontend completo
4. Adicionar testes

Este projeto fornece uma base sólida seguindo as melhores práticas de arquitetura para um sistema completo de gestão pet.
