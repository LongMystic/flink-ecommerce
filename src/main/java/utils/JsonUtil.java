package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.Transaction;


public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String convertTransactionToJson(Transaction transaction) {
        try {
            return objectMapper.writeValueAsString(transaction);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
