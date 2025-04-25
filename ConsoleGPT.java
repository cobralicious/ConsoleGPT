package com.alex;

import java.util.Scanner;
import okhttp3.*;
import com.google.gson.*;

public class ConsoleGPT {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        OkHttpClient client = new OkHttpClient();

        String apiKey = System.getenv("OPENROUTER_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("❌ API key not found. Set OPENROUTER_API_KEY as an environment variable.");
            return;
        }

        System.out.println(">>> Hi, I'm AI. Write your question. Write 'exit' to finish.");

        while (true) {
            System.out.print("You: ");
            String userInput = scanner.nextLine();

            if (userInput.equalsIgnoreCase("exit")) break;

            String requestBody = "{\n" +
                    "  \"model\": \"mistralai/mistral-7b-instruct:free\",\n" +
                    "  \"messages\": [\n" +
                    "    {\"role\": \"user\", \"content\": \"" + userInput + "\"}\n" +
                    "  ]\n" +
                    "}";

            Request request = new Request.Builder()
                    .url("https://openrouter.ai/api/v1/chat/completions")
                    .post(RequestBody.create(
                            requestBody,
                            MediaType.parse("application/json")
                    ))
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("HTTP-Referer", "https://your-app-name.com")
                    .addHeader("Content-Type", "application/json")
                    .build();

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
            String reply = json.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();

            System.out.println("GPT: " + reply.trim());
        }

        scanner.close();
    }
}
