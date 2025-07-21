# MyChest

MyChest é um plugin Bukkit/Spigot para gerenciamento de baús virtuais ilimitados, com armazenamento seguro em MySQL. Tenha inventários privados, persistentes e acessíveis de qualquer lugar do servidor!

---

## Funcionalidades

- **Baús virtuais ilimitados:** Cada jogador pode criar e acessar quantos baús quiser.
- **Armazenamento persistente:** Todos os itens salvos em banco de dados MySQL.
- **Sistema de grupos e permissões:** Controle quem pode criar, acessar e editar baús.
- **Gerenciamento via comandos:** Adicione, remova, edite e administre baús facilmente.
- **Mensagens customizáveis:** Personalize o feedback para os jogadores.
- **Integração Vault:** Permissões avançadas usando o Vault.
- **Inventário multi-viewer:** Vários jogadores podem abrir o mesmo baú simultaneamente.
- **Configuração fácil:** Arquivos `config.yml` e `messages.yml` para personalização.

---

## Instalação

1. **Pré-requisitos:**  
   - Servidor Bukkit/Spigot/PaperMC
   - Plugin [Vault](https://dev.bukkit.org/projects/vault) instalado  
   - Banco de dados MySQL acessível

2. **Instale o plugin:**  
   - Baixe o arquivo `.jar` do MyChest.
   - Coloque em `plugins/MyChest.jar` na pasta do seu servidor.

3. **Configure o MySQL:**  
   - Edite o arquivo `config.yml` gerado na pasta do plugin:
     ```yaml
     mysql:
       host: "localhost"
       port: "3306"
       database: "mychest"
       username: "usuario"
       password: "senha"
     ```
   - Reinicie o servidor.

---

## Comandos

| Comando                     | Descrição                                            |
|-----------------------------|-----------------------------------------------------|
| `/chest`                    | Abre o baú virtual principal do jogador             |
| `/chests`                   | Lista todos os seus baús virtuais                   |
| `/chestadmin <jogador>`     | Administra os baús de outro jogador (permissão)     |
| `/chestedit <id>`           | Edita propriedades do baú (nome, tamanho, etc.)     |

Exemplo:
```
/chest
/chests
/chestadmin HugoSilvaF
/chestedit 1
```

---

## Permissões

- `mychest.use` — Permite usar comandos de baú virtual
- `mychest.admin` — Permite administrar baús de outros jogadores
- Permissões de grupo configuráveis no `config.yml`

---

## Configuração

- **config.yml:**  
  Ajuste MySQL, permissões de grupos, aliases de comandos, opções avançadas.
- **messages.yml:**  
  Personalize todas as mensagens exibidas pelo plugin.

---

## Exemplo de Grupo no config.yml

```yaml
permissions:
  groups:
    default:
      chests: 3
      size: "27" # slots
    vip:
      chests: 10
      size: "54"
```

---

## Dependências

- [Vault](https://dev.bukkit.org/projects/vault) (obrigatório)
- MySQL
- Bukkit/Spigot/PaperMC

---

## Contribuição

Pull requests e sugestões são bem-vindos!  
Abra uma issue para reportar bugs ou sugerir melhorias.

---

## Licença

Este projeto ainda não possui uma licença definida.

---

## Autor

Desenvolvido por [HugoSilvaF](https://github.com/HugoSilvaF)
