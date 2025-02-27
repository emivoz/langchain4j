package dev.langchain4j.service;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.PdfFileContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.pdf.PdfFile;
import dev.langchain4j.exception.IllegalConfigurationException;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.mock.ChatModelMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.langchain4j.data.message.UserMessage.userMessage;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class AiServicesUserMessageConfigTest {

    @Spy
    ChatLanguageModel chatLanguageModel = ChatModelMock.thatAlwaysResponds("Berlin");

    private static final Image image = Image.builder().url("https://en.wikipedia.org/wiki/Llama#/media/File:Llamas,_Vernagt-Stausee,_Italy.jpg").build();
    private static final ImageContent imageContent = ImageContent.from(image);
    private static final PdfFile pdf = PdfFile.builder().url("https://upload.wikimedia.org/wikipedia/commons/2/20/Re_example.pdf").build();
    private static final PdfFileContent pdfFileContent = PdfFileContent.from(pdf);

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(chatLanguageModel);
    }

    interface AiService {

        String chat1(String userMessage);

        String chat2(@UserMessage String userMessage);

        String chat3(@UserMessage String userMessage, @V("country") String country);

        @UserMessage("What is the capital of Germany?")
        String chat4();

        @UserMessage("What is the capital of {{it}}?")
        String chat5(String country);

        @UserMessage("What is the capital of {{country}}?")
        String chat6(@V("country") String country);

        @UserMessage("What is the {{it}} of {{country}}?")
        String chat7(@V("it") String it, @V("country") String country);

        @UserMessage("Count the number of cars in this image")
        String chat8(@UserMessage ImageContent imageContent);

        @UserMessage("Count the number of cars in this image")
        String chat9(@UserMessage Image image);

        @UserMessage("Count the number of cars in these two images")
        String chat10(@UserMessage ImageContent imageContent, @UserMessage Image image);

        @UserMessage("Count the number of {{color}} cars in this image")
        String chat11(@UserMessage ImageContent imageContent, @V("color") String color);

        String chat12(Image image);

        String chat13(@UserMessage String userMessage, @UserMessage Image image);

        String chat14(@UserMessage Image image, @UserMessage String userMessage);

        String chat15(@UserMessage PdfFile pdfFile, @UserMessage String userMessage);

        // illegal configuration

        String illegalChat1();

        String illegalChat2(@V("country") String country);

        String illegalChat3(String userMessage, String country);

        String illegalChat4(@UserMessage String userMessage, String country);

        @UserMessage
        String illegalChat5();

        @UserMessage("Hello")
        String illegalChat6(@UserMessage String userMessage);


        // TODO more tests with @UserName, @V, @MemoryId
    }

    @Test
    void test_user_message_configuration_1() {

        // given
        AiService aiService = AiServices.builder(AiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // when-then
        assertThat(aiService.chat1("What is the capital of Germany?"))
                .containsIgnoringCase("Berlin");
        verify(chatLanguageModel).generate(singletonList(userMessage("What is the capital of Germany?")));
        verify(chatLanguageModel).supportedCapabilities();
    }

    @Test
    void test_user_message_configuration_2() {

        // given
        AiService aiService = AiServices.builder(AiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // when-then
        assertThat(aiService.chat2("What is the capital of Germany?"))
                .containsIgnoringCase("Berlin");
        verify(chatLanguageModel).generate(singletonList(userMessage("What is the capital of Germany?")));
        verify(chatLanguageModel).supportedCapabilities();
    }

    @Test
    void test_user_message_configuration_3() {

        // given
        AiService aiService = AiServices.builder(AiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // when-then
        assertThat(aiService.chat3("What is the capital of {{country}}?", "Germany"))
                .containsIgnoringCase("Berlin");
        verify(chatLanguageModel).generate(singletonList(userMessage("What is the capital of Germany?")));
        verify(chatLanguageModel).supportedCapabilities();
    }

    @Test
    void test_user_message_configuration_4() {

        // given
        AiService aiService = AiServices.builder(AiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // when-then
        assertThat(aiService.chat4())
                .containsIgnoringCase("Berlin");
        verify(chatLanguageModel).generate(singletonList(userMessage("What is the capital of Germany?")));
        verify(chatLanguageModel).supportedCapabilities();
    }

    @Test
    void test_user_message_configuration_5() {

        // given
        AiService aiService = AiServices.builder(AiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // when-then
        assertThat(aiService.chat5("Germany"))
                .containsIgnoringCase("Berlin");
        verify(chatLanguageModel).generate(singletonList(userMessage("What is the capital of Germany?")));
        verify(chatLanguageModel).supportedCapabilities();
    }

    @Test
    void test_user_message_configuration_6() {

        // given
        AiService aiService = AiServices.builder(AiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // when-then
        assertThat(aiService.chat6("Germany"))
                .containsIgnoringCase("Berlin");
        verify(chatLanguageModel).generate(singletonList(userMessage("What is the capital of Germany?")));
        verify(chatLanguageModel).supportedCapabilities();
    }

    @Test
    void test_user_message_configuration_7() {

        // given
        AiService aiService = AiServices.builder(AiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // when-then
        assertThat(aiService.chat7("capital", "Germany"))
                .containsIgnoringCase("Berlin");
        verify(chatLanguageModel).generate(singletonList(userMessage("What is the capital of Germany?")));
        verify(chatLanguageModel).supportedCapabilities();
    }

    @Test
    void test_user_message_configuration_8() {

        // given
        AiService aiService = AiServices.builder(AiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // when-then
        assertThat(aiService.chat8(imageContent))
                .containsIgnoringCase("Berlin");
        verify(chatLanguageModel).generate(
                singletonList(userMessage(
                        Stream.of(
                                TextContent.from("Count the number of cars in this image"),
                                imageContent
                        ).collect(Collectors.toList())
                ))
        );

        verify(chatLanguageModel).supportedCapabilities();
    }

    @Test
    void test_user_message_configuration_9() {

        // given
        AiService aiService = AiServices.builder(AiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // when-then
        assertThat(aiService.chat9(image))
                .containsIgnoringCase("Berlin");
        verify(chatLanguageModel).generate(
                singletonList(userMessage(
                        Stream.of(
                                TextContent.from("Count the number of cars in this image"),
                                imageContent
                        ).collect(Collectors.toList())
                ))
        );

        verify(chatLanguageModel).supportedCapabilities();
    }

    @Test
    void test_user_message_configuration_10() {

        // given
        AiService aiService = AiServices.builder(AiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // when-then
        assertThat(aiService.chat10(imageContent, image))
                .containsIgnoringCase("Berlin");
        verify(chatLanguageModel).generate(
                singletonList(userMessage(
                        Stream.of(
                                TextContent.from("Count the number of cars in these two images"),
                                imageContent,
                                imageContent
                        ).collect(Collectors.toList())
                ))
        );

        verify(chatLanguageModel).supportedCapabilities();
    }

    @Test
    void test_user_message_configuration_11() {

        // given
        AiService aiService = AiServices.builder(AiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // when-then
        assertThat(aiService.chat11(imageContent, "red"))
                .containsIgnoringCase("Berlin");
        verify(chatLanguageModel).generate(
                singletonList(userMessage(
                        Stream.of(
                                TextContent.from("Count the number of red cars in this image"),
                                imageContent
                        ).collect(Collectors.toList())
                ))
        );

        verify(chatLanguageModel).supportedCapabilities();
    }

    @Test
    void test_user_message_configuration_12() {

        // given
        AiService aiService = AiServices.builder(AiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // when-then
        assertThat(aiService.chat12(image))
                .containsIgnoringCase("Berlin");
        verify(chatLanguageModel).generate(singletonList(userMessage(imageContent)));
        verify(chatLanguageModel).supportedCapabilities();

    }

    @Test
    void test_user_message_configuration_13() {

        // given
        AiService aiService = AiServices.builder(AiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // when-then
        assertThat(aiService.chat13("What's on this image?", image))
                .containsIgnoringCase("Berlin");
        verify(chatLanguageModel).generate(singletonList(userMessage(
                TextContent.from("What's on this image?"),
                imageContent
        )));

        verify(chatLanguageModel).supportedCapabilities();

    }

    @Test
    void test_user_message_configuration_14() {

        // given
        AiService aiService = AiServices.builder(AiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // when-then
        assertThat(aiService.chat14(image, "What's on this image?"))
                .containsIgnoringCase("Berlin");
        verify(chatLanguageModel).generate(singletonList(userMessage(
                TextContent.from("What's on this image?"),
                imageContent
        )));

        verify(chatLanguageModel).supportedCapabilities();
    }

    @Test
    void test_user_message_configuration_15() {

        // given
        AiService aiService = AiServices.builder(AiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // when-then
        assertThat(aiService.chat15(pdf, "What's on this PDF?"))
                .containsIgnoringCase("Berlin");
        verify(chatLanguageModel).generate(singletonList(userMessage(
                TextContent.from("What's on this PDF?"),
                pdfFileContent
        )));

        verify(chatLanguageModel).supportedCapabilities();
    }

    @Test
    void test_illegal_user_message_configuration_1() {

        // given
        AiService aiService = AiServices.builder(AiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // when-then
        assertThatThrownBy(aiService::illegalChat1)
                .isExactlyInstanceOf(IllegalConfigurationException.class)
                .hasMessage("Error: The method 'illegalChat1' does not have a user message defined.");
    }

    @Test
    void test_illegal_user_message_configuration_2() {

        // given
        AiService aiService = AiServices.builder(AiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // when-then
        assertThatThrownBy(() -> aiService.illegalChat2("Germany"))
                .isExactlyInstanceOf(IllegalConfigurationException.class)
                .hasMessage("Error: The method 'illegalChat2' does not have a user message defined.");
    }

    @Test
    void test_illegal_user_message_configuration_3() {

        // given
        AiService aiService = AiServices.builder(AiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // when-then
        assertThatThrownBy(() -> aiService.illegalChat3("What is the capital of {{it}}?", "Germany"))
                .isExactlyInstanceOf(IllegalConfigurationException.class)
                .hasMessage("Parameter 'arg0' of method 'illegalChat3' should be annotated " +
                        "with @V or @UserMessage or @UserName or @MemoryId");
    }

    @Test
    void test_illegal_user_message_configuration_4() {

        // given
        AiService aiService = AiServices.builder(AiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // when-then
        assertThatThrownBy(() -> aiService.illegalChat4("What is the capital of {{it}}?", "Germany"))
                .isExactlyInstanceOf(IllegalConfigurationException.class)
                .hasMessage("Parameter 'arg1' of method 'illegalChat4' should be annotated " +
                        "with @V or @UserMessage or @UserName or @MemoryId");
    }

    @Test
    void test_illegal_user_message_configuration_5() {

        // given
        AiService aiService = AiServices.builder(AiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // when-then
        assertThatThrownBy(aiService::illegalChat5)
                .isExactlyInstanceOf(IllegalConfigurationException.class)
                .hasMessage("@UserMessage's template cannot be empty");
    }

    @Test
    void test_illegal_user_message_configuration_6() {

        // given
        AiService aiService = AiServices.builder(AiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // when-then
        assertThatThrownBy(() -> aiService.illegalChat6("Hello"))
                .isExactlyInstanceOf(IllegalConfigurationException.class)
                .hasMessage("Error: The method 'illegalChat6' has multiple @UserMessage annotations. Please use only one.");
    }
}
