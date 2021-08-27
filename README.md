# MsBotEva

This connector has been created using [Bot Framework](https://dev.botframework.com), it shows how to incorporate eVA conversational flow.

This application is a Spring Boot app and uses the Azure CLI and azure-webapp Maven plugin to deploy to Azure..

## Prerequisites

- Java 1.8+
- Install [Maven](https://maven.apache.org/)
- An account on [Azure](https://azure.microsoft.com) if you want to deploy to Azure.

## To try this sample locally
- From the root of this project folder:
  - Build the sample using `mvn package`
  - Run it by using `java -jar .\target\eva-ms-bot-service-3.1.jar`

- Test the bot using Bot Framework Emulator

  [Bot Framework Emulator](https://github.com/microsoft/botframework-emulator) is a desktop application that allows bot developers to test and debug their bots on localhost or running remotely through a tunnel.

  - Install the Bot Framework Emulator version 4.3.0 or greater from [here](https://github.com/Microsoft/BotFramework-Emulator/releases)

  - Connect to the bot using Bot Framework Emulator

    - Launch Bot Framework Emulator
    - File -> Open Bot
    - Enter a Bot URL of `http://localhost:8080/api/messages`

## Deploy the bot to Azure

As described on [Deploy your bot](https://docs.microsoft.com/en-us/azure/bot-service/bot-builder-deploy-az-cli), you will perform the first 4 steps to setup the Azure app, then deploy the code using the azure-webapp Maven plugin.

### 1. Login to Azure
From a command (or PowerShell) prompt in the root of the bot folder, execute:  
`az login`
  
### 2. Set the subscription
`az account set --subscription "<azure-subscription>"`

example:

`az account set --subscription b89530ad-6252-431e-b90c-d1913fead39c`

If you aren't sure which subscription to use for deploying the bot, you can view the list of subscriptions for your account by using `az account list` command. 

### 3. Create an App registration
`az ad app create --display-name "<botname>" --password "<appsecret>" --available-to-other-tenants`

Replace `<botname>` and `<appsecret>` with your own values.

`<botname>` is the unique name of your bot.  
`<appsecret>` is a minimum 16 character password for your bot. 

Record the `appid` from the returned JSON

Example:

az ad app create --display-name "ms-bot-eva" --password "6a25i79r-c62a-4cdc-98b3-9cb4185fc565" --available-to-other-tenants

### 4. Create the Azure resources
Replace the values for `<appid>`, `<appsecret>`, `<botname>`, and `<groupname>` in the following commands:

#### To a new Resource Group
`az deployment create --name "MsBotEvaDeploy" --location "westus" --template-file ".\deploymentTemplates\template-with-new-rg.json" --parameters groupName="<groupname>" botId="<botname>" appId="<appid>" appSecret="<appsecret>"`

#### To an existing Resource Group
`az group deployment create --name "MsBotEvaDeploy" --resource-group "<groupname>" --template-file ".\deploymentTemplates\template-with-preexisting-rg.json" --parameters botId="<botname>" appId="<appid>" appSecret="<appsecret>"`

Example:
az group deployment create --resource-group "EVA" --template-file ".\deploymentTemplates\template-with-preexisting-rg.json" --parameters appId="2bc8c8c1-39c6-427a-a92e-120c26c42160" appSecret="6a25i79r-c62a-4cdc-98b3-9cb4185fc565" botId="ms-bot-eva" newWebAppName="ms-bot-eva" existingAppServicePlan="ServicePlanba31bd6a-bb4d" appServicePlanLocation="Central US" --name "ms-bot-eva"

#### Issues
Issue:

'The specified app service plan was not found.'

Fix:

In template-with-preexisting-rg.json line 108, replace:

`"serverFarmId": "[variables('servicePlanName')]",`

by complete servicePlan:

`"serverFarmId": "/subscriptions/b89530ad-6252-431e-b90c-d1913fead39c/resourceGroups/EVA/providers/Microsoft.Web/serverfarms/ServicePlanba31bd6a-bb4d",`

### 5. Update the pom.xml
In pom.xml update the following nodes under azure-webapp-maven-plugin
- `resourceGroup` using the `<groupname>` used above
- `appName` using the `<botname>` used above

#### Issues
Issue:

Plugin azure-webapp-maven-plugin version 1.7.0 auto updates App Service Plan Sku to Premium

Fix:

Change to plugin azure-webapp-maven-plugin version 1.6.0

### 6. Update app id and password
In src/main/resources/application.properties update 
  - `MicrosoftAppPassword` with the botsecret value
  - `MicrosoftAppId` with the appid from the first step

### 7. Deploy the code
- Execute `mvn clean package` 
- Execute `mvn azure-webapp:deploy`

If the deployment is successful, you will be able to test it via "Test in Web Chat" from the Azure Portal using the "Bot Channel Registration" for the bot.

After the bot is deployed, you only need to execute #7 if you make changes to the bot.

### 8. Teams channel
#### 8.1 Add Teams to Bot Channels Registration

Open the created Bot Channels Registration resource.

Navigate to Bot management > Channels.

Add Microsoft Teams Channel.

#### 8.2 Add Bot Application to Teams

Edit the manifest.json contained in the teamsAppManifest folder to replace your Microsoft App Id (that was created when you registered your bot earlier) everywhere you see the place holder string <<YOUR-MICROSOFT-APP-ID>> (depending on the scenario the Microsoft App Id may occur multiple times in the manifest.json).

Zip up the contents of the teamsAppManifest folder to create a manifest.zip.

Upload the manifest.zip to Teams (in the Apps view click "Upload a custom app").

A new teams app manifest can be easily build with App Studio directly in Microsoft Teams apps.

With App Studio you can create and test a new App with Bot capabilities to set up a bot to include it in your app experience.

## Further reading

- [Maven Plugin for Azure App Service](https://docs.microsoft.com/en-us/java/api/overview/azure/maven/azure-webapp-maven-plugin/readme?view=azure-java-stable)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Azure for Java cloud developers](https://docs.microsoft.com/en-us/azure/java/?view=azure-java-stable)
- [Bot Framework Documentation](https://docs.botframework.com)
- [Bot Basics](https://docs.microsoft.com/azure/bot-service/bot-builder-basics?view=azure-bot-service-4.0)
- [Activity processing](https://docs.microsoft.com/en-us/azure/bot-service/bot-builder-concept-activity-processing?view=azure-bot-service-4.0)
- [Azure Bot Service Introduction](https://docs.microsoft.com/azure/bot-service/bot-service-overview-introduction?view=azure-bot-service-4.0)
- [Azure Bot Service Documentation](https://docs.microsoft.com/azure/bot-service/?view=azure-bot-service-4.0)
