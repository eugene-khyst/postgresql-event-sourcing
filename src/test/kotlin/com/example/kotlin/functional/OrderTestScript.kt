package com.example.kotlin.functional

import com.example.eventsourcing.config.Json.objectMapper
import com.example.eventsourcing.config.Kafka.TOPIC_ORDER_EVENTS
import com.fasterxml.jackson.core.JsonProcessingException
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.assertj.core.api.Assertions
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.slf4j.LoggerFactory
import org.springframework.boot.test.json.BasicJsonTester
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.test.utils.KafkaTestUtils
import java.math.BigDecimal
import java.time.Duration
import java.util.*
import java.util.stream.StreamSupport

class OrderTestScript(
    private val httpHandler: HttpHandler,
    private val kafkaBrokers: String
) {
    private val jsonTester = BasicJsonTester(javaClass)

    fun execute() {
        log.info("Place a new order")
        val orderId = placeOrder(
            """
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
                
                """.trimIndent()
        )
        log.info("Get the placed order")
        getOrder(
            orderId, """
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
                
                """.trimIndent().format(orderId)
        )
        var price = BigDecimal("100.00")
        for (i in 0..19) {
            price = price.add(BigDecimal("10"))
            log.info("Adjust the order")
            modifyOrder(
                orderId, """
                    {
                      "status":"ADJUSTED",
                      "price":"%s"
                    }
                    
                    """.trimIndent().format(price)
            )
        }
        log.info("Get the adjusted order")
        getOrder(
            orderId, """
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
                
                """.trimIndent().format(orderId)
        )
        log.info("Accepted the order")
        modifyOrder(
            orderId, """
                {
                  "status":"ACCEPTED",
                  "driverId":"2c068a1a-9263-433f-a70b-067d51b98378"
                }
                
                """.trimIndent()
        )
        log.info("Get the accepted order")
        getOrder(
            orderId, """
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
                
                """.trimIndent().format(orderId)
        )
        log.info("Complete the order")
        modifyOrder(
            orderId, """
                {
                  "status":"COMPLETED"
                }
                
                """.trimIndent()
        )
        log.info("Get the completed order")
        getOrder(
            orderId, """
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
                
                """.trimIndent().format(orderId)
        )
        log.info("Try to cancel the completed order")
        modifyOrderError(
            orderId, """
                {
                  "status":"CANCELLED"
                }
                
                """.trimIndent(), "Order in status COMPLETED can't be cancelled"
        )
        log.info("Print integration events")
        val kafkaConsumer = createKafkaConsumer(TOPIC_ORDER_EVENTS)
        val kafkaRecordValues = getKafkaRecords(kafkaConsumer, Duration.ofSeconds(10), 23)
        Assertions.assertThat(kafkaRecordValues)
            .hasSizeGreaterThanOrEqualTo(23)
        val lastKafkaRecordValue = kafkaRecordValues[kafkaRecordValues.size - 1]
        Assertions.assertThat(jsonTester.from(lastKafkaRecordValue)).isEqualToJson(
            """
                {
                  "orderId":"%s",
                  "eventType":"ORDER_COMPLETED",
                  "version":23,
                  "status":"COMPLETED",
                  "riderId":"63770803-38f4-4594-aec2-4c74918f7165",
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
                  "driverId":"2c068a1a-9263-433f-a70b-067d51b98378"
                }
                
                """.trimIndent().format(orderId)
        )
    }

    private fun placeOrder(body: String): UUID {
        val response = httpHandler(
            Request(Method.POST, "/orders")
                .json()
                .body(body)
        )
        Assertions.assertThat(response.status.successful)
            .isTrue()
        val jsonString = response.bodyString()
        Assertions.assertThat(jsonTester.from(jsonString))
            .extractingJsonPathStringValue("@.orderId")
            .isNotEmpty()
        return try {
            val jsonTree = objectMapper.readTree(jsonString)
            val orderId = jsonTree["orderId"].asText()
            UUID.fromString(orderId)
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }

    private fun modifyOrder(orderId: UUID, body: String) {
        val response = httpHandler(
            Request(Method.PUT, "/orders/$orderId")
                .json()
                .body(body)
        )
        Assertions.assertThat(response.status.successful)
            .isTrue()
    }

    private fun modifyOrderError(orderId: UUID, body: String, error: String) {
        val response = httpHandler(
            Request(Method.PUT, "/orders/$orderId")
                .json()
                .body(body)
        )
        Assertions.assertThat(response.status.clientError)
            .isTrue()
        val jsonString = response.bodyString()
        Assertions.assertThat(jsonTester.from(jsonString))
            .extractingJsonPathStringValue("@.error")
            .isEqualTo(error)
    }

    private fun getOrder(orderId: UUID, expectedJson: String) {
        val response = httpHandler(
            Request(Method.GET, "/orders/$orderId")
                .json()
        )
        Assertions.assertThat(response.status.successful)
            .isTrue()
        val jsonString = response.bodyString()
        Assertions.assertThat(jsonTester.from(jsonString))
            .isEqualToJson(expectedJson)
    }

    private fun createKafkaConsumer(vararg topicsToConsume: String): Consumer<String, String> {
        val consumerProps = KafkaTestUtils.consumerProps(
            kafkaBrokers,
            this.javaClass.simpleName + "-consumer",
            "true"
        )
        val cf = DefaultKafkaConsumerFactory(
            consumerProps,
            StringDeserializer(),
            StringDeserializer()
        )
        return cf.createConsumer().also {
            it.subscribe(listOf(*topicsToConsume))
        }
    }

    private fun getKafkaRecords(
        consumer: Consumer<String, String>,
        timeout: Duration,
        minRecords: Int
    ): List<String> {
        val records = KafkaTestUtils.getRecords(consumer, timeout, minRecords)
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(records.iterator(), Spliterator.ORDERED), false)
            .sorted(Comparator.comparingLong { obj: ConsumerRecord<String, String> -> obj.timestamp() })
            .map { obj: ConsumerRecord<String, String> -> obj.value() }
            .toList()
    }

    companion object {
        fun Request.json() =
            header("content-type", "application/json")
                .header("accept", "application/json")

        private val log = LoggerFactory.getLogger(OrderTestScript::class.java)
    }
}
