/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package FlinkEcommerce;

import deserializer.JSONValueDeserializationSchema;
import dto.SalesPerCategory;
import dto.SalesPerDay;
import dto.SalesPerMonth;
import dto.Transaction;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.elasticsearch.sink.Elasticsearch7SinkBuilder;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.elasticsearch7.shaded.org.apache.http.HttpHost;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.action.index.IndexRequest;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.client.Requests;
import org.apache.flink.elasticsearch7.shaded.org.elasticsearch.common.xcontent.XContentType;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.RemoteStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import utils.JsonUtil;

import java.sql.Date;

public class DataStreamJob {

    private static final String jdbcUrl = "jdbc:postgresql://localhost:5432/postgres";
    private static final String username = "postgres";
    private static final String password = "postgres";
    private static final String serverHost = "localhost";
    private static final int serverPort = 6123;
    private static final String jarPath = "/Users/long.vk/Downloads/longvk/java/flink-ecommerce/target/FlinkEcommerce-1.0-SNAPSHOT.jar";

    public static void main(String[] args) throws Exception {
        // Sets up the execution environment, which is the main entry point
        // to building Flink applications.
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        RemoteStreamEnvironment env = new RemoteStreamEnvironment(serverHost, serverPort, jarPath);

        String topic = "financial_transactions";

        KafkaSource<Transaction> source = KafkaSource.<Transaction>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics(topic)
                .setGroupId("flink-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new JSONValueDeserializationSchema())
                .build();

        DataStream<Transaction> transactionDataStream =
                env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka source");

//        transactionDataStream.print();

        JdbcExecutionOptions executionOptions = new JdbcExecutionOptions.Builder()
                .withBatchSize(1000)
                .withBatchIntervalMs(200)
                .withMaxRetries(5)
                .build();

        JdbcConnectionOptions connectionOptions = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                .withUrl(jdbcUrl)
                .withDriverName("org.postgresql.Driver")
                .withUsername(username)
                .withPassword(password)
                .build();

//        // create transactions table
//        transactionDataStream.addSink(JdbcSink.sink(
//                "CREATE TABLE IF NOT EXISTS transactions (" +
//                        "transaction_id VARCHAR(255) PRIMARY KEY, " +
//                        "product_id VARCHAR(255), " +
//                        "product_name VARCHAR(255), " +
//                        "product_category VARCHAR(255), " +
//                        "product_price DOUBLE PRECISION, " +
//                        "product_quantity INTEGER, " +
//                        "product_brand VARCHAR(255), " +
//                        "total_amount DOUBLE PRECISION, " +
//                        "currency VARCHAR(255), " +
//                        "customer_id VARCHAR(255), " +
//                        "transaction_date TIMESTAMP, " +
//                        "payment_method VARCHAR(255) " +
//                        ")",
//                (JdbcStatementBuilder<Transaction>) (preparedStatement, transaction) -> {
//
//                },
//                executionOptions,
//                connectionOptions
//        )).name("Create Transactions table sink");
//
//        // create sales_per_category table sink
//        transactionDataStream.addSink(JdbcSink.sink(
//                "CREATE TABLE IF NOT EXISTS sales_per_category (" +
//                        "transaction_date DATE, " +
//                        "category VARCHAR(255), " +
//                        "total_sales DOUBLE PRECISION, " +
//                        "PRIMARY KEY (transaction_date, category) " +
//                        ")",
//                (JdbcStatementBuilder<Transaction>) (preparedStatement, transaction) -> {
//
//                },
//                executionOptions,
//                connectionOptions
//        )).name("Create Sales Per Category Table Sink");
//
//        // create sales_per_day table sink
//        transactionDataStream.addSink(JdbcSink.sink(
//                "CREATE TABLE IF NOT EXISTS sales_per_day (" +
//                        "transaction_date DATE PRIMARY KEY, " +
//                        "total_sales DOUBLE PRECISION " +
//                        ")",
//                (JdbcStatementBuilder<Transaction>) (preparedStatement, transaction) -> {
//
//                },
//                executionOptions,
//                connectionOptions
//        )).name("Create Sales Per Day table sink");
//
//        // create sales_per_month table sink
//        transactionDataStream.addSink(JdbcSink.sink(
//                "CREATE TABLE IF NOT EXISTS sales_per_month (" +
//                        "year INTEGER, " +
//                        "month INTEGER, " +
//                        "total_sales DOUBLE PRECISION, " +
//                        "PRIMARY KEY (month, year) " +
//                        ")",
//                (JdbcStatementBuilder<Transaction>) (preparedStatement, transaction) -> {
//
//                },
//                executionOptions,
//                connectionOptions
//        )).name("Create Sales Per Month table sink");
//
//        transactionDataStream.addSink(JdbcSink.sink(
//                "INSERT INTO transactions(transaction_id, product_id, product_name, product_category, product_price, " +
//                        "product_quantity, product_brand, total_amount, currency, customer_id, transaction_date, payment_method) " +
//                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
//                        "ON CONFLICT (transaction_id) DO UPDATE SET " +
//                        "product_id = EXCLUDED.product_id, " +
//                        "product_name = EXCLUDED.product_name, " +
//                        "product_category = EXCLUDED.product_category, " +
//                        "product_price = EXCLUDED.product_price, " +
//                        "product_quantity = EXCLUDED.product_quantity, " +
//                        "product_brand = EXCLUDED.product_brand, " +
//                        "total_amount = EXCLUDED.total_amount, " +
//                        "currency = EXCLUDED.currency, " +
//                        "customer_id = EXCLUDED.customer_id, " +
//                        "transaction_date = EXCLUDED.transaction_date, " +
//                        "payment_method = EXCLUDED.payment_method " +
//                        "WHERE transactions.transaction_id = EXCLUDED.transaction_id",
//                (JdbcStatementBuilder<Transaction>) (preparedStatement, transactions) -> {
//                    preparedStatement.setString(1, transactions.getTransactionId());
//                    preparedStatement.setString(2, transactions.getProductId());
//                    preparedStatement.setString(3, transactions.getProductName());
//                    preparedStatement.setString(4, transactions.getProductCategory());
//                    preparedStatement.setDouble(5, transactions.getProductPrice());
//                    preparedStatement.setInt(6, transactions.getProductQuantity());
//                    preparedStatement.setString(7, transactions.getProductBrand());
//                    preparedStatement.setDouble(8, transactions.getTotalAmount());
//                    preparedStatement.setString(9, transactions.getCurrency());
//                    preparedStatement.setString(10, transactions.getCustomerId());
//                    preparedStatement.setTimestamp(11, transactions.getTransactionDate());
//                    preparedStatement.setString(12, transactions.getPaymentMethod());
//                },
//                executionOptions,
//                connectionOptions
//        )).name("Insert into transactions table sink");
//
//        transactionDataStream.map(
//                transaction -> {
//                    Date transactionDate = new Date(System.currentTimeMillis());
//                    String category = transaction.getProductCategory();
//                    Double totalSales = transaction.getTotalAmount();
//                    return new SalesPerCategory(transactionDate, category, totalSales);
//                }
//        ).keyBy(SalesPerCategory::getCategory).reduce((salesPerCategory, t1) -> {
//            salesPerCategory.setTotalSales(salesPerCategory.getTotalSales() + t1.getTotalSales());
//            return salesPerCategory;
//        }).addSink(JdbcSink.sink(
//                "INSERT INTO sales_per_category(transaction_date, category, total_sales) " +
//                        "VALUES (?, ?, ?) " +
//                        "ON CONFLICT (transaction_date, category) DO UPDATE SET " +
//                        "total_sales = EXCLUDED.total_sales " +
//                        "WHERE sales_per_category.transaction_date = EXCLUDED.transaction_date " +
//                        "AND sales_per_category.category = EXCLUDED.category",
//                (JdbcStatementBuilder<SalesPerCategory>) (preparedStatement, salesPerCategory) -> {
//                   preparedStatement.setDate(1, new Date(System.currentTimeMillis()));
//                   preparedStatement.setString(2, salesPerCategory.getCategory());
//                   preparedStatement.setDouble(3, salesPerCategory.getTotalSales());
//                },
//                executionOptions,
//                connectionOptions
//        )).name("Insert into sales_per_category table");
//
//        transactionDataStream.map(
//                transaction -> {
//                    Date transactionDate = new Date(System.currentTimeMillis());
//                    Double totalSales = transaction.getTotalAmount();
//                    return new SalesPerDay(transactionDate, totalSales);
//                }
//        ).keyBy(SalesPerDay::getTransactionDate).reduce((salesPerDay, t1) -> {
//            salesPerDay.setTotalSales(salesPerDay.getTotalSales() + t1.getTotalSales());
//            return salesPerDay;
//        }).addSink(JdbcSink.sink(
//                "INSERT INTO sales_per_day(transaction_date, total_sales) " +
//                        "VALUES (?, ?) " +
//                        "ON CONFLICT (transaction_date) DO UPDATE SET " +
//                        "total_sales = EXCLUDED.total_sales " +
//                        "WHERE sales_per_day.transaction_date = EXCLUDED.transaction_date ",
//                (JdbcStatementBuilder<SalesPerDay>) (preparedStatement, salesPerCategory) -> {
//                    preparedStatement.setDate(1, new Date(System.currentTimeMillis()));
//                    preparedStatement.setDouble(2, salesPerCategory.getTotalSales());
//                },
//                executionOptions,
//                connectionOptions
//        )).name("Insert into sales_per_day table");
//
//        transactionDataStream.map(
//                transaction -> {
//                    Date transactionDate = new Date(System.currentTimeMillis());
//                    Integer year = transactionDate.toLocalDate().getYear();
//                    Integer month = transactionDate.toLocalDate().getMonth().getValue();
//                    Double totalSales = transaction.getTotalAmount();
//                    return new SalesPerMonth(year, month, totalSales);
//                }
//        ).keyBy(SalesPerMonth::getMonth).reduce((salesPerMonth, t1) -> {
//            salesPerMonth.setTotalSales(salesPerMonth.getTotalSales() + t1.getTotalSales());
//            return salesPerMonth;
//        }).addSink(JdbcSink.sink(
//                "INSERT INTO sales_per_month(year, month, total_sales) " +
//                        "VALUES (?, ?, ?) " +
//                        "ON CONFLICT (month, year) DO UPDATE SET " +
//                        "total_sales = EXCLUDED.total_sales " +
//                        "WHERE sales_per_month.year = EXCLUDED.year " +
//                        "AND sales_per_month.month = EXCLUDED.month",
//                (JdbcStatementBuilder<SalesPerMonth>) (preparedStatement, salesPerCategory) -> {
//                    Date transactionDate = new Date(System.currentTimeMillis());
//                    preparedStatement.setInt(1, transactionDate.toLocalDate().getYear());
//                    preparedStatement.setInt(2, transactionDate.toLocalDate().getMonth().getValue());
//                    preparedStatement.setDouble(3, salesPerCategory.getTotalSales());
//                },
//                executionOptions,
//                connectionOptions
//        )).name("Insert into sales_per_month table");

        transactionDataStream.sinkTo(
                new Elasticsearch7SinkBuilder<Transaction>()
                        .setHosts(new HttpHost("localhost", 9200, "http"))
                        .setBulkFlushMaxActions(1) // flush after every record
                        .setBulkFlushInterval(1000) // flush every second (in ms)
                        .setEmitter((transaction, runtimeContext, requestIndexer) -> {
                            String json = JsonUtil.convertTransactionToJson(transaction);
                            IndexRequest indexRequest = Requests.indexRequest()
                                    .index("transactions")
                                    .id(transaction.getTransactionId())
                                    .source(json, XContentType.JSON);
                            requestIndexer.add(indexRequest);
                        }).build()
        ).name("Elasticsearch Sink");

        // Execute program, beginning computation.
        env.execute("Flink Ecommerce Realtime Streaming");
    }
}
