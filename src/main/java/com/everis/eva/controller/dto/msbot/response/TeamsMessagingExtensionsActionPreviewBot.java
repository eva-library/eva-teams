package com.everis.eva.controller.dto.msbot.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;


import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.Attachment;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * This class implements the functionality of the Bot.
 *
 * <p>This is where application specific logic for interacting with the users would be
 * added.  This sample shows how to create a simple card based on
 * parameters entered by the user from a Task Module.
 * </p>
 */
@Component
public class TeamsMessagingExtensionsActionPreviewBot {

	/*
    @Override
    protected CompletableFuture<Void> onMessageActivity(
        TurnContext turnContext) {
        if (turnContext.getActivity().getValue() != null) {
            // This was a message from the card.
            LinkedHashMap obj = (LinkedHashMap) turnContext.getActivity().getValue();
            String answer = (String) obj.get("Answer");
            String choices = (String) obj.get("Choices");
         return turnContext.sendActivity(
             MessageFactory.text(
                 String.format("%1$s answered '%2$s' and chose '%3$s",
                     turnContext.getActivity().getFrom().getName(),
                     answer,
                     choices)))
             .thenApply(resourceResponse -> null);
        }

        // This is a regular text message.
        return turnContext.sendActivity(MessageFactory.text("Hello from the TeamsMessagingExtensionsActionPreviewBot."))
            .thenApply(resourceResponse -> null);
    }
*/
/*  
	@Override
    protected CompletableFuture<MessagingExtensionActionResponse> onTeamsMessagingExtensionFetchTask(
        TurnContext turnContext,
        MessagingExtensionAction action) {

        Attachment adaptiveCardEditor = getAdaptiveCardAttachment("adaptiveCardEditor.json");

        return CompletableFuture.completedFuture(
            new MessagingExtensionActionResponse(){{
                setTask(new TaskModuleContinueResponse(){{
                    setValue(new TaskModuleTaskInfo(){{
                        setCard(adaptiveCardEditor);
                        setWidth(500);
                        setHeight(450);
                        setTitle("Task Module Fetch Example");
                    }});
                    setType("continue");
                }});
            }});
    }

    @Override
    protected CompletableFuture<MessagingExtensionActionResponse> onTeamsMessagingExtensionSubmitAction(
        TurnContext turnContext,
        MessagingExtensionAction action) {

        Attachment adaptiveCard = getAdaptiveCardAttachment("submitCard.json");

        updateAttachmentAdaptiveCard(adaptiveCard, action);

        return CompletableFuture.completedFuture(new MessagingExtensionActionResponse(){{
            setComposeExtension(new MessagingExtensionResult(){{
                setType("botMessagePreview");
                setActivityPreview(MessageFactory.attachment(adaptiveCard));
            }});
        }});
    }
*/
/*
    @Override
    protected CompletableFuture<MessagingExtensionActionResponse> onTeamsMessagingExtensionBotMessagePreviewEdit(
        TurnContext turnContext,
        MessagingExtensionAction action) {

        // This is a preview edit call and so this time we want to re-create the adaptive card editor.
        Attachment adaptiveCard = getAdaptiveCardAttachment("adaptiveCardEditor.json");

        Activity preview = action.getBotActivityPreview().get(0);
        Attachment previewCard = preview.getAttachments().get(0);
        updateAttachmentAdaptiveCardEdit(adaptiveCard, previewCard);

        return CompletableFuture.completedFuture(new MessagingExtensionActionResponse(){{
            setTask(new TaskModuleContinueResponse(){{
                setValue(new TaskModuleTaskInfo(){{
                    setCard(adaptiveCard);
                    setHeight(450);
                    setWidth(500);
                    setTitle("Task Module Fetch Example");
                }});
                setType("continue");
            }});
        }});
    }
*/
/*	
    @Override
    protected CompletableFuture<MessagingExtensionActionResponse> onTeamsMessagingExtensionBotMessagePreviewSend(
        TurnContext turnContext,
        MessagingExtensionAction action) {
        // The data has been returned to the bot in the action structure.

        Activity preview = action.getBotActivityPreview().get(0);
        Attachment previewCard = preview.getAttachments().get(0);

        Activity message = MessageFactory.attachment(previewCard);

        return turnContext.sendActivity(message)
            .thenApply(resourceResponse -> null);
    }
*/
	/*
    @Override
    protected CompletableFuture<Void> onTeamsMessagingExtensionCardButtonClicked(
        TurnContext turnContext,
        Object cardData) {
        // If the adaptive card was added to the compose window (by either the OnTeamsMessagingExtensionSubmitActionAsync or
        // OnTeamsMessagingExtensionBotMessagePreviewSendAsync handler's return values) the submit values will come in here.
        Activity reply = MessageFactory.text("OnTeamsMessagingExtensionCardButtonClickedAsync");
        return turnContext.sendActivity(reply)
            .thenApply(resourceResponse -> null);
    }
*/
    public Attachment getAdaptiveCardAttachment(String fileName) {
        try {
            InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(fileName);
            String content = IOUtils.toString(input, StandardCharsets.UTF_8);

            return new Attachment(){{
                setContentType("application/vnd.microsoft.card.adaptive");
                setContent(new ObjectMapper().readValue(content, AdaptiveCard.class));
            }};
        } catch (IOException e) {
            LoggerFactory.getLogger(TeamsMessagingExtensionsActionPreviewBot.class)
                .error("getAdaptiveCardAttachment", e);
        }
        return new Attachment();
    }

  
}

