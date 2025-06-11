# Bibliophile - Tutorial Túnel

Repositório utilizado como referência para demonstrar a integração de túneis Cloudflare em projetos reais, facilitando o desenvolvimento e testes em ambientes locais.

Confira o passo a passo completo no tutorial disponível na [Wikiversidade](https://pt.wikiversity.org/wiki/T%C3%BAneis_de_Rede:_Conceitos,_Aplica%C3%A7%C3%B5es_e_Uso_com_Cloudflare_Tunnel).

## Stack do Projeto

O projeto utiliza as seguintes tecnologias:

- **API de Livros**: Integração com a Open Library API para obter informações detalhadas sobre livros.
- **Backend**: Desenvolvido em Kotlin, utilizando o framework Ktor para criar uma RestAPI.
- **Banco de Dados**: Utiliza MySQL ou MariaDB para persistência de dados.
- **Frontend**: Construído com React JSX, garantindo uma interface dinâmica e responsiva.
- **Containerização**: Utiliza Docker e Docker Compose para facilitar a execução e o gerenciamento do ambiente.

### Bibliotecas Utilizadas

- **Material-UI (MUI)** para componentes visuais modernos e acessíveis.

## Como Rodar o Projeto

O projeto está containerizado, sendo possível iniciá-lo facilmente com Docker Compose.

### Configuração do Ambiente

Antes de iniciar o projeto, copie o arquivo .env.example e configure suas variáveis de ambiente:
Edite o arquivo .env e preencha os valores necessários, como credenciais do banco de dados.

### Utilizando Docker

1. Certifique-se de que o Docker e o Docker Compose estão instalados em sua máquina.
2. No diretório raiz do projeto, execute: `docker-compose up --build`
3. O backend e o frontend estarão acessíveis nos endpoints configurados no `docker-compose.yml`.

### Testes Automatizados

Para executar os testes automatizados, siga os passos abaixo:

1. Acesse o container do backend utilizando o comando:

    ```bash
    docker-compose exec backend bash
    ```

2. Dentro do container, execute o comando para rodar os testes:

    ```bash
    ./gradlew test
    ```

    Os relatórios de testes estarão disponíveis no seguinte caminho: `backend/build/reports/tests/test/index.html`

3. Para gerar e visualizar o relatório de cobertura de testes, execute:

    ```bash
    ./gradlew koverHtmlReport
    ```

    O relatório de cobertura será gerado no seguinte caminho: `backend/build/reports/kover/html/index.html`
