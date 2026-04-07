import java.io.*;
import java.util.Scanner;

public class Main {

    //read arguments from command line and validate
    private static boolean validateArg(String[] args){
        if(args.length == 0 || !new File(args[0]).exists() || !args[0].endsWith(".txt")){
            System.err.println("usage: java Main <path.txt>"); //print the actual error
            return false;
        }
        return true;
    }

    private static String readFile(String path){


        //create a string builder to hold the content of the file
      StringBuilder content = new StringBuilder();
      try (BufferedReader reader = new BufferedReader(new FileReader(path))){

          String line;
          while((line = reader.readLine()) != null){
              content.append(line).append("\n");
          }

      } catch (IOException e){
            System.err.println("error reading file" + e.getMessage());
      }



        return content.toString();
    }

    private static String askLanguage(Scanner scanner){

        System.out.println("enter the language to translate:");
        String language = scanner.nextLine();
        return language;


    }

    //build payload to return json body as a string
    private static String buildPayload(String fileContent, String language){
        //escape the language and file content to include in json body
        String escapedLanguage = escapeJson(language);
        String escapedFileContent = escapeJson(fileContent);

        return "{" +
                "\"model\":\"compound-beta-mini\"," +
                "\"messages\":[" +
                "{\"role\":\"system\",\"content\":\"you are a translator, translate to " + escapedLanguage + " return only the translated text.\"}," +
                "{\"role\":\"user\",\"content\":\"translate the following text to " + escapedLanguage + ":\\n\\n" + escapedFileContent + "\"}" +
                "]" +
                "}";
    }

    //escape special characters
    private static String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    //hit the api with the payload and return response as a string
    private  static String callAPI(String payload, String apiKey){
        //pass the payload using data and wrap the curl command through the shell
        ProcessBuilder pb = new ProcessBuilder(
                "sh", "-c",
                "curl -s -X POST https://api.groq.com/openai/v1/chat/completions " +
                        "-H 'Content-Type: application/json' " +
                        "-H 'Authorization: Bearer " + apiKey + "' " +
                        "--data '" + payload + "'"
        );
        try{
            Process process = pb.start();
            pb.redirectErrorStream(true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("api call failed with exit code " + exitCode);
                return "";
            }
            System.out.println("RAW RESPONSE: " + response.toString());
            return response.toString();
        } catch (IOException | InterruptedException e) {
            System.err.println("error calling api  " + e.getMessage());
            return "";
        }
    }

    //receive the api response and return the translated text
    private static String getTranslation(String response) {
        // find the last content field — that's always the assistant's reply
        int startIndex = response.lastIndexOf("\"content\":\"");
        if (startIndex == -1) {
            System.err.println("unexpected api response format");
            return "";
        }

        startIndex += "\"content\":\"".length();

        int endIndex = response.indexOf("\"", startIndex);
        if (endIndex == -1) {
            System.err.println("unexpected api response format");
            return "";
        }

        // unescape special characters back to readable text
        return response.substring(startIndex, endIndex)
                .replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }


    private static void writeFile (String content ,String outputPath){
        File file = new File(outputPath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        } catch (IOException e) {
            System.err.println("error writing file: " + e.getMessage());
        }

    }


    public static void main(String[] args) {

        //initialize and validate api key
        String apiKey = System.getenv("OpenAIToken");
        if(apiKey == null || apiKey.isBlank()){
            System.err.println("missimg api key");
            return;
        }

        if(!validateArg(args)) return ;

        String content = readFile(args[0]);
        Scanner scanner = new Scanner(System.in);

        String language = askLanguage(scanner);
        String payload = buildPayload(content, language);
        String response = callAPI(payload,apiKey );
        String translatedText = getTranslation(response);
        writeFile(translatedText, "test-output.txt");



    }



}
