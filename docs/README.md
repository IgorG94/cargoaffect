# CargoAffect

## Introdução

O projeto CargoAffect consiste em um sistema composto por um aplicativo de captura de voz para o sistema Android e um serviço web (API) que realiza o processamento de amostras de voz de caminhoneiros com base em um algoritmo supervisionado, tomando ações de acordo com o estado emocional detectado, para melhorar a experiência de direção dos motoristas. 

Os módulos de análise de áudio foram obtidos a partir da biblioteca [pyAudioAnalysis](https://github.com/tyiannak/pyAudioAnalysis), criada por [Theodoros Giannakopoulos](https://tyiannak.github.io) e explorada [em sua publicação](http://journals.plos.org/plosone/article?id=10.1371/journal.pone.0144610).

Clique [aqui](https://github.com/IgorG94/cargoaffect) para acessar o repositório do projeto.

## Configuração

Para rodar a API, rode o comando `./scripts/start`.
Para realizar o treinamento do algoritmo SVM, rode o comando `./scripts/training`.
É necessário ter uma pasta com áudios para treinamento e arquivos `.csv` com os valores de escalas emocionais, no [padrão](https://github.com/tyiannak/pyAudioAnalysis/wiki/4.-Classification-and-Regression#regression) definido na biblioteca pyAudioAnalysis.

## Documentos

Os documentos do projeto de formatura (Banner e Press Release) estão disponíveis na pasta `docs/`.
Para acessar a pasta, clique [aqui](https://github.com/IgorG94/cargoaffect/tree/master/docs).
