package com.remag.sbmb.multiblock;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class MultiblockRecipeSerializer implements RecipeSerializer<MultiblockRecipe> {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public MultiblockRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        // 🔍 Detect and decompress if pattern_compressed exists
        if (json.has("pattern_compressed")) {
            try {
                String compressed = json.get("pattern_compressed").getAsString();
                String decompressed = decompressBase64Gzip(compressed);
                JsonArray patternArray = JsonParser.parseString(decompressed).getAsJsonArray();
                json.add("pattern", patternArray);
                json.remove("pattern_compressed");
            } catch (IOException e) {
                throw new RuntimeException("Failed to decompress pattern_compressed for recipe " + recipeId, e);
            }
        }

        // Standard JSON decoding
        DataResult<MultiblockRecipe.PartialMultiblockRecipe> result =
                MultiblockRecipe.PartialMultiblockRecipe.PARTIAL_CODEC.parse(JsonOps.INSTANCE, json);
        MultiblockRecipe.PartialMultiblockRecipe partial = result.getOrThrow(false, error -> {
            throw new RuntimeException("Failed to decode multiblock recipe json: " + error);
        });

        return new MultiblockRecipe(recipeId, partial.size(), partial.pattern(), partial.result(), partial.count());
    }

    @Override
    public MultiblockRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        try {
            int length = buffer.readVarInt();
            byte[] compressedData = buffer.readByteArray(length);
            String jsonString = decompressGzip(compressedData);

            MultiblockRecipe.PartialMultiblockRecipe partial =
                    GSON.fromJson(jsonString, MultiblockRecipe.PartialMultiblockRecipe.class);

            return new MultiblockRecipe(recipeId, partial.size(), partial.pattern(), partial.result(), partial.count());
        } catch (IOException e) {
            throw new RuntimeException("Failed to decompress multiblock recipe from network", e);
        }
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, MultiblockRecipe recipe) {
        String jsonString = GSON.toJson(recipe);
        try {
            byte[] compressed = compressGzip(jsonString);
            buffer.writeVarInt(compressed.length);
            buffer.writeByteArray(compressed);
        } catch (IOException e) {
            throw new RuntimeException("Failed to compress multiblock recipe for network", e);
        }
    }

    // --- Compression helpers ---

    private static String decompressBase64Gzip(String base64) throws IOException {
        byte[] compressed = Base64.getDecoder().decode(base64);
        return decompressGzip(compressed);
    }

    private static byte[] compressGzip(String str) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(str.getBytes(StandardCharsets.UTF_8));
        }
        return out.toByteArray();
    }

    private static String decompressGzip(byte[] data) throws IOException {
        try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data));
             InputStreamReader reader = new InputStreamReader(gzip, StandardCharsets.UTF_8);
             BufferedReader in = new BufferedReader(reader)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
}