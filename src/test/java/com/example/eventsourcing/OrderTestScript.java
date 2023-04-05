package com.example.eventsourcing;

import com.example.eventsourcing.config.KafkaTopicsConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.test.json.BasicJsonTester;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
@Slf4j
public class OrderTestScript {

    private static final HttpHeaders HEADERS = new HttpHeaders();

    static {
        HEADERS.setContentType(MediaType.APPLICATION_JSON);
        HEADERS.setAccept(List.of(MediaType.APPLICATION_JSON));
    }

    private final TestRestTemplate restTemplate;
    private final String kafkaBrokers;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BasicJsonTester jsonTester = new BasicJsonTester(getClass());

    @SneakyThrows
    public void execute() {
        log.info("Place a new order");
        UUID orderId = placeOrder("""
                {
                  "riderId":"63770803-38f4-4594-aec2-4c74918f7165",
                  "price":"123.45",
                  "route":[
                    {
                      "address":"Kyiv, 17A Polyarna Street",
                      "lat":50.51980052414157,
                      "lon":30.467197278948536
                    },
                    {
                      "address":"Kyiv, 18V Novokostyantynivska Street",
                      "lat":50.48509161169076,
                      "lon":30.485170724431292
                    }
                  ]
                }
                """);

        log.info("Get the placed order");
        getOrder(orderId, """
                {
                  "id":"%s",
                  "version":1,
                  "status":"PLACED",
                  "riderId":"63770803-38f4-4594-aec2-4c74918f7165",
                  "price":123.45,
                  "route":[
                    {
                      "address":"Kyiv, 17A Polyarna Street",
                      "lat":50.51980052414157,
                      "lon":30.467197278948536
                    },
                    {
                      "address":"Kyiv, 18V Novokostyantynivska Street",
                      "lat":50.48509161169076,
                      "lon":30.485170724431292
                    }
                  ]
                }
                """.formatted(orderId));

        var price = new BigDecimal("100.00");
        for (int i = 0; i < 20; i++) {
            price = price.add(new BigDecimal("10"));
            log.info("Adjust the order");
            modifyOrder(orderId, """
                    {
                      "status":"ADJUSTED",
                      "price":"%s"
                    }
                    """.formatted(price));
        }

        log.info("Get the adjusted order");
        getOrder(orderId, """
                {
                  "id":"%s",
                  "version":21,
                  "status":"ADJUSTED",
                  "riderId":"63770803-38f4-4594-aec2-4c74918f7165",
                  "price":300.00,
                  "route":[
                    {
                      "address":"Kyiv, 17A Polyarna Street",
                      "lat":50.51980052414157,
                      "lon":30.467197278948536
                    },
                    {
                      "address":"Kyiv, 18V Novokostyantynivska Street",
                      "lat":50.48509161169076,
                      "lon":30.485170724431292
                    }
                  ]
                }
                """.formatted(orderId));

        log.info("Accepted the order");
        modifyOrder(orderId, """
                {
                  "status":"ACCEPTED",
                  "driverId":"2c068a1a-9263-433f-a70b-067d51b98378"
                }
                """);

        log.info("Get the accepted order");
        getOrder(orderId, """
                {
                  "id":"%s",
                  "version":22,
                  "status":"ACCEPTED",
                  "riderId":"63770803-38f4-4594-aec2-4c74918f7165",
                  "price":300.00,
                  "route":[
                    {
                      "address":"Kyiv, 17A Polyarna Street",
                      "lat":50.51980052414157,
                      "lon":30.467197278948536
                    },
                    {
                      "address":"Kyiv, 18V Novokostyantynivska Street",
                      "lat":50.48509161169076,
                      "lon":30.485170724431292
                    }
                  ],
                  "driverId":"2c068a1a-9263-433f-a70b-067d51b98378"
                }
                """.formatted(orderId));

        log.info("Complete the order");
        modifyOrder(orderId, """
                {
                  "status":"COMPLETED"
                }
                """);

        log.info("Get the completed order");
        getOrder(orderId, """
                {
                  "id":"%s",
                  "version":23,
                  "status":"COMPLETED",
                  "riderId":"63770803-38f4-4594-aec2-4c74918f7165",
                  "price":300.00,
                  "route":[
                    {
                      "address":"Kyiv, 17A Polyarna Street",
                      "lat":50.51980052414157,
                      "lon":30.467197278948536
                    },
                    {
                      "address":"Kyiv, 18V Novokostyantynivska Street",
                      "lat":50.48509161169076,
                      "lon":30.485170724431292
                    }
                  ],
                  "driverId":"2c068a1a-9263-433f-a70b-067d51b98378"
                }
                """.formatted(orderId));

        log.info("Try to cancel the completed order");
        modifyOrderError(orderId, """
                {
                  "status":"CANCELLED"
                }
                """, "Order in status COMPLETED can't be cancelled");

        log.info("Print integration events");
        var kafkaConsumer = createKafkaConsumer(KafkaTopicsConfig.TOPIC_ORDER_EVENTS);
        List<String> kafkaRecordValues = getKafkaRecords(kafkaConsumer, Duration.ofSeconds(10), 23);
        assertThat(kafkaRecordValues)
                .hasSizeGreaterThanOrEqualTo(23);

        String lastKafkaRecordValue = kafkaRecordValues.get(kafkaRecordValues.size() - 1);
        assertThat(jsonTester.from(lastKafkaRecordValue)).isEqualToJson("""
                {
                  "order_id":"%s",
                  "event_type":"ORDER_COMPLETED",
                  "version":23,
                  "status":"COMPLETED",
                  "rider_id":"63770803-38f4-4594-aec2-4c74918f7165",
                  "price":300.00,
                  "route":[
                    {
                      "lat":50.51980052414157,
                      "lon":30.467197278948536,
                      "address":"Kyiv, 17A Polyarna Street"
                    },
                    {
                      "lat":50.48509161169076,
                      "lon":30.485170724431292,
                      "address":"Kyiv, 18V Novokostyantynivska Street"
                    }
                  ],
                  "driver_id":"2c068a1a-9263-433f-a70b-067d51b98378"
                }
                """.formatted(orderId));
    }

    private UUID placeOrder(String body) throws JsonProcessingException {
        ResponseEntity<String> response = this.restTemplate.exchange(
                "/orders",
                HttpMethod.POST,
                new HttpEntity<>(body, HEADERS),
                String.class);

        assertThat(response.getStatusCode().is2xxSuccessful())
                .isTrue();

        String jsonString = response.getBody();
        assertThat(jsonTester.from(jsonString))
                .extractingJsonPathStringValue("@.orderId")
                .isNotEmpty();

        JsonNode jsonTree = objectMapper.readTree(jsonString);
        String orderId = jsonTree.get("orderId").asText();
        return UUID.fromString(orderId);
    }

    private void modifyOrder(UUID orderId, String body) {
        ResponseEntity<String> response = this.restTemplate.exchange(
                "/orders/" + orderId,
                HttpMethod.PUT,
                new HttpEntity<>(body, HEADERS),
                String.class);

        assertThat(response.getStatusCode().is2xxSuccessful())
                .isTrue();
    }

    private void modifyOrderError(UUID orderId, String body, String error) {
        ResponseEntity<String> response = this.restTemplate.exchange(
                "/orders/" + orderId,
                HttpMethod.PUT,
                new HttpEntity<>(body, HEADERS),
                String.class);

        assertThat(response.getStatusCode().is4xxClientError())
                .isTrue();

        String jsonString = response.getBody();
        assertThat(jsonTester.from(jsonString))
                .extractingJsonPathStringValue("@.error")
                .isEqualTo(error);
    }

    private void getOrder(UUID orderId, String expectedJson) {
        ResponseEntity<String> response = this.restTemplate.exchange(
                "/orders/" + orderId,
                HttpMethod.GET,
                new HttpEntity<>(HEADERS),
                String.class
        );

        assertThat(response.getStatusCode().is2xxSuccessful())
                .isTrue();

        String jsonString = response.getBody();
        assertThat(jsonTester.from(jsonString))
                .isEqualToJson(expectedJson);
    }

    private Consumer<String, String> createKafkaConsumer(String... topicsToConsume) {
        var consumerProps = KafkaTestUtils.consumerProps(
                kafkaBrokers,
                this.getClass().getSimpleName() + "-consumer",
                "true"
        );
        var cf = new DefaultKafkaConsumerFactory<>(
                consumerProps,
                new StringDeserializer(),
                new StringDeserializer()
        );
        Consumer<String, String> consumer = cf.createConsumer();
        consumer.subscribe(Arrays.asList(topicsToConsume));
        return consumer;
    }

    private List<String> getKafkaRecords(Consumer<String, String> consumer,
                                         Duration timeout,
                                         int minRecords) {
        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer, timeout, minRecords);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(records.iterator(), Spliterator.ORDERED), false)
                .sorted(Comparator.comparingLong(ConsumerRecord::timestamp))
                .map(ConsumerRecord::value)
                .toList();
    }
}
