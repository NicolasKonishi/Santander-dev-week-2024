package me.dio.sdw24.adapters.out;

import feign.FeignException;
import feign.RequestInterceptor;
import me.dio.sdw24.domain.ports.GenerativeAiService;
import org.springdoc.core.service.OpenAPIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.http.HttpHeaders;
import java.util.List;

@ConditionalOnProperty(name = "generative-ai.provider", havingValue = "OPENAI")

@FeignClient(name = "openAiChatApi", url ="${openai.base-url}", configuration = OpenAiChatApi.Config.class)
public interface OpenAiChatApi extends GenerativeAiService {

    @PostMapping("/v1/chat/completions")
    OpenAiChatCompletionResponsive chatCompletion(OpenAiChatCompletionRequest req);
    @Override
    default String generateContent(String objective, String context){
        String model = "gpt-3.5-turbo";
        List<Message> messages = List.of(
                new Message("system", objective),
                new Message("user", context)
        );
        OpenAiChatCompletionRequest req = new OpenAiChatCompletionRequest(model, messages);
        try {
            OpenAiChatCompletionResponsive resp = chatCompletion(req);
            return resp.choices().getFirst().message().content();
        }catch (FeignException httpErrors){
            return "Erro de comunicão com a API da OpenAi :(";
        }catch (Exception unexpectedError){
            return "O retorno da API da OpenAi não contem os dados esperados :0";
        }
    }
    record OpenAiChatCompletionRequest(String model, List<Message> messages){}
    record Message(String role, String content){}
    record OpenAiChatCompletionResponsive(List<Choice> choices){}
    record Choice(Message message){}

    class Config {
        @Bean
        public RequestInterceptor apiKeyRequestInterception(@Value("${openai.api-key}") String apiKey){
            return requestTemplate -> requestTemplate.header(org.springframework.http.HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(apiKey));
        }
    }
}