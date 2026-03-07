package com.example.LLD.PubSubOffset;
import com.example.LLD.PubSubOffset.models.Message;
import com.example.LLD.PubSubOffset.models.Subscriber;
import com.example.LLD.PubSubOffset.models.Topic;
import com.example.LLD.PubSubOffset.service.PubSubService;

/**
 * Driver class demonstrating the multi-threaded Pub-Sub system.
 *
 * Scenario:
 * - 2 topics: "sports" and "tech"
 * - 3 subscribers: sub1 → sports, sub2 → tech, sub3 → both
 * - Multiple messages published to each topic
 * - Output shows interleaved, multi-threaded consumption
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {

        // 1. Create the Pub-Sub service
        PubSubService pubSubService = new PubSubService();

        // 2. Create topics
        Topic sportsTopic = pubSubService.createTopic("sports");
        Topic techTopic = pubSubService.createTopic("tech");

        // 3. Create subscribers
        Subscriber sub1 = new Subscriber("sub1");
        Subscriber sub2 = new Subscriber("sub2");
        Subscriber sub3 = new Subscriber("sub3");

        // 4. Subscribe
        // sub1 → sports only
        // sub2 → tech only
        // sub3 → both sports & tech
        pubSubService.subscribe(sub1, sportsTopic);
        pubSubService.subscribe(sub2, techTopic);
        pubSubService.subscribe(sub3, sportsTopic);
        pubSubService.subscribe(sub3, techTopic);

        // 5. Publish messages
        pubSubService.publish(sportsTopic, new Message("India won the cricket match!"));
        pubSubService.publish(techTopic, new Message("Java 21 released with virtual threads"));
        pubSubService.publish(sportsTopic, new Message("FIFA World Cup 2026 schedule announced"));
        pubSubService.publish(techTopic, new Message("OpenAI launches GPT-5"));
        pubSubService.publish(sportsTopic, new Message("Virat Kohli scores century"));

        // 6. Give consumer threads time to process all messages
        Thread.sleep(2000);

        System.out.println("\n--- All messages processed ---");

        // 7. Shutdown the service
        pubSubService.shutdown();
    }
}
