package fintech;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.openjdk.jmh.annotations.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(value = {Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class LoadBalancingMultipleBenchmark {
    private List<KafkaProducer<String, String>> kafkaProducers = new ArrayList<>();
    private List<KafkaConsumer<String, String>> kafkaConsumers = new ArrayList<>();
    private List<RabbitMQProducer> rabbitProducers = new ArrayList<>();
    private List<RabbitMQConsumer> rabbitConsumers = new ArrayList<>();

    private long totalProducerLatencyKafka = 0;
    private long totalConsumerProcessingTimeKafka = 0;
    private long totalProducerLatencyRabbit = 0;
    private long totalConsumerProcessingTimeRabbit = 0;
    private long totalDeliveryDelayKafka = 0;
    private long totalDeliveryDelayRabbit = 0;

    @Setup(Level.Trial)
    public void setup() {
        Properties kafkaProducerProps = new Properties();
        kafkaProducerProps.put("bootstrap.servers", "localhost:9092");
        kafkaProducerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Properties kafkaConsumerProps = new Properties();
        kafkaConsumerProps.put("bootstrap.servers", "localhost:9092");
        kafkaConsumerProps.put("group.id", "test-group");
        kafkaConsumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConsumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        for (int i = 0; i < 3; i++) {
            kafkaProducers.add(new KafkaProducer<>(kafkaProducerProps));
            kafkaConsumers.add(new KafkaConsumer<>(kafkaConsumerProps));
            rabbitProducers.add(new RabbitMQProducer());
            rabbitConsumers.add(new RabbitMQConsumer());
        }
    }

    @Benchmark
    public void testKafkaLoadBalancingMultipleConsumers() {
        long startTime = System.currentTimeMillis();
        for (KafkaProducer<String, String> producer : kafkaProducers) {
            producer.send(new ProducerRecord<>("test-topic", "key", "message"));
        }
        long producerLatency = System.currentTimeMillis() - startTime;
        totalProducerLatencyKafka += producerLatency;

        long consumerStartTime = System.currentTimeMillis();
        for (KafkaConsumer<String, String> consumer : kafkaConsumers) {
            consumer.subscribe(Collections.singletonList("test-topic"));
            consumer.poll(Duration.ofMillis(1000));
        }
        long consumerProcessingTime = System.currentTimeMillis() - consumerStartTime;
        totalConsumerProcessingTimeKafka += consumerProcessingTime;
    }

    @Benchmark
    public void testRabbitMQLoadBalancingMultipleConsumers() {
        long startTime = System.currentTimeMillis();
        for (RabbitMQProducer producer : rabbitProducers) {
            producer.send("message");
        }
        long producerLatency = System.currentTimeMillis() - startTime;
        totalProducerLatencyRabbit += producerLatency;

        long consumerStartTime = System.currentTimeMillis();
        for (RabbitMQConsumer consumer : rabbitConsumers) {
            consumer.consume();
        }
        long consumerProcessingTime = System.currentTimeMillis() - consumerStartTime;
        totalConsumerProcessingTimeRabbit += consumerProcessingTime;
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("LoadBalancingMultipleBenchmark_report.txt", true))) {
            writer.write("Benchmark, Mode, Throughput (ops/s), Latency (ms), Consumer Processing Time (ms), Message Delivery Delay (ms)\n");

            writer.write(String.format("testKafkaLoadBalancingMultipleConsumers, %s, %.2f, %.2f, %.2f" + "\n",
                    Mode.AverageTime,
                    (double) totalProducerLatencyKafka / kafkaProducers.size(),
                    (double) totalConsumerProcessingTimeKafka / kafkaConsumers.size(),
                    (double) totalDeliveryDelayKafka / kafkaProducers.size()));

            writer.write(String.format("testRabbitMQLoadBalancingMultipleConsumers, %s, %.2f, %.2f, %.2f" + "\n",
                    Mode.AverageTime,
                    (double) totalProducerLatencyRabbit / rabbitProducers.size(),
                    (double) totalConsumerProcessingTimeRabbit / rabbitConsumers.size(),
                    (double) totalDeliveryDelayRabbit / rabbitProducers.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
