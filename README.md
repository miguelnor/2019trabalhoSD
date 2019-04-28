# Trabalho de Sistemas Distribuídos

Este trabalho foi/está sendo desenvolvido para a matéria de Sistemas Distribuídos.
Faculdade de Computação - Universidade Federal de Uberlândia

Professor: Lásaro Camargos (http://www.facom.ufu.br/~lasaro/)

Desenvolvido por: Miguel S. Pereira

## Primeira Entrega:

Arquitetura da primeira entrega consiste em desenvolver programa com as seguintes especifícações dadas:

### Cliente

#### Leitura de comandos
- 1 thread em loop infinito apresentando menu de comandos e lendo comandos do teclado;
- comando entrado deve ser validado localmente;
- se válido, comando enviado para o servidor;
- se inválido, mensagem de erro é apresentada;
- comando sair termina a execução desta thread;

#### Apresentação de respostas
- 1 thread em loop infinito recebendo mensagens do servidor;
- uma vez recebida mensagem, a mesma é apresentada na tela;
- uma vez terminada a thread de leitura de comandos, esta deve esperar pelo menos 5 segundos por novas mensagens e então se termina.

### Servidor

#### A  base de dados
- Mapa de BigInteger (inteiro de precisão infinita) para um vetor de bytes (ou algo que o valha).
- mantido em memória apenas (por enquanto)
- manipulado por 4 operações (CRUD) observando a semântica de cada operação (create <> update)
- todos os comandos retornam um código de erro/sucesso mais resposta, se for adequado.
- Apesar do banco ser em memória, toda operação será logada em disco.

#### Terá arquitetura em estágios
- 1 ou mais threads recebendo comandos e colocando em uma fila F1
- 1 thread consumindo comandos de F1 e colocando cópias do comando em uma fila F2 e em outra fila F3
- 1 thread consumindo comandos de F2 e gravando-os em disco.
- 1 thread consumindo de F3 aplicando o comando no banco de dados (mapa)

##### Thread de log
- grava comandos em um arquivo de log
- adiciona comandos sempre ao fim do arquivo somente se comandos alterarem a base de dados

##### Thread de processamento
- manipula o mapa
- emite respostas de sucesso (create/update/delete)
- responde com a informação solicitada (read)
- emite erros quando adequado
- processa seguindo a ordem da fila F3

##### Tolerância de falhas
Na reinicialização do processo deve ser aberto o arquivo de log e feito o reprocessamento dos comandos na ordem antes de processar novos comandos.

### Comunicação
Deve ser feita toda via TCP, o canal cliente-servidor deve ser mantido aberto enquanto o comando estiver sendo executado
Todas as portas que forem usadas para comunicação devem ser especificadas via arquivo de configuração