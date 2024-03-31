package me.dio.sdw24.adapters.out;

import feign.FeignException;
import feign.RequestInterceptor;
import me.dio.sdw24.domain.ports.GenerativeAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
@ConditionalOnProperty(name = "generative-ai.provider", havingValue = "GEMINI", matchIfMissing = true)
@FeignClient(name = "geminiApi", url ="${gemini.base-url}", configuration = GoogleGeminiService.Config.class)
public interface GoogleGeminiService extends GenerativeAiService {

    @PostMapping("v1beta/models/gemini-pro:generateContent")
    GoogleGeminiResponsive textOnlyInput(GoogleGeminiRequest req);
    @Override
    default String generateContent(String objective, String context){
        String prompt = """
                %s
                %s
                """.formatted(objective,context);

        GoogleGeminiRequest req = new GoogleGeminiRequest(
                List.of(new Content(List.of(new Part(prompt))))
        );
        try {
            GoogleGeminiResponsive resp = textOnlyInput(req);
            return resp.candidates().getFirst().content().parts().getFirst().text();
        }catch (FeignException httpErrors){
            return "Erro de comunicão com a API do Google Gemini :(";
        }catch (Exception unexpectedError){
            return "O retorno da API do Google Gemini não contem os dados esperados :0";
        }
    }
    record GoogleGeminiRequest(List<Content> contents){}
    record Content(List<Part> parts){}
    record Part(String text){}
    record GoogleGeminiResponsive(List<Candidate> candidates){}
    record Candidate(Content content){}

    class Config {
        @Bean
        public RequestInterceptor apiKeyRequestInterception(@Value("${gemini.api-key}") String apiKey){
            return requestTemplate -> requestTemplate.query("key",apiKey);
        }
    }
}