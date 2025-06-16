## Atividade IV

Empresa criada: AutoBots

Parceiros: Toyota Motor Corporation e Grupo Volskwagen

Fundador, Dev e Engenheiro de Software: André Salerno

## Objetivo

- Implementar a "atualização de segurança" que consiste em incluir no sistema o processo de autenticação e autorização via JSON Web Token (JWT).

Segue abaixo um padrão a ser seguido:

## Perfis e Autorizações

## 🛡️ Perfis e Autorizações

| 🧑‍💼 **Perfil**     | 🔐 **Autorizações** |
|--------------------|----------------------|
| ![Admin](https://img.shields.io/badge/-Administrador-red) | ✅ Acesso total à aplicação<br>🔄 Pode **criar, ler, atualizar e deletar (CRUD)** qualquer recurso<br>👥 Pode **adicionar/remover** outros administradores |
| ![Gerente](https://img.shields.io/badge/-Gerente-blue) | 👥 Pode fazer **CRUD** sobre usuários dos perfis: gerente, vendedor e cliente<br>🛒 Pode fazer **CRUD** sobre serviços, vendas e mercadorias |
| ![Vendedor](https://img.shields.io/badge/-Vendedor-green) | 👥 Pode fazer **CRUD** sobre usuários **cliente**<br>📦 Pode **ler** informações sobre serviços e mercadorias<br>🧾 Pode **criar** suas próprias vendas e **visualizá-las** |
| ![Cliente](https://img.shields.io/badge/-Cliente-lightgrey) | 👤 Pode **visualizar** seu próprio cadastro<br>🧾 Pode **visualizar** suas próprias compras |

> ℹ️ **CRUD**: Create, Read, Update, Delete

