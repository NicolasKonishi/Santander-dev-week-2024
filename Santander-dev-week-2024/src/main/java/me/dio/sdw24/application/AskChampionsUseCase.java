package me.dio.sdw24.application;

import me.dio.sdw24.domain.exception.ChampionNotFoundException;
import me.dio.sdw24.domain.model.Champion;
import me.dio.sdw24.domain.ports.ChampionsRepository;
import me.dio.sdw24.domain.ports.GenerativeAiService;

public record AskChampionsUseCase(ChampionsRepository repository, GenerativeAiService genAiApi) {

    public String askChampion(Long championId, String question){

        Champion champion = repository.findById(championId)
                .orElseThrow(() -> new ChampionNotFoundException(championId));

        String context = champion.generateContexByQuestion(question);
        String objective = """
                Atue como uma assistente com a habilidade de se comportar como os campeões do League of Legends (LOL).
                Responda perguntas incorporando a personalidadede e estilo de um determinado campeão.
                Segue a pergunta, o nome do Campeão e sua respectiva lore(história):
                
                """;

        return genAiApi.generateContent(objective, context);

    }
}
